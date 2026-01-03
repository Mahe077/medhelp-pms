"use client";

import React from "react";
import { X, Bell, Info, AlertCircle, CheckCircle2 } from "lucide-react";
import { Button } from "@/components/ui/Button";
import { cn } from "@/lib/utils";

interface NotificationDrawerProps {
    isOpen: boolean;
    onClose: () => void;
}

const notifications = [
    {
        id: 1,
        title: "Low Inventory Alert",
        description: "Amoxicillin 500mg is below safety stock level (50 units remaining).",
        time: "10 minutes ago",
        type: "warning",
        icon: AlertCircle,
    },
    {
        id: 2,
        title: "New Prescription",
        description: "Dr. Samarasiri has sent a new prescription for Patient ID: #12930.",
        time: "1 hour ago",
        type: "info",
        icon: Info,
    },
    {
        id: 3,
        title: "System Update",
        description: "The Pharmacy Management System has been updated to v1.2.4.",
        time: "3 hours ago",
        type: "success",
        icon: CheckCircle2,
    },
];

const NotificationDrawer = ({ isOpen, onClose }: NotificationDrawerProps) => {
    return (
        <>
            {/* Backdrop */}
            <div
                className={cn(
                    "fixed inset-0 bg-background/80 backdrop-blur-sm z-40 transition-opacity duration-300",
                    isOpen ? "opacity-100" : "opacity-0 pointer-events-none"
                )}
                onClick={onClose}
            />

            {/* Drawer */}
            <div
                className={cn(
                    "fixed top-0 right-0 h-full w-full sm:w-80 md:w-96 bg-card border-l border-border z-50 transform transition-transform duration-300 ease-in-out shadow-2xl",
                    isOpen ? "translate-x-0" : "translate-x-full"
                )}
            >
                <div className="flex flex-col h-full">
                    {/* Header */}
                    <div className="flex items-center justify-between p-6 border-b border-border">
                        <div className="flex items-center gap-2">
                            <Bell className="h-5 w-5 text-primary" />
                            <h2 className="text-xl font-bold">Notifications</h2>
                        </div>
                        <Button variant="ghost" size="icon" onClick={onClose} className="rounded-full">
                            <X className="h-5 w-5" />
                        </Button>
                    </div>

                    {/* Content */}
                    <div className="flex-1 overflow-y-auto p-4 space-y-4">
                        {notifications.length > 0 ? (
                            notifications.map((notif) => (
                                <div
                                    key={notif.id}
                                    className="p-4 rounded-xl border border-border bg-background/50 hover:bg-accent/30 transition-colors cursor-pointer group"
                                >
                                    <div className="flex gap-4">
                                        <div className={cn(
                                            "mt-1 p-2 rounded-lg shrink-0",
                                            notif.type === "warning" && "bg-orange-500/10 text-orange-500",
                                            notif.type === "info" && "bg-blue-500/10 text-blue-500",
                                            notif.type === "success" && "bg-green-500/10 text-green-500"
                                        )}>
                                            <notif.icon className="h-4 w-4" />
                                        </div>
                                        <div className="space-y-1">
                                            <h3 className="text-sm font-bold group-hover:text-primary transition-colors">
                                                {notif.title}
                                            </h3>
                                            <p className="text-xs text-muted-foreground leading-relaxed">
                                                {notif.description}
                                            </p>
                                            <p className="text-[10px] text-muted-foreground/60 flex items-center gap-1 pt-1">
                                                {notif.time}
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            ))
                        ) : (
                            <div className="flex flex-col items-center justify-center h-full text-muted-foreground opacity-50">
                                <Bell className="h-12 w-12 mb-4" />
                                <p>No new notifications</p>
                            </div>
                        )}
                    </div>

                    {/* Footer */}
                    <div className="p-6 border-t border-border mt-auto">
                        <Button className="w-full" variant="outline">
                            Mark all as read
                        </Button>
                    </div>
                </div>
            </div>
        </>
    );
};

export default NotificationDrawer;
