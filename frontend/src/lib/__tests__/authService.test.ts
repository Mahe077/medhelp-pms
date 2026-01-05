/**
 * @jest-environment jsdom
 */
import { authService } from "../auth";

// Mock fetch
const mockFetch = jest.fn();
global.fetch = mockFetch;

// Mock localStorage
const mockLocalStorage = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
};
Object.defineProperty(window, "localStorage", {
  value: mockLocalStorage,
});

describe("authService", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockLocalStorage.getItem.mockClear();
    mockLocalStorage.setItem.mockClear();
    mockLocalStorage.removeItem.mockClear();
  });

  describe("login", () => {
    const mockAuthResponse = {
      success: true,
      data: {
        accessToken: "test-access-token",
        refreshToken: "test-refresh-token",
        tokenType: "Bearer",
        expiresIn: 3600,
        user: {
          id: "123",
          username: "testuser",
          email: "test@example.com",
          firstName: "Test",
          lastName: "User",
          role: "USER",
          userType: "EXTERNAL" as const,
          isActive: true,
          isEmailVerified: true,
          preferredLanguage: "en",
          preferredTheme: "dark",
          permissions: ["READ_PROFILE"],
        },
      },
    };

    it("should successfully login and store tokens in localStorage", async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockAuthResponse,
      });

      const result = await authService.login("testuser", "password123");

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining("/auth/login"),
        expect.objectContaining({
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            usernameOrEmail: "testuser",
            password: "password123",
          }),
        })
      );

      expect(mockLocalStorage.setItem).toHaveBeenCalledWith(
        "accessToken",
        "test-access-token"
      );
      expect(mockLocalStorage.setItem).toHaveBeenCalledWith(
        "refreshToken",
        "test-refresh-token"
      );
      expect(mockLocalStorage.setItem).toHaveBeenCalledWith(
        "user",
        JSON.stringify(mockAuthResponse.data.user)
      );

      expect(result).toEqual(mockAuthResponse.data);
    });

    it("should throw error for invalid credentials", async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        json: async () => ({ message: "Invalid credentials" }),
      });

      await expect(
        authService.login("testuser", "wrongpassword")
      ).rejects.toThrow("Invalid credentials");
    });

    it("should throw error when response success is false", async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ success: false, message: "User not found" }),
      });

      await expect(authService.login("testuser", "password")).rejects.toThrow(
        "User not found"
      );
    });
  });

  describe("register", () => {
    const mockRegisterRequest = {
      username: "newuser",
      email: "newuser@example.com",
      password: "Password123!",
      firstName: "New",
      lastName: "User",
    };

    const mockRegisterResponse = {
      success: true,
      data: {
        message: "Registration successful",
        user: {
          id: "456",
          username: "newuser",
          email: "newuser@example.com",
          firstName: "New",
          lastName: "User",
        },
      },
    };

    it("should successfully register a new user", async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockRegisterResponse,
      });

      const result = await authService.register(mockRegisterRequest);

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining("/auth/register"),
        expect.objectContaining({
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(mockRegisterRequest),
        })
      );

      expect(result).toEqual(mockRegisterResponse.data);
    });

    it("should throw error for duplicate email", async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        json: async () => ({ message: "Email already exists" }),
      });

      await expect(authService.register(mockRegisterRequest)).rejects.toThrow(
        "Email already exists"
      );
    });
  });

  describe("validateToken", () => {
    const mockUser = {
      id: "123",
      username: "testuser",
      email: "test@example.com",
      firstName: "Test",
      lastName: "User",
    };

    it("should validate token and return user data", async () => {
      mockLocalStorage.getItem.mockReturnValue("valid-token");
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ success: true, data: mockUser }),
      });

      const result = await authService.validateToken();

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining("/auth/me"),
        expect.objectContaining({
          method: "GET",
          headers: expect.objectContaining({
            Authorization: "Bearer valid-token",
          }),
        })
      );

      expect(mockLocalStorage.setItem).toHaveBeenCalledWith(
        "user",
        JSON.stringify(mockUser)
      );
      expect(result).toEqual(mockUser);
    });

    it("should throw error when no token exists", async () => {
      mockLocalStorage.getItem.mockReturnValue(null);

      await expect(authService.validateToken()).rejects.toThrow("No token");
    });

    it("should throw error for invalid token", async () => {
      mockLocalStorage.getItem.mockReturnValue("invalid-token");
      mockFetch.mockResolvedValueOnce({
        ok: false,
        json: async () => ({}),
      });

      await expect(authService.validateToken()).rejects.toThrow(
        "Token invalid"
      );
    });
  });

  describe("logout", () => {
    it("should clear all auth data from localStorage", async () => {
      await authService.logout();

      expect(mockLocalStorage.removeItem).toHaveBeenCalledWith("accessToken");
      expect(mockLocalStorage.removeItem).toHaveBeenCalledWith("refreshToken");
      expect(mockLocalStorage.removeItem).toHaveBeenCalledWith("user");
    });
  });

  describe("getCurrentUser", () => {
    it("should return user from localStorage when available", () => {
      const mockUser = { id: "123", username: "testuser" };
      mockLocalStorage.getItem.mockReturnValue(JSON.stringify(mockUser));

      const result = authService.getCurrentUser();

      expect(mockLocalStorage.getItem).toHaveBeenCalledWith("user");
      expect(result).toEqual(mockUser);
    });

    it("should return null when no user in localStorage", () => {
      mockLocalStorage.getItem.mockReturnValue(null);

      const result = authService.getCurrentUser();

      expect(result).toBeNull();
    });
  });

  describe("getToken", () => {
    it("should return token from localStorage", () => {
      mockLocalStorage.getItem.mockReturnValue("test-token");

      const result = authService.getToken();

      expect(mockLocalStorage.getItem).toHaveBeenCalledWith("accessToken");
      expect(result).toBe("test-token");
    });

    it("should return null when no token exists", () => {
      mockLocalStorage.getItem.mockReturnValue(null);

      const result = authService.getToken();

      expect(result).toBeNull();
    });
  });

  describe("isAuthenticated", () => {
    it("should return true when token exists", () => {
      mockLocalStorage.getItem.mockReturnValue("test-token");

      const result = authService.isAuthenticated();

      expect(result).toBe(true);
    });

    it("should return false when no token exists", () => {
      mockLocalStorage.getItem.mockReturnValue(null);

      const result = authService.isAuthenticated();

      expect(result).toBe(false);
    });
  });

  describe("updatePreferences", () => {
    it("should update user preferences and return updated user", async () => {
      const mockUpdatedUser = {
        id: "123",
        username: "testuser",
        preferredLanguage: "si",
        preferredTheme: "light",
      };

      mockLocalStorage.getItem.mockReturnValue("valid-token");
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ success: true, data: mockUpdatedUser }),
      });

      const result = await authService.updatePreferences({
        preferredLanguage: "si",
        preferredTheme: "light",
      });

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining("/auth/preferences"),
        expect.objectContaining({
          method: "PATCH",
          headers: expect.objectContaining({
            Authorization: "Bearer valid-token",
          }),
          body: JSON.stringify({
            preferredLanguage: "si",
            preferredTheme: "light",
          }),
        })
      );

      expect(mockLocalStorage.setItem).toHaveBeenCalledWith(
        "user",
        JSON.stringify(mockUpdatedUser)
      );
      expect(result).toEqual(mockUpdatedUser);
    });
  });
});
