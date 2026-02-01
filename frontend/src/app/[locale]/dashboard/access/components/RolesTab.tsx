import { useState } from "react";
import { useRoles, usePermissions, useCreateRole, useAssignPermission, useRemovePermission, Role, Permission, useRolePermissions } from "@/lib/api/access";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Plus, Shield } from "lucide-react";
import { useTranslations } from "next-intl";
import { toast } from "sonner";
import { Checkbox } from "@/components/ui/checkbox";

export default function RolesTab() {
    const t = useTranslations("Access.roles");
    const commonT = useTranslations("Access.common");

    const { data: roles = [], isLoading: isLoadingRoles } = useRoles();
    const { data: permissions = [] } = usePermissions();

    const createRole = useCreateRole();
    const assignPermission = useAssignPermission();
    const removePermission = useRemovePermission();

    const [isCreateOpen, setIsCreateOpen] = useState(false);
    const [newRoleName, setNewRoleName] = useState("");
    const [newRoleDesc, setNewRoleDesc] = useState("");
    const [selectedRole, setSelectedRole] = useState<Role | null>(null);

    const handleCreateRole = async () => {
        try {
            await createRole.mutateAsync({ name: newRoleName, description: newRoleDesc });
            toast.success(commonT("success"));
            setIsCreateOpen(false);
            setNewRoleName("");
            setNewRoleDesc("");
        } catch {
            toast.error(commonT("error"));
        }
    };

    const handleAssignPermission = async (roleId: string, permissionId: string) => {
        try {
            await assignPermission.mutateAsync({ roleId, permissionId });
            toast.success(commonT("success"));
            // Refetch logic handled by react-query invalidation
        } catch {
            toast.error(commonT("error"));
        }
    };

    const handleRemovePermission = async (roleId: string, permissionId: string) => {
        try {
            await removePermission.mutateAsync({ roleId, permissionId });
            toast.success(commonT("success"));
        } catch {
            toast.error(commonT("error"));
        }
    };

    if (isLoadingRoles) {
        return <div className="p-4">{commonT("loading")}</div>;
    }

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
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
                                <Input value={newRoleName} onChange={(e) => setNewRoleName(e.target.value)} placeholder="Admin" />
                            </div>
                            <div className="space-y-2">
                                <label className="text-sm font-medium">{t("roleDesc")}</label>
                                <Input value={newRoleDesc} onChange={(e) => setNewRoleDesc(e.target.value)} placeholder="Full system access" />
                            </div>
                        </div>
                        <DialogFooter>
                            <Button variant="outline" onClick={() => setIsCreateOpen(false)}>{commonT("cancel")}</Button>
                            <Button onClick={handleCreateRole} disabled={createRole.isPending}>{commonT("save")}</Button>
                        </DialogFooter>
                    </DialogContent>
                </Dialog>
            </div>

            <div className="rounded-md border">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>{t("name")}</TableHead>
                            <TableHead>{t("roleDesc")}</TableHead>
                            <TableHead className="w-[150px] text-right">{commonT("actions")}</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {roles.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={3} className="text-center h-24 text-muted-foreground">
                                    {t("noRoles")}
                                </TableCell>
                            </TableRow>
                        ) : (
                            roles.map((role) => (
                                <TableRow key={role.id}>
                                    <TableCell className="font-medium flex items-center gap-2">
                                        <Shield className="h-4 w-4 text-muted-foreground" />
                                        {role.name}
                                    </TableCell>
                                    <TableCell>{role.description}</TableCell>
                                    <TableCell className="text-right">
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            onClick={() => setSelectedRole(role)}
                                        >
                                            {t("managePermissions")}
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))
                        )}
                    </TableBody>
                </Table>
            </div>

            {/* Role Details / Permission Assignment Dialog */}
            {selectedRole && (
                <Dialog open={!!selectedRole} onOpenChange={(open) => !open && setSelectedRole(null)}>
                    <DialogContent className="max-w-3xl h-[80vh] flex flex-col">
                        <DialogHeader>
                            <DialogTitle>{selectedRole.name}</DialogTitle>
                            <DialogDescription>{selectedRole.description}</DialogDescription>
                        </DialogHeader>

                        <div className="flex-1 overflow-y-auto py-4">
                            <RolePermissionsEditor
                                role={selectedRole}
                                allPermissions={permissions}
                                onAssign={handleAssignPermission}
                                onRemove={handleRemovePermission}
                            />
                        </div>
                    </DialogContent>
                </Dialog>
            )}
        </div>
    );
}

