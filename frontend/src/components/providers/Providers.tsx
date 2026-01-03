"use client";

import { ThemeProvider as NextThemesProvider } from "next-themes";
import { QueryProvider } from "@/lib/providers/QueryProvider";

import { AuthInitializer } from "./AuthInitializer";

export function Providers({ children }: { children: React.ReactNode }) {
    return (
        <QueryProvider>
            <NextThemesProvider
                attribute="class"
                defaultTheme="system"
                enableSystem
                disableTransitionOnChange
            >
                <AuthInitializer>
                    {children}
                </AuthInitializer>
            </NextThemesProvider>
        </QueryProvider>
    );
}
