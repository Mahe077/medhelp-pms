"use client"

import { useTranslations } from 'next-intl';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useState } from 'react';
import { FileKey, IdCard, User, Waypoints } from 'lucide-react';
import UsersTab from './components/UsersTab';
import RolesTab from './components/RolesTab';
import PermissionsTab from './components/PermissionsTab';
import AuditTrailTab from './components/AuditTrailTab';

function AccessPage() {
    const t = useTranslations("Access");
    const [activeTab, setActiveTab] = useState("users");

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div className="flex flex-col gap-2">
                    <h1 className="text-3xl font-bold tracking-tight">{t("title")}</h1>
                    <p className="text-muted-foreground">{t("description")}</p>
                </div>
            </div>
            <Tabs
                value={activeTab}
                onValueChange={setActiveTab}
                className="w-full"
            >
                <TabsList className="grid w-full grid-cols-2 md:grid-cols-4">
                    <TabsTrigger value="users" className="flex items-center gap-2">
                        <User className="h-4 w-4" />
                        {t("tabs.users")}
                    </TabsTrigger>
                    <TabsTrigger value="roles" className="flex items-center gap-2">
                        <IdCard className="h-4 w-4" />
                        {t("tabs.roles")}
                    </TabsTrigger>
                    <TabsTrigger value="permissions" className="flex items-center gap-2">
                        <FileKey className="h-4 w-4" />
                        {t("tabs.permissions")}
                    </TabsTrigger>
                    <TabsTrigger value="audit-trail" className="flex items-center gap-2">
                        <Waypoints className="h-4 w-4" />
                        {t("tabs.auditTrail")}
                    </TabsTrigger>
                </TabsList>

                {/* 
                   We use conditional rendering here to ensure that components (and their data fetching hooks)
                   only mount when the specific tab is valid.
                   TabsContent from shadcn/radix may render even if hidden, so explicit check 
                   ensures true lazy loading.
                */}
                <TabsContent value="users">
                    {activeTab === "users" && <UsersTab />}
                </TabsContent>
                <TabsContent value="roles">
                    {activeTab === "roles" && <RolesTab />}
                </TabsContent>
                <TabsContent value="permissions">
                    {activeTab === "permissions" && <PermissionsTab />}
                </TabsContent>
                <TabsContent value="audit-trail">
                    {activeTab === "audit-trail" && <AuditTrailTab />}
                </TabsContent>
            </Tabs>
        </div>
    )
}

export default AccessPage