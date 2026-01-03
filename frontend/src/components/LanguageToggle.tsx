"use client";

import React from "react";
import { Button } from "@/components/ui/Button";
import { usePreferences } from "@/hooks/usePreferences";

export function LanguageToggle() {
    const { currentLanguage, updatePreferences } = usePreferences();

    return (
        <div className="flex items-center gap-1 rounded-full border border-border/50 bg-card/50 p-1 backdrop-blur-md">
            <Button
                variant={currentLanguage === "en" ? "secondary" : "ghost"}
                size="sm"
                className="h-8 rounded-full px-3 text-xs font-semibold transition-all hover:bg-primary/20"
                onClick={() => updatePreferences({ language: "en" })}
                title="English"
            >
                EN
            </Button>
            <Button
                variant={currentLanguage === "si" ? "secondary" : "ghost"}
                size="sm"
                className="h-8 rounded-full px-3 text-xs font-semibold transition-all hover:bg-primary/20"
                onClick={() => updatePreferences({ language: "si" })}
                title="සිංහල"
            >
                සිං
            </Button>
        </div>
    );
}
