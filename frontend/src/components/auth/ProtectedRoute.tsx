"use client";

import { useAuth } from "@/hooks/useAuth";
import { useRouter, usePathname } from "@/i18n/routing";
import { useEffect } from "react";
import { Loader2 } from "lucide-react";

interface ProtectedRouteProps {
    children: React.ReactNode;
    requiredPermission?: string;
}

export function ProtectedRoute({ children, requiredPermission }: ProtectedRouteProps) {
    const { loading, isAuthenticated, hasPermission } = useAuth();
    const router = useRouter();
    const pathname = usePathname();

    useEffect(() => {
        if (!loading && !isAuthenticated) {
            router.replace(`/login?redirect=${pathname}`);
        } else if (!loading && requiredPermission && !hasPermission(requiredPermission)) {
            router.replace("/unauthorized");
        }
    }, [loading, isAuthenticated, requiredPermission, hasPermission, router, pathname]);

    if (loading) {
        return (
            <div className="flex h-screen w-full items-center justify-center bg-background">
                <Loader2 className="h-10 w-10 animate-spin text-primary" />
            </div>
        );
    }

    if (!isAuthenticated || (requiredPermission && !hasPermission(requiredPermission))) {
        return null;
    }

    return <>{children}</>;
}
