"use client";

import React, { useState } from "react";
import Sidebar from "./dashboard/Sidebar";
import TopBar from "./dashboard/TopBar";
import NotificationDrawer from "./dashboard/NotificationDrawer";
import { cn } from "@/lib/utils";

interface DashboardLayoutProps {
    children: React.ReactNode;
}

const DashboardLayout = ({ children }: DashboardLayoutProps) => {
    const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
    const [mobileSidebarOpen, setMobileSidebarOpen] = useState(false);
    const [notificationsOpen, setNotificationsOpen] = useState(false);

    return (
        <div className="flex h-screen overflow-hidden bg-background">
            {/* Sidebar - Desktop */}
            <div className="hidden md:flex h-full">
                <Sidebar
                    collapsed={sidebarCollapsed}
                    setCollapsed={setSidebarCollapsed}
                />
            </div>

            {/* Sidebar - Mobile Overlay */}
            <div
                className={cn(
                    "fixed inset-0 bg-background/80 backdrop-blur-sm z-40 md:hidden transition-opacity duration-300",
                    mobileSidebarOpen ? "opacity-100" : "opacity-0 pointer-events-none"
                )}
                onClick={() => setMobileSidebarOpen(false)}
            />
            <div
                className={cn(
                    "fixed inset-y-0 left-0 w-64 bg-card z-50 md:hidden transform transition-transform duration-300 ease-in-out",
                    mobileSidebarOpen ? "translate-x-0" : "-translate-x-full"
                )}
            >
                <Sidebar
                    collapsed={false}
                    setCollapsed={() => { }}
                />
            </div>

            {/* Main Content */}
            <div className="flex flex-1 flex-col overflow-hidden">
                <TopBar
                    onOpenNotifications={() => setNotificationsOpen(true)}
                    onToggleMobileSidebar={() => setMobileSidebarOpen(true)}
                />
                <main className="flex-1 overflow-y-auto p-6 md:p-8">
                    <div className="mx-auto max-w-7xl">
                        {children}
                    </div>
                </main>
            </div>

            {/* Notifications Drawer */}
            <NotificationDrawer
                isOpen={notificationsOpen}
                onClose={() => setNotificationsOpen(false)}
            />
        </div>
    );
};

export default DashboardLayout;
