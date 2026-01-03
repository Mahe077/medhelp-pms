"use client";

import { useEffect } from "react";
import { authService } from "@/lib/auth";
import { useRouter, usePathname } from "@/i18n/routing";
import { usePreferences } from "@/hooks/usePreferences";

export function AuthInitializer({ children }: { children: React.ReactNode }) {
    const router = useRouter();
    const pathname = usePathname();
    const { applyStoredPreferences } = usePreferences();

    useEffect(() => {
        const checkAuth = async () => {
            if (authService.isAuthenticated()) {
                try {
                    const user = await authService.validateToken();
                    applyStoredPreferences(user);
                    // If on login/register and authenticated, redirect to dashboard
                    if (pathname === "/login" || pathname === "/register" || pathname === "/") {
                        router.replace("/dashboard");
                    }
                } catch {
                    authService.logout();
                    if (pathname !== "/login" && pathname !== "/register" && pathname !== "/verify-email") {
                        router.replace("/login");
                    }
                }
            } else {
                // Not authenticated, redirect to login if not on public pages
                if (pathname !== "/login" && pathname !== "/register" && pathname !== "/verify-email") {
                    router.replace("/login");
                }
            }
        };

        checkAuth();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [pathname]);

    return <>{children}</>;
}
