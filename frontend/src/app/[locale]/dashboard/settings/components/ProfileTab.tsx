"use client"

import { useTranslations } from 'next-intl';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/Card';
import { Label } from '@/components/ui/Label';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { useProfileSettings, useUpdateProfileSettings } from '@/lib/api/settings';
import { toast } from 'sonner';

export function ProfileTab() {
    const t = useTranslations("Settings");
    const { data: profile, isLoading: profileLoading } = useProfileSettings();
    const updateProfileMutation = useUpdateProfileSettings();

    const handleProfileSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const formData = new FormData(e.currentTarget);

        updateProfileMutation.mutate({
            username: profile?.username || '',
            email: profile?.email || '',
            firstName: formData.get('firstName') as string,
            lastName: formData.get('lastName') as string,
            phone: formData.get('phone') as string,
            licenseNumber: formData.get('licenseNumber') as string,
        }, {
            onSuccess: () => {
                toast.success(t('profile.updateSuccess'));
            },
            onError: () => {
                toast.error(t('profile.updateError'));
            }
        });
    };

    return (
        <Card>
            <CardHeader>
                <CardTitle>{t("profile.title")}</CardTitle>
                <CardDescription>{t("profile.description")}</CardDescription>
            </CardHeader>
            <CardContent>
                {profileLoading ? (
                    <div>{t("common.loading")}</div>
                ) : (
                    <form onSubmit={handleProfileSubmit} className="space-y-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label htmlFor="firstName">{t("profile.firstName")}</Label>
                                <Input id="firstName" name="firstName" defaultValue={profile?.firstName} />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="lastName">{t("profile.lastName")}</Label>
                                <Input id="lastName" name="lastName" defaultValue={profile?.lastName} />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="email">{t("profile.email")}</Label>
                                <Input id="email" name="email" type="email" defaultValue={profile?.email} disabled />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="phone">{t("profile.phone")}</Label>
                                <Input id="phone" name="phone" defaultValue={profile?.phone} />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="licenseNumber">{t("profile.licenseNumber")}</Label>
                                <Input id="licenseNumber" name="licenseNumber" defaultValue={profile?.licenseNumber} />
                            </div>
                        </div>
                        <Button type="submit" disabled={updateProfileMutation.isPending}>
                            {updateProfileMutation.isPending ? t("profile.saving") : t("profile.saveChanges")}
                        </Button>
                    </form>
                )}
            </CardContent>
        </Card>
    );
}
