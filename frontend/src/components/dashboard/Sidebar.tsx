"use client";

import React from "react";
import Link from "next/link";
import { usePathname } from "@/i18n/routing";
import {
    LayoutDashboard,
    Pill,
    Users,
    Settings,
    LogOut,
    ChevronLeft,
    ChevronRight,
    ClipboardList,
    Package,
    Accessibility,
    Lock
} from "lucide-react";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/Button";
import { useTranslations } from "next-intl";
import { useAuth } from "@/hooks/useAuth";

interface SidebarProps {
    collapsed: boolean;
    setCollapsed: (collapsed: boolean) => void;
}

const Sidebar = ({ collapsed, setCollapsed }: SidebarProps) => {
    const pathname = usePathname();
    const t = useTranslations("Dashboard");
    const { logout } = useAuth();

    const navItems = [
        {
            title: t("overview"),
            icon: LayoutDashboard,
            href: "/dashboard",
        },
        {
            title: t("patients"),
            icon: Users,
            href: "/dashboard/patients",
        },
        {
            title: t("medications"),
            icon: Pill,
            href: "/dashboard/medications",
        },
        {
            title: t("prescriptions"),
            icon: ClipboardList,
            href: "/dashboard/prescriptions",
        },
        {
            title: t("inventory"),
            icon: Package,
            href: "/dashboard/inventory",
        },
        {
            title: t("settings"),
            icon: Settings,
            href: "/dashboard/settings",
        },
        {
            title: t("access"),
            icon: Lock,
            href: "/dashboard/access",
        }
    ];

    return (
        <aside
            className={cn(
                "relative flex flex-col border-r border-border bg-card transition-all duration-300 ease-in-out z-30",
                collapsed ? "w-20" : "w-64"
            )}
        >
            {/* Logo Section */}
            <div className="flex h-16 items-center border-b border-border px-4 py-4">
                <div className="flex items-center gap-3 overflow-hidden">
                    <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-primary shadow-lg shadow-primary/30">
                        <Pill className="h-6 w-6 text-primary-foreground" />
                    </div>
                    {!collapsed && (
                        <span className="text-xl font-bold tracking-tight whitespace-nowrap">MedHelp</span>
                    )}
                </div>
            </div>

            {/* Navigation Items */}
            <nav className="flex-1 space-y-1 p-3 overflow-y-auto">
                {navItems.map((item) => {
                    const isActive = item.href === "/dashboard"
                        ? pathname === "/dashboard"
                        : pathname.startsWith(item.href);

                    return (
                        <Link
                            key={item.href}
                            href={item.href}
                            className={cn(
                                "flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors",
                                isActive
                                    ? "bg-primary text-primary-foreground"
                                    : "text-muted-foreground hover:bg-accent hover:text-accent-foreground"
                            )}
                        >
                            <item.icon className={cn("h-5 w-5 shrink-0")} />
                            {!collapsed && <span className="whitespace-nowrap">{item.title}</span>}
                        </Link>
                    );
                })}
            </nav>

            {/* Footer / Logout */}
            <div className="border-t border-border p-3">
                <Button
                    variant="ghost"
                    className={cn(
                        "w-full justify-start gap-3 px-3 text-muted-foreground hover:text-destructive",
                        collapsed && "justify-center"
                    )}
                    onClick={() => logout()}
                >
                    <LogOut className="h-5 w-5 shrink-0" />
                    {!collapsed && <span>{t("logout")}</span>}
                </Button>
            </div>

            {/* Collapse Toggle */}
            <Button
                variant="outline"
                size="icon"
                className="absolute -right-3 top-20 h-6 w-6 rounded-full border border-border bg-background shadow-sm hover:bg-accent hidden md:flex"
                onClick={() => setCollapsed(!collapsed)}
            >
                {collapsed ? (
                    <ChevronRight className="h-3 w-3" />
                ) : (
                    <ChevronLeft className="h-3 w-3" />
                )}
            </Button>
        </aside>
    );
};

export default Sidebar;
