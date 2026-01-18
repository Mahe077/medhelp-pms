"use client"

import { useTranslations } from 'next-intl';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/Card';
import { Label } from '@/components/ui/Label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { useUserPreferences, useUpdateUserPreferences, type UserPreferences } from '@/lib/api/settings';
import { toast } from 'sonner';

export function PreferencesTab() {
    const t = useTranslations("Settings");
    const { data: userPrefs, isLoading: prefsLoading } = useUserPreferences();
    const updatePrefsMutation = useUpdateUserPreferences();

    const handlePreferencesUpdate = (field: keyof UserPreferences, value: string) => {
        if (!userPrefs) return;

        const updated = { ...userPrefs, [field]: value };
        updatePrefsMutation.mutate(updated, {
            onSuccess: () => {
                toast.success(t('preferences.updateSuccess'));
                // Reload to apply language/theme changes
                window.location.reload();
            },
            onError: () => {
                toast.error(t('preferences.updateError'));
            }
        });
    };

    return (
        <Card>
            <CardHeader>
                <CardTitle>{t("preferences.title")}</CardTitle>
                <CardDescription>{t("preferences.description")}</CardDescription>
            </CardHeader>
            <CardContent>
                {prefsLoading ? (
                    <div>{t("common.loading")}</div>
                ) : userPrefs && (
                    <div className="space-y-6">
                        <div className="space-y-2">
                            <Label htmlFor="language">{t("preferences.language")}</Label>
                            <Select
                                defaultValue={userPrefs.preferredLanguage}
                                onValueChange={(value) => handlePreferencesUpdate('preferredLanguage', value)}
                            >
                                <SelectTrigger>
                                    <SelectValue placeholder={t("preferences.selectLanguage")} />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="en">{t("preferences.english")}</SelectItem>
                                    <SelectItem value="si">{t("preferences.sinhala")}</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="theme">{t("preferences.theme")}</Label>
                            <Select
                                defaultValue={userPrefs.preferredTheme}
                                onValueChange={(value) => handlePreferencesUpdate('preferredTheme', value)}
                            >
                                <SelectTrigger>
                                    <SelectValue placeholder={t("preferences.selectTheme")} />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="light">{t("preferences.light")}</SelectItem>
                                    <SelectItem value="dark">{t("preferences.dark")}</SelectItem>
                                    <SelectItem value="system">{t("preferences.system")}</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                    </div>
                )}
            </CardContent>
        </Card>
    );
}
