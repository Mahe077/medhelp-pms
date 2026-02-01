# Rate Limiting Implementation Plan

## 1. Objective

To protect the Pharmacy Management System API from abuse, prevent brute-force attacks, and ensure high availability and fair usage for all clients. This plan aligns with the principles defined in `API_contracts.md` and the architecture outlined in `architecture.md`.

## 2. Location of Implementation

The rate-limiting logic will be implemented as a middleware within the **API Gateway / BFF layer**. This centralizes the control, simplifies module logic, and ensures that every request is checked before hitting the application layer.

## 3. Technology Stack

-   **Storage**: **Redis**. As per the architecture document, Redis is the ideal choice for its high performance in handling atomic increments and expiring keys, which are essential for rate limiting.
-   **Algorithm**: **Sliding Window Counter**. This algorithm provides a smooth rate limit over a moving time window, offering a good balance between performance and accuracy, and preventing bursts of traffic at the window's edge.

## 4. Client Identification Strategy

To apply fair limits, requests will be identified using the following methods in order of priority:

1.  **Authenticated User**: For logged-in users, the unique identifier will be the **User ID** extracted from the validated JWT token.
2.  **Anonymous User**: For unauthenticated requests, the unique identifier will be the client's **IP Address**.

## 5. Rate Limiting Tiers & Policies

We will define several tiers of limits to apply different policies based on the request's context. These will be configurable via environment variables to allow for easy adjustments without code changes.

### Tier 1: Strict - Sensitive Write/Auth Operations

-   **Endpoints**:
    -   `POST /api/v1/auth/login`
    -   `POST /api/v1/auth/refresh`
    -   `POST /api/v1/auth/change-password`
    -   `POST /api/v1/users` (User creation)
-   **Identifier**: IP Address
-   **Proposed Limit**: 10 requests per minute.
-   **Reason**: Protects against credential stuffing, brute-force login attempts, and user enumeration.

### Tier 2: Authenticated User - General API Access

-   **Endpoints**: All endpoints for a logged-in user not covered by other tiers.
-   **Identifier**: User ID from JWT.
-   **Proposed Limit**: 200 requests per minute.
-   **Reason**: A generous limit for regular application usage by a known user.

### Tier 3: Anonymous User - General API Access

-   **Endpoints**: All `GET` endpoints accessible to the public.
-   **Identifier**: IP Address.
-   **Proposed Limit**: 60 requests per minute.
-   **Reason**: Allows reasonable access for unauthenticated exploration (e.g., checking medication info) while protecting against scraping.

## 6. API Response on Rate Limit Exceeded

When a client exceeds the defined rate limit, the API Gateway will immediately stop processing the request and respond with:

-   **Status Code**: `429 Too Many Requests`
-   **Headers**:
    -   `X-RateLimit-Limit`: The total number of requests allowed in the current window.
    -   `X-RateLimit-Remaining`: The number of requests remaining in the current window.
    -   `X-RateLimit-Reset`: A UTC timestamp indicating when the rate limit will reset.
-   **Body**:
    ```json
    {
      "success": false,
      "error": {
        "code": "TOO_MANY_REQUESTS",
        "message": "Rate limit exceeded. Please try again later."
      },
      "meta": { ... }
    }
    ```

## 7. High-Level Implementation Steps

1.  **Integrate Redis Client**: Add a Redis client library to the API Gateway service and establish a connection.
2.  **Develop Rate Limiter Middleware**: Create a middleware function (e.g., for Express/NestJS) that will be executed on incoming requests.
3.  **Implement Identification Logic**: Inside the middleware, extract the User ID from the JWT payload. If no token is present, fall back to using the request's IP address.
4.  **Implement Sliding Window Algorithm**:
    -   Use Redis `ZSET`s (Sorted Sets) for a precise sliding window or a simpler `INCR` with `EXPIRE` for a fixed-window approach as a starting point.
    -   The key should be constructed from the identifier and the endpoint/tier (e.g., `ratelimit:{userId}:{endpoint_group}`).
    -   On each request, increment the count for the current time window.
