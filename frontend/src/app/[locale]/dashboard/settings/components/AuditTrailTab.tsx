"use client"

import { useState } from 'react';
import { useTranslations } from 'next-intl';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { useAuditTrail } from '@/lib/api/settings';

export function AuditTrailTab() {
    const t = useTranslations("Settings");
    const [auditPage, setAuditPage] = useState(0);
    const { data: auditData, isLoading: auditLoading } = useAuditTrail(auditPage, 20);

    return (
        <Card>
            <CardHeader>
                <CardTitle>{t("auditTrail.title")}</CardTitle>
                <CardDescription>{t("auditTrail.description")}</CardDescription>
            </CardHeader>
            <CardContent>
                {auditLoading ? (
                    <div>{t("auditTrail.loading")}</div>
                ) : auditData && auditData.content.length > 0 ? (
                    <div className="space-y-4">
                        {auditData.content.map((log) => (
                            <div key={log.id} className="border-l-2 border-primary pl-4 pb-4">
                                <div className="flex justify-between items-start">
                                    <div>
                                        <p className="font-semibold">{log.eventType}</p>
                                        <p className="text-sm text-muted-foreground">
                                            {t("auditTrail.changedBy")}: {log.userName}
                                        </p>
                                    </div>
                                    <p className="text-sm text-muted-foreground">
                                        {new Date(log.occurredAt).toLocaleString()}
                                    </p>
                                </div>
                            </div>
                        ))}
                        <div className="flex gap-2 justify-center">
                            <Button variant="outline" onClick={() => setAuditPage(p => Math.max(0, p - 1))} disabled={auditPage === 0}>
                                Previous
                            </Button>
                            <Button variant="outline" onClick={() => setAuditPage(p => p + 1)} disabled={auditPage >= (auditData.totalPages - 1)}>
                                Next
                            </Button>
                        </div>
                    </div>
                ) : (
                    <p className="text-muted-foreground">{t("auditTrail.noRecords")}</p>
                )}
            </CardContent>
        </Card>
    );
}
