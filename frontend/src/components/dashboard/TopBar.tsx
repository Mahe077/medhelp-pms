"use client";

import React from "react";
import {
    Bell,
    Search,
    User,
    Menu,
    ChevronDown
} from "lucide-react";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { ThemeToggle } from "@/components/ThemeToggle";
import { LanguageToggle } from "@/components/LanguageToggle";
import { useTranslations } from "next-intl";

interface TopBarProps {
    onOpenNotifications: () => void;
    onToggleMobileSidebar: () => void;
}

const TopBar = ({ onOpenNotifications, onToggleMobileSidebar }: TopBarProps) => {
    const t = useTranslations("Dashboard");
    return (
        <header className="flex h-16 items-center justify-between border-b border-border bg-card/80 px-6 backdrop-blur-md sticky top-0 z-20">
            <div className="flex items-center gap-4">
                <Button
                    variant="ghost"
                    size="icon"
                    className="md:hidden"
                    onClick={onToggleMobileSidebar}
                >
                    <Menu className="h-5 w-5" />
                </Button>

                <div className="relative hidden max-w-md w-72 sm:block">
                    <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                    <Input
                        placeholder={t("searchPlaceholder")}
                        className="pl-10 h-9 bg-background/50 border-border/50 focus:bg-background transition-all"
                    />
                </div>
            </div>

            <div className="flex items-center gap-2 sm:gap-4">
                <LanguageToggle />
                <ThemeToggle />

                <Button
                    variant="ghost"
                    size="icon"
                    className="relative text-muted-foreground hover:text-primary transition-colors"
                    onClick={onOpenNotifications}
                >
                    <Bell className="h-5 w-5" />
                    <span className="absolute top-2 right-2 flex h-2 w-2 rounded-full bg-primary ring-2 ring-background" />
                </Button>

                <div className="h-8 w-px bg-border mx-1 hidden sm:block" />

                <Button
                    variant="ghost"
                    className="flex items-center gap-2 p-1 pl-2 hover:bg-accent rounded-full transition-all"
                >
                    <div className="flex flex-col items-end hidden lg:flex">
                        <span className="text-xs font-bold leading-none">Admin User</span>
                        <span className="text-[10px] text-muted-foreground">Administrator</span>
                    </div>
                    <div className="h-8 w-8 rounded-full bg-primary/10 flex items-center justify-center border border-primary/20">
                        <User className="h-4 w-4 text-primary" />
                    </div>
                    <ChevronDown className="h-3 w-3 text-muted-foreground hidden sm:block" />
                </Button>
            </div>
        </header>
    );
};

export default TopBar;
