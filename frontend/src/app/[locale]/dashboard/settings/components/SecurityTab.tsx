"use client"

import { useState } from 'react';
import { useTranslations } from 'next-intl';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/Card';
import { Label } from '@/components/ui/Label';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { useChangePassword, type PasswordChangeRequest } from '@/lib/api/settings';
import { toast } from 'sonner';

export function SecurityTab() {
    const t = useTranslations("Settings");
    const [passwords, setPasswords] = useState<PasswordChangeRequest>({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
    });
    const changePasswordMutation = useChangePassword();

    const handlePasswordChange = (e: React.FormEvent) => {
        e.preventDefault();

        if (passwords.newPassword !== passwords.confirmPassword) {
            toast.error(t('security.passwordsMatch'));
            return;
        }

        changePasswordMutation.mutate(passwords, {
            onSuccess: () => {
                toast.success(t('security.passwordChangeSuccess'));
                setPasswords({ currentPassword: '', newPassword: '', confirmPassword: '' });
            },
            onError: () => {
                toast.error(t('security.passwordChangeError'));
            }
        });
    };

    return (
        <Card>
            <CardHeader>
                <CardTitle>{t("security.title")}</CardTitle>
                <CardDescription>{t("security.description")}</CardDescription>
            </CardHeader>
            <CardContent>
                <form onSubmit={handlePasswordChange} className="space-y-4">
                    <div className="space-y-2">
                        <Label htmlFor="currentPassword">{t("security.currentPassword")}</Label>
                        <Input
                            id="currentPassword"
                            type="password"
                            value={passwords.currentPassword}
                            onChange={(e) => setPasswords({ ...passwords, currentPassword: e.target.value })}
                        />
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="newPassword">{t("security.newPassword")}</Label>
                        <Input
                            id="newPassword"
                            type="password"
                            value={passwords.newPassword}
                            onChange={(e) => setPasswords({ ...passwords, newPassword: e.target.value })}
                        />
                        <p className="text-sm text-muted-foreground">{t("security.passwordRequirements")}</p>
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="confirmPassword">{t("security.confirmPassword")}</Label>
                        <Input
                            id="confirmPassword"
                            type="password"
                            value={passwords.confirmPassword}
                            onChange={(e) => setPasswords({ ...passwords, confirmPassword: e.target.value })}
                        />
                    </div>
                    <Button type="submit" disabled={changePasswordMutation.isPending}>
                        {changePasswordMutation.isPending ? t("security.changing") : t("security.changePasswordButton")}
                    </Button>
                </form>
            </CardContent>
        </Card>
    );
}
