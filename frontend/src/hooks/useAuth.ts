"use client";

import { useState, useEffect } from "react";
import { User, authService, RegisterRequest } from "@/lib/auth";
import { useRouter } from "next/navigation";
import { usePreferences } from "./usePreferences";

export function useAuth() {
  const [user, setUser] = useState<User | null>(() => {
    if (typeof window !== "undefined") {
      return authService.getCurrentUser();
    }
    return null;
  });
  const [loading] = useState(false);
  const router = useRouter();
  const { applyStoredPreferences } = usePreferences();

  useEffect(() => {
    // Session initialization logic if needed
  }, []);

  const login = async (usernameOrEmail: string, password: string) => {
    const data = await authService.login(usernameOrEmail, password);
    setUser(data.user);
    applyStoredPreferences(data.user);
    return data;
  };

  const register = async (data: RegisterRequest) => {
    return await authService.register(data);
  };

  const logout = async () => {
    await authService.logout();
    setUser(null);
    router.replace("/login");
  };

  const hasPermission = (permission: string) => {
    return user?.permissions.includes(permission) || false;
  };

  return {
    user,
    loading,
    login,
    register,
    logout,
    isAuthenticated: !!user,
    hasPermission,
  };
}
