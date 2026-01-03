"use client";

import { ThemeProvider as NextThemesProvider } from "next-themes";
import { QueryProvider } from "@/lib/providers/QueryProvider";

export function Providers({ children }: { children: React.ReactNode }) {
    return (
        <QueryProvider>
            <NextThemesProvider
                attribute="class"
                defaultTheme="system"
                enableSystem
                disableTransitionOnChange
            >
                {children}
            </NextThemesProvider>
        </QueryProvider>
    );
}