5.  **Apply Tier Logic**: The middleware will determine which limit tier to apply based on the request's endpoint path and authentication status.
6.  **Check and Respond**:
    -   Compare the current request count against the configured limit for that tier.
    -   If the limit is exceeded, construct and send the `429` response with appropriate headers.
    -   If the limit is not exceeded, attach the rate limit headers to the response and pass the request to the next middleware.
7.  **Configuration**: Store rate limit values (e.g., `RATE_LIMIT_AUTH_USER=200`, `RATE_LIMIT_LOGIN_ATTEMPT=10`) in environment variables.
8.  **Testing**:
    -   Write unit tests for the rate-limiting logic.
    -   Write integration tests to ensure the middleware correctly blocks requests and returns the correct headers and status code.

## 8. Proposed Implementation with Resilience4j

Given the project's Java and Spring Boot foundation, `Resilience4j-RateLimiter` is the recommended library. It can be implemented as follows:

### Configuration (`application.yml`)

We will define our rate limiting tiers in the application configuration.

```yaml
resilience4j.ratelimiter:
  instances:
    # Tier 1: Strict (e.g., Login)
    auth-strict:
      limitForPeriod: 10
      limitRefreshPeriod: 1m
      timeoutDuration: 0
    # Tier 2: Authenticated User
    user-default:
      limitForPeriod: 200
      limitRefreshPeriod: 1m
      timeoutDuration: 0
    # Tier 3: Anonymous User
    anonymous-default:
      limitForPeriod: 60
      limitRefreshPeriod: 1m
      timeoutDuration: 0
```

### Implementation Strategy

1.  **Spring Boot Integration**: Add the `resilience4j-spring-boot2` and `spring-boot-starter-aop` dependencies to the `pom.xml`.
2.  **Key Resolver**: Create a custom `KeyResolver` bean that implements the client identification strategy. It will inspect the `HttpServletRequest` to:
    -   Extract the User ID from the JWT `Authorization` header.
    -   Fall back to the client's IP address (`X-Forwarded-For` or `getRemoteAddr()`) if the user is not authenticated.
3.  **Applying Rate Limits**: Use the `@RateLimiter` annotation on controller methods or, preferably, on a `WebFilter` for a more centralized approach. The annotation will specify which rate limiter configuration to use (e.g., `name = "auth-strict"`).
4.  **Exception Handling**: Create a global `@RestControllerAdvice` to catch the `RequestNotPermitted` exception thrown by Resilience4j when a limit is exceeded. This handler will be responsible for returning the standard `429 Too Many Requests` JSON response and headers.

### In-Memory vs. Distributed (Redis)

-   **Initial Approach (In-Memory)**: Per the modular monolith architecture, we will start with Resilience4j's default in-memory rate limiter. This is simple, fast, and sufficient for a single-instance deployment.
-   **Scaling (Distributed)**: If the application needs to be scaled horizontally (multiple instances), the in-memory approach will become inconsistent. At that stage, we will need to implement a distributed rate limiter. This will involve:
    -   Using a library like `resilience4j-ratelimiter-redis` or a custom implementation.
    -   Configuring Resilience4j to use Redis as its backend to ensure that all instances share the same rate-limiting counters. This aligns with the architecture document's mention of using Redis for rate limiting.

## 9. Future Considerations

-   **Dynamic Limits**: Implement a system where limits can be adjusted dynamically without restarting the service, possibly via a configuration service or an admin API endpoint.
-   **Granular Endpoint Limiting**: Allow configuration for specific endpoints (`GET /patients/{id}`) in addition to tiered groups.
-   **WAF Integration**: For production environments, consider integrating with a Web Application Firewall (WAF) for an additional layer of protection against DDoS and other threats.
-   **Soft Limiting**: Introduce a "soft" limit that logs a warning or triggers an alert before the "hard" limit is reached, allowing for proactive monitoring.
