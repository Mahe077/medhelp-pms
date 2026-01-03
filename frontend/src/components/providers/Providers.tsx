"use client";

import { ThemeProvider as NextThemesProvider } from "next-themes";
import { QueryProvider } from "@/lib/providers/QueryProvider";

import { AuthInitializer } from "./AuthInitializer";

export function Providers({ children, enableAuthGuard = true }: { children: React.ReactNode; enableAuthGuard?: boolean }) {
    return (
        <QueryProvider>
            <NextThemesProvider
                attribute="class"
                defaultTheme="system"
                enableSystem
                disableTransitionOnChange
            >
                {enableAuthGuard ? (
                    <AuthInitializer>
                        {children}
                    </AuthInitializer>
                ) : (
                    children
                )}
            </NextThemesProvider>
        </QueryProvider>
    );
}
