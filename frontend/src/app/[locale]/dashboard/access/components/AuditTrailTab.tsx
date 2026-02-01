import { useAuditLogs } from "@/lib/api/access";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Card, CardContent } from "@/components/ui/Card";
import { Badge } from "@/components/ui/badge";
import { useTranslations } from "next-intl";
import { Activity } from "lucide-react";

export default function AuditTrailTab() {
    const t = useTranslations("Access.auditTrail");
    const commonT = useTranslations("Access.common");

    const { data: logs = [], isLoading } = useAuditLogs();

    if (isLoading) {
        return <div className="p-4">{commonT("loading")}</div>;
    }

    return (
        <div className="space-y-6">
            <div>
                <h2 className="text-2xl font-bold tracking-tight">{t("title")}</h2>
                <p className="text-muted-foreground">{t("description")}</p>
            </div>

            <Card>
                <CardContent className="p-0">
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>{t("event")}</TableHead>
                                <TableHead>{t("descriptionCol")}</TableHead>
                                <TableHead>{t("performedBy")}</TableHead>
                                <TableHead className="text-right">{t("time")}</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {logs.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={4} className="text-center h-24 text-muted-foreground">
                                        {t("noRecords")}
                                    </TableCell>
                                </TableRow>
                            ) : (
                                logs.map((log) => (
                                    <TableRow key={log.id}>
                                        <TableCell className="font-medium">
                                            <div className="flex items-center">
                                                <Activity className="mr-2 h-4 w-4 text-muted-foreground" />
                                                <Badge variant="outline">{log.eventType}</Badge>
                                            </div>
                                        </TableCell>
                                        <TableCell>{log.description}</TableCell>
                                        <TableCell>{log.userName || log.userId}</TableCell>
                                        <TableCell className="text-right text-muted-foreground">
                                            {log.occurredAt ? new Date(log.occurredAt).toLocaleString() : "-"}
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>
        </div>
    );
}