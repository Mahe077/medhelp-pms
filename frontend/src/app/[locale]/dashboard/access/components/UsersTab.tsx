import React from 'react'
import { useTranslations } from 'next-intl';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/Card';

function UsersTab() {
    const t = useTranslations("Access");
    return (
        <Card>
            <CardHeader>
                <CardTitle>{t("users.title")}</CardTitle>
                <CardDescription>{t("users.description")}</CardDescription>
            </CardHeader>
            <CardContent>
                <div>{t("users.noUsers")}</div>
            </CardContent>
        </Card>
    )
}

export default UsersTab