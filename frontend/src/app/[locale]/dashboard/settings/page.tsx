"use client"

import { useState } from 'react';
import { useTranslations } from 'next-intl';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { User, Shield, Bell, Settings2, History } from 'lucide-react';

import { ProfileTab } from './components/ProfileTab';
import { SecurityTab } from './components/SecurityTab';
import { NotificationsTab } from './components/NotificationsTab';
import { PreferencesTab } from './components/PreferencesTab';
import { AuditTrailTab } from './components/AuditTrailTab';

function SettingsPage() {
    const t = useTranslations("Settings");
    const [activeTab, setActiveTab] = useState("profile");

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div className="flex flex-col gap-2">
                    <h1 className="text-3xl font-bold tracking-tight">{t("title")}</h1>
                    <p className="text-muted-foreground">{t("description")}</p>
                </div>
            </div>

            <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
                <TabsList className="grid w-full grid-cols-5">
                    <TabsTrigger value="profile" className="flex items-center gap-2">
                        <User className="h-4 w-4" />
                        {t("tabs.profile")}
                    </TabsTrigger>
                    <TabsTrigger value="security" className="flex items-center gap-2">
                        <Shield className="h-4 w-4" />
                        {t("tabs.security")}
                    </TabsTrigger>
                    <TabsTrigger value="notifications" className="flex items-center gap-2">
                        <Bell className="h-4 w-4" />
                        {t("tabs.notifications")}
                    </TabsTrigger>
                    <TabsTrigger value="preferences" className="flex items-center gap-2">
                        <Settings2 className="h-4 w-4" />
                        {t("tabs.preferences")}
                    </TabsTrigger>
                    <TabsTrigger value="audit" className="flex items-center gap-2">
                        <History className="h-4 w-4" />
                        {t("tabs.auditTrail")}
                    </TabsTrigger>
                </TabsList>

                {/* 
                   We use conditional rendering here to ensure that components (and their data fetching hooks)
                   only mount when the specific tab is valid.
                   TabsContent from shadcn/radix may render even if hidden, so explicit check 
                   ensures true lazy loading.
                */}
                <TabsContent value="profile">
                    {activeTab === 'profile' && <ProfileTab />}
                </TabsContent>

                <TabsContent value="security">
                    {activeTab === 'security' && <SecurityTab />}
                </TabsContent>

                <TabsContent value="notifications">
                    {activeTab === 'notifications' && <NotificationsTab />}
                </TabsContent>

                <TabsContent value="preferences">
                    {activeTab === 'preferences' && <PreferencesTab />}
                </TabsContent>

                <TabsContent value="audit">
                    {activeTab === 'audit' && <AuditTrailTab />}
                </TabsContent>
            </Tabs>
        </div>
    );
}

export default SettingsPage;
