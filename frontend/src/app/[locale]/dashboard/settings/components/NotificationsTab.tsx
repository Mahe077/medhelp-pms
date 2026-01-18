"use client"

import { useTranslations } from 'next-intl';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/Card';
import { Label } from '@/components/ui/Label';
import { Switch } from '@/components/ui/switch';
import { useNotificationPreferences, useUpdateNotificationPreferences, type NotificationPreferences } from '@/lib/api/settings';
import { toast } from 'sonner';

export function NotificationsTab() {
    const t = useTranslations("Settings");
    const { data: notifPrefs, isLoading: notifLoading } = useNotificationPreferences();
    const updateNotifMutation = useUpdateNotificationPreferences();

    const handleNotificationUpdate = (field: keyof NotificationPreferences, value: boolean | number) => {
        if (!notifPrefs) return;

        const updated = { ...notifPrefs, [field]: value };
        updateNotifMutation.mutate(updated, {
            onSuccess: () => {
                toast.success(t('notifications.updateSuccess'));
            },
            onError: () => {
                toast.error(t('notifications.updateError'));
            }
        });
    };

    return (
        <Card>
            <CardHeader>
                <CardTitle>{t("notifications.title")}</CardTitle>
                <CardDescription>{t("notifications.description")}</CardDescription>
            </CardHeader>
            <CardContent>
                {notifLoading ? (
                    <div>{t("common.loading")}</div>
                ) : notifPrefs && (
                    <div className="space-y-6">
                        <div className="space-y-4">
                            <h3 className="text-lg font-semibold">{t("notifications.general")}</h3>
                            <div className="flex items-center justify-between">
                                <Label htmlFor="emailNotif">{t("notifications.emailNotifications")}</Label>
                                <Switch
                                    id="emailNotif"
                                    checked={notifPrefs.emailNotificationsEnabled}
                                    onCheckedChange={(checked) => handleNotificationUpdate('emailNotificationsEnabled', checked)}
                                />
                            </div>
                            <div className="flex items-center justify-between">
                                <Label htmlFor="smsNotif">{t("notifications.smsNotifications")}</Label>
                                <Switch
                                    id="smsNotif"
                                    checked={notifPrefs.smsNotificationsEnabled}
                                    onCheckedChange={(checked) => handleNotificationUpdate('smsNotificationsEnabled', checked)}
                                />
                            </div>
                        </div>

                        <div className="space-y-4">
                            <h3 className="text-lg font-semibold">{t("notifications.prescriptions")}</h3>
                            <div className="flex items-center justify-between">
                                <Label htmlFor="rxEmail">{t("notifications.prescriptionReadyEmail")}</Label>
                                <Switch
                                    id="rxEmail"
                                    checked={notifPrefs.prescriptionReadyEmail}
                                    onCheckedChange={(checked) => handleNotificationUpdate('prescriptionReadyEmail', checked)}
                                />
                            </div>
                            <div className="flex items-center justify-between">
                                <Label htmlFor="rxSms">{t("notifications.prescriptionReadySms")}</Label>
                                <Switch
                                    id="rxSms"
                                    checked={notifPrefs.prescriptionReadySms}
                                    onCheckedChange={(checked) => handleNotificationUpdate('prescriptionReadySms', checked)}
                                />
                            </div>
                        </div>

                        <div className="space-y-4">
                            <h3 className="text-lg font-semibold">{t("notifications.refills")}</h3>
                            <div className="flex items-center justify-between">
                                <Label htmlFor="refillEmail">{t("notifications.refillReminderEmail")}</Label>
                                <Switch
                                    id="refillEmail"
                                    checked={notifPrefs.refillReminderEmail}
                                    onCheckedChange={(checked) => handleNotificationUpdate('refillReminderEmail', checked)}
                                />
                            </div>
                            <div className="flex items-center justify-between">
                                <Label htmlFor="refillSms">{t("notifications.refillReminderSms")}</Label>
                                <Switch
                                    id="refillSms"
                                    checked={notifPrefs.refillReminderSms}
                                    onCheckedChange={(checked) => handleNotificationUpdate('refillReminderSms', checked)}
                                />
                            </div>
                        </div>
                    </div>
                )}
            </CardContent>
        </Card>
    );
}
