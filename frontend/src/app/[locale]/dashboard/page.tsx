import React from 'react'

export default function DashboardPage() {
    return (
        <div className="space-y-6">
            <div className="flex flex-col gap-2">
                <h1 className="text-3xl font-bold tracking-tight">Dashboard Overview</h1>
                <p className="text-muted-foreground">
                    Welcome to MedHelp Pharmacy Management System. Here is what&apos;s happening today.
                </p>
            </div>

            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                <div className="rounded-xl border border-border bg-card p-6 shadow-sm">
                    <div className="flex flex-col gap-1">
                        <span className="text-xs font-medium text-muted-foreground uppercase">Total Sales</span>
                        <div className="flex items-baseline gap-2">
                            <span className="text-2xl font-bold">Rs. 124,500</span>
                            <span className="text-xs font-medium text-green-500">+12%</span>
                        </div>
                    </div>
                </div>
                <div className="rounded-xl border border-border bg-card p-6 shadow-sm">
                    <div className="flex flex-col gap-1">
                        <span className="text-xs font-medium text-muted-foreground uppercase">Prescriptions</span>
                        <div className="flex items-baseline gap-2">
                            <span className="text-2xl font-bold">48</span>
                            <span className="text-xs font-medium text-green-500">+5</span>
                        </div>
                    </div>
                </div>
                <div className="rounded-xl border border-border bg-card p-6 shadow-sm">
                    <div className="flex flex-col gap-1">
                        <span className="text-xs font-medium text-muted-foreground uppercase">Active Patients</span>
                        <div className="flex items-baseline gap-2">
                            <span className="text-2xl font-bold">1,240</span>
                            <span className="text-xs font-medium text-blue-500">Stable</span>
                        </div>
                    </div>
                </div>
                <div className="rounded-xl border border-border bg-card p-6 shadow-sm">
                    <div className="flex flex-col gap-1">
                        <span className="text-xs font-medium text-muted-foreground uppercase">Low Stock Icons</span>
                        <div className="flex items-baseline gap-2">
                            <span className="text-2xl font-bold">12</span>
                            <span className="text-xs font-medium text-destructive">Action Required</span>
                        </div>
                    </div>
                </div>
            </div>

            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-7">
                <div className="col-span-4 rounded-xl border border-border bg-card p-6 shadow-sm min-h-[400px]">
                    <h3 className="text-lg font-bold mb-4">Sales Analytics</h3>
                    <div className="flex items-center justify-center h-[300px] bg-accent/20 rounded-lg border border-dashed border-border">
                        <span className="text-muted-foreground italic">Chart visualization will be implemented here</span>
                    </div>
                </div>
                <div className="col-span-3 rounded-xl border border-border bg-card p-6 shadow-sm min-h-[400px]">
                    <h3 className="text-lg font-bold mb-4">Recent Prescriptions</h3>
                    <div className="space-y-4">
                        {[1, 2, 3, 4, 5].map((i) => (
                            <div key={i} className="flex items-center justify-between border-b border-border pb-3 last:border-0 last:pb-0">
                                <div className="flex flex-col">
                                    <span className="text-sm font-bold">Patient #124{i}</span>
                                    <span className="text-xs text-muted-foreground">Dr. Samarasiri • Amoxicillin</span>
                                </div>
                                <span className="text-xs font-medium bg-primary/10 text-primary px-2 py-1 rounded">Pending</span>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    )
}