function RolePermissionsEditor({ role, allPermissions, onAssign, onRemove }: {
    role: Role,
    allPermissions: Permission[],
    onAssign: (roleId: string, permId: string) => void,
    onRemove: (roleId: string, permId: string) => void
}) {
    const { data: rolePermissions = [], isLoading } = useRolePermissions(role.id);
    const tRoles = useTranslations("Access.roles");

    if (isLoading) {
        return <div className="p-4 flex justify-center text-muted-foreground">{tRoles("loading")}</div>;
    }

    const rolePermissionIds = new Set(rolePermissions.map(p => p.id));

    // Group permissions by resource
    const groupedPermissions = allPermissions.reduce((acc, perm) => {
        const resource = perm.resource || "Other";
        if (!acc[resource]) {
            acc[resource] = [];
        }
        acc[resource].push(perm);
        return acc;
    }, {} as Record<string, Permission[]>);

    // Sort resources
    const sortedResources = Object.keys(groupedPermissions).sort();

    return (
        <div className="space-y-6">
            {sortedResources.map(resource => {
                const groupPerms = groupedPermissions[resource];
                const allSelected = groupPerms.every(p => rolePermissionIds.has(p.id));
                const someSelected = groupPerms.some(p => rolePermissionIds.has(p.id));
                const isIndeterminate = someSelected && !allSelected;

                return (
                    <div key={resource} className="rounded-lg border bg-card text-card-foreground shadow-sm">
                        <div className="flex items-center justify-between p-4 border-b bg-muted/20">
                            <div className="flex items-center gap-2">
                                <Shield className="h-4 w-4 text-primary" />
                                <h4 className="font-semibold capitalize">{resource}</h4>
                            </div>
                            <div className="flex items-center gap-2">
                                <Checkbox
                                    id={`select-all-${resource}`}
                                    checked={allSelected ? true : isIndeterminate ? "indeterminate" : false}
                                    onCheckedChange={(checked) => {
                                        if (checked === true) {
                                            // Assign all
                                            groupPerms.forEach(p => {
                                                if (!rolePermissionIds.has(p.id)) {
                                                    onAssign(role.id, p.id);
                                                }
                                            });
                                        } else {
                                            // Remove all
                                            groupPerms.forEach(p => {
                                                if (rolePermissionIds.has(p.id)) {
                                                    onRemove(role.id, p.id);
                                                }
                                            });
                                        }
                                    }}
                                />
                                <label htmlFor={`select-all-${resource}`} className="text-sm text-muted-foreground cursor-pointer select-none">
                                    {allSelected ? "Unselect All" : "Select All"}
                                </label>
                            </div>
                        </div>
                        <div className="p-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                            {groupPerms.map(permission => {
                                const isAssigned = rolePermissionIds.has(permission.id);
                                return (
                                    <div key={permission.id} className="flex items-start space-x-3 p-2 rounded-md hover:bg-muted/50 transition-colors">
                                        <Checkbox
                                            id={permission.id}
                                            checked={isAssigned}
                                            onCheckedChange={(checked) => {
                                                if (checked) {
                                                    onAssign(role.id, permission.id);
                                                } else {
                                                    onRemove(role.id, permission.id);
                                                }
                                            }}
                                            className="mt-0.5"
                                        />
                                        <div className="grid gap-1">
                                            <label
                                                htmlFor={permission.id}
                                                className="text-sm font-medium leading-none cursor-pointer"
                                            >
                                                {permission.action}
                                            </label>
                                            <p className="text-xs text-muted-foreground line-clamp-2" title={permission.description}>
                                                {permission.description}
                                            </p>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    </div>
                );
            })}
        </div>
    );
}