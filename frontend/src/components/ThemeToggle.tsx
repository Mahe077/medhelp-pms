"use client";

import * as React from "react";
import { Moon, Sun, Monitor } from "lucide-react";
import { usePreferences } from "@/hooks/usePreferences";
import { Button } from "@/components/ui/Button";

export function ThemeToggle() {
    const { currentTheme, updatePreferences } = usePreferences();
    const [mounted, setMounted] = React.useState(false);

    React.useEffect(() => {
        setMounted(true);
    }, []);

    if (!mounted) {
        return <Button variant="ghost" size="icon" className="w-9 h-9" />;
    }

    return (
        <div className="flex items-center gap-1 rounded-full border border-border/50 bg-card/50 p-1 backdrop-blur-md">
            <Button
                variant={currentTheme === "light" ? "secondary" : "ghost"}
                size="icon"
                className="h-8 w-8 rounded-full"
                onClick={() => updatePreferences({ theme: "light" })}
                title="Light Mode"
            >
                < Sun className="h-4 w-4" />
            </Button>
            <Button
                variant={currentTheme === "dark" ? "secondary" : "ghost"}
                size="icon"
                className="h-8 w-8 rounded-full"
                onClick={() => updatePreferences({ theme: "dark" })}
                title="Dark Mode"
            >
                <Moon className="h-4 w-4" />
            </Button>
            <Button
                variant={currentTheme === "system" ? "secondary" : "ghost"}
                size="icon"
                className="h-8 w-8 rounded-full"
                onClick={() => updatePreferences({ theme: "system" })}
                title="System Preference"
            >
                <Monitor className="h-4 w-4" />
            </Button>
        </div>
    );
}
