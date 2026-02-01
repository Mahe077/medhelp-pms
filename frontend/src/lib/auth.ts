"use client";

export type User = {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
  role: string;
  userType: "INTERNAL" | "EXTERNAL";
  isActive: boolean;
  isEmailVerified: boolean;
  preferredLanguage: string;
  preferredTheme: string;
  permissions: string[];
};

export type RegisterRequest = {
  username: string;
  email: string;
  password?: string;
  firstName: string;
  lastName: string;
  phone?: string;
};

export type RegisterResponse = {
  message: string;
  user: User;
};

export type AuthResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
};

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_URL || "http://localhost:8000/api/v1";

export const authService = {
  async login(
    usernameOrEmail: string,
    password: string
  ): Promise<AuthResponse> {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ usernameOrEmail, password }),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Login failed");
    }

    const data = await response.json();
    if (data.success) {
      localStorage.setItem("accessToken", data.data.accessToken);
      localStorage.setItem("refreshToken", data.data.refreshToken);
      localStorage.setItem("user", JSON.stringify(data.data.user));
      return data.data;
    }
    throw new Error(data.message || "Login failed");
  },

  async register(data: RegisterRequest): Promise<RegisterResponse> {
    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Registration failed");
    }

    const result = await response.json();
    return result.data;
  },

  async validateToken(): Promise<User> {
    const token = this.getToken();
    if (!token) throw new Error("No token");

    const response = await fetch(`${API_BASE_URL}/auth/me`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error("Token invalid");
    }

    const data = await response.json();
    if (data.success) {
      localStorage.setItem("user", JSON.stringify(data.data));
      return data.data;
    }
    throw new Error("Token validation failed");
  },

  async updatePreferences(preferences: {
    preferredLanguage?: string;
    preferredTheme?: string;
  }): Promise<User> {
    const token = this.getToken();
    if (!token) throw new Error("No token");

    const response = await fetch(`${API_BASE_URL}/auth/preferences`, {
      method: "PATCH",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(preferences),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Failed to update preferences");
    }

    const data = await response.json();
    if (data.success) {
      localStorage.setItem("user", JSON.stringify(data.data));
      return data.data;
    }
    throw new Error("Update preferences failed");
  },

  async logout() {
    const token = this.getToken();
    if (token) {
      try {
        await fetch(`${API_BASE_URL}/auth/logout`, {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });
      } catch (error) {
        console.error("Logout failed on server:", error);
      }
    }
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("user");
  },

  getCurrentUser(): User | null {
    if (typeof window === "undefined") return null;
    const user = localStorage.getItem("user");
    return user ? JSON.parse(user) : null;
  },

  getToken(): string | null {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("accessToken");
  },

  isAuthenticated(): boolean {
    return !!this.getToken();
  },
};
