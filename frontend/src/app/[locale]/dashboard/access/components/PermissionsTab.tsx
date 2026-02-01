import { useState } from "react";
import { usePermissions, useCreatePermission } from "@/lib/api/access";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Card, CardContent } from "@/components/ui/Card";
import { Badge } from "@/components/ui/badge";
import { Plus, Key, Search } from "lucide-react";
import { useTranslations } from "next-intl";
import { toast } from "sonner";

export default function PermissionsTab() {
    const t = useTranslations("Access.permissions");
    const commonT = useTranslations("Access.common");

    const { data: permissions = [], isLoading } = usePermissions();
    const createPermission = useCreatePermission();

    const [isCreateOpen, setIsCreateOpen] = useState(false);
    const [newPermName, setNewPermName] = useState("");
    const [newPermDesc, setNewPermDesc] = useState("");
    const [newPermResource, setNewPermResource] = useState("");
    const [newPermAction, setNewPermAction] = useState("");
    const [searchQuery, setSearchQuery] = useState("");

    const handleCreatePermission = async () => {
        try {
            await createPermission.mutateAsync({
                name: newPermName,
                description: newPermDesc,
                resource: newPermResource,
                action: newPermAction
            });
            toast.success(commonT("success"));
            setIsCreateOpen(false);
            setNewPermName("");
            setNewPermDesc("");
            setNewPermResource("");
            setNewPermAction("");
        } catch {
            toast.error(commonT("error"));
        }
    };

    const filteredPermissions = permissions
        .filter(p =>
            p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            p.description?.toLowerCase().includes(searchQuery.toLowerCase())
        )
        .sort((a, b) => {
            const resCompare = a.resource.localeCompare(b.resource);
            if (resCompare !== 0) return resCompare;
            return a.name.localeCompare(b.name);
        });

    if (isLoading) {
        return <div className="p-4">{commonT("loading")}</div>;
    }

    return (
        <div className="space-y-6">
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div>
                    <h2 className="text-2xl font-bold tracking-tight">{t("title")}</h2>
                    <p className="text-muted-foreground">{t("description")}</p>
                </div>
                <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
                    <DialogTrigger asChild>
                        <Button>
                            <Plus className="mr-2 h-4 w-4" />
                            {t("create")}
                        </Button>
                    </DialogTrigger>
                    <DialogContent>
                        <DialogHeader>
                            <DialogTitle>{t("create")}</DialogTitle>
                            <DialogDescription>{t("description")}</DialogDescription>
                        </DialogHeader>
                        <div className="space-y-4 py-4">
                            <div className="space-y-2">
                                <label className="text-sm font-medium">{t("name")}</label>
                                <Input value={newPermName} onChange={(e) => setNewPermName(e.target.value)} placeholder="user:read" />
                            </div>
                            <div className="space-y-2">
                                <label className="text-sm font-medium">{t("resource")}</label>
                                <Input value={newPermResource} onChange={(e) => setNewPermResource(e.target.value)} placeholder="user" />
                            </div>
                            <div className="space-y-2">
                                <label className="text-sm font-medium">{t("action")}</label>
                                <Input value={newPermAction} onChange={(e) => setNewPermAction(e.target.value)} placeholder="read" />
                            </div>
                            <div className="space-y-2">
                                <label className="text-sm font-medium">{t("permDesc")}</label>
                                <Input value={newPermDesc} onChange={(e) => setNewPermDesc(e.target.value)} placeholder="Can read user details" />
                            </div>
                        </div>
                        <DialogFooter>
                            <Button variant="outline" onClick={() => setIsCreateOpen(false)}>{commonT("cancel")}</Button>
                            <Button onClick={handleCreatePermission} disabled={createPermission.isPending}>{commonT("save")}</Button>
                        </DialogFooter>
                    </DialogContent>
                </Dialog>
            </div>

            <div className="flex items-center space-x-2">
                <div className="relative flex-1 max-w-sm">
                    <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                    <Input
                        type="search"
                        placeholder="Search permissions..."
                        className="pl-8"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                    />
                </div>
            </div>

            <Card>
                <CardContent className="p-0">
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>{t("name")}</TableHead>
                                <TableHead>{t("resource")}</TableHead>
                                <TableHead>{t("action")}</TableHead>
                                <TableHead className="w-[40%]">{t("permDesc")}</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {filteredPermissions.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={4} className="text-center h-24 text-muted-foreground">
                                        {t("noPermissions")}
                                    </TableCell>
                                </TableRow>
                            ) : (
                                filteredPermissions.map((perm) => (
                                    <TableRow key={perm.id}>
                                        <TableCell className="font-medium flex items-center">
                                            <Key className="mr-2 h-4 w-4 text-muted-foreground" />
                                            {perm.name}
                                        </TableCell>
                                        <TableCell><Badge variant="outline">{perm.resource}</Badge></TableCell>
                                        <TableCell><Badge variant="secondary">{perm.action}</Badge></TableCell>
                                        <TableCell className="text-muted-foreground">{perm.description}</TableCell>
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