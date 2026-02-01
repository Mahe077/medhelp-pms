import React, { useState } from 'react';
import { useTranslations } from 'next-intl';
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from '@/components/ui/Card';
import { Button } from '@/components/ui/button';
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from '@/components/ui/table';
import { Badge } from '@/components/ui/badge';
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog';
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select';
import { useUsers, useRoles, useAssignRoleToUser, useRemoveRoleFromUser, User, Role } from '@/lib/api/access';
import { Loader2, Plus, X } from 'lucide-react';

export default function UsersTab() {
    const t = useTranslations('Access.users');
    const common = useTranslations('Common');

    const { data: users, isLoading: isUsersLoading } = useUsers();
    const { data: roles } = useRoles();

    const assignRoleMutation = useAssignRoleToUser();
    const removeRoleMutation = useRemoveRoleFromUser();

    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [selectedRole, setSelectedRole] = useState<string>('');
    const [isAssignDialogOpen, setIsAssignDialogOpen] = useState(false);

    const handleAssignRole = () => {
        if (selectedUser && selectedRole) {
            assignRoleMutation.mutate(
                { userId: selectedUser.id, roleId: selectedRole },
                {
                    onSuccess: () => {
                        setIsAssignDialogOpen(false);
                        setSelectedRole('');
                    },
                }
            );
        }
    };

    const handleRemoveRole = (userId: string, roleId: string) => {
        if (confirm(t('confirmRemoveRole') || "Are you sure you want to remove this role?")) {
            removeRoleMutation.mutate({ userId, roleId });
        }
    };

    if (isUsersLoading) {
        return (
            <div className="flex justify-center p-8">
                <Loader2 className="h-8 w-8 animate-spin" />
            </div>
        );
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle>{t('title')}</CardTitle>
                <CardDescription>{t('description')}</CardDescription>
            </CardHeader>
            <CardContent>
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>{t('username')}</TableHead>
                            <TableHead>{t('email')}</TableHead>
                            <TableHead>{t('roles')}</TableHead>
                            <TableHead>{t('status')}</TableHead>
                            <TableHead>{t('actions')}</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {users?.map((user) => (
                            <TableRow key={user.id}>
                                <TableCell>{user.username}</TableCell>
                                <TableCell>{user.email}</TableCell>
                                <TableCell>
                                    <div className="flex flex-wrap gap-1">
                                        {user.roles && user.roles.map((role: Role) => (
                                            <Badge key={role.id} variant="secondary" className="flex items-center gap-1">
                                                {role.name}
                                                <button
                                                    onClick={() => handleRemoveRole(user.id, role.id)}
                                                    className="ml-1 hover:text-destructive"
                                                >
                                                    <X className="h-3 w-3" />
                                                </button>
                                            </Badge>
                                        ))}
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            className="h-6 w-6 p-0 rounded-full"
                                            onClick={() => {
                                                setSelectedUser(user);
                                                setIsAssignDialogOpen(true);
                                            }}
                                        >
                                            <Plus className="h-4 w-4" />
                                        </Button>
                                    </div>
                                </TableCell>
                                <TableCell>
                                    <Badge variant={user.isActive ? 'default' : 'secondary'}>
                                        {user.isActive ? t('active') : t('inactive')}
                                    </Badge>
                                </TableCell>
                                <TableCell>
                                    {/* Add edit/view actions here if needed */}
                                </TableCell>
                            </TableRow>
                        ))}
                        {(!users || users.length === 0) && (
                            <TableRow>
                                <TableCell colSpan={5} className="text-center text-muted-foreground">
                                    {t('noUsers')}
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>

                <Dialog open={isAssignDialogOpen} onOpenChange={setIsAssignDialogOpen}>
                    <DialogContent>
                        <DialogHeader>
                            <DialogTitle>{t('assignRole')}</DialogTitle>
                            <DialogDescription>
                                {selectedUser ? t('assignRoleDescription', { username: selectedUser.username }) : ''}
                            </DialogDescription>
                        </DialogHeader>
                        <div className="py-4">
                            <Select value={selectedRole} onValueChange={setSelectedRole}>
                                <SelectTrigger>
                                    <SelectValue placeholder={t('selectRole')} />
                                </SelectTrigger>
                                <SelectContent>
                                    {roles?.map((role) => (
                                        <SelectItem key={role.id} value={role.id}>
                                            {role.name}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>
                        <DialogFooter>
                            <Button variant="outline" onClick={() => setIsAssignDialogOpen(false)}>
                                {common('cancel')}
                            </Button>
                            <Button onClick={handleAssignRole} disabled={!selectedRole || assignRoleMutation.isPending}>
                                {assignRoleMutation.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                                {common('save')}
                            </Button>
                        </DialogFooter>
                    </DialogContent>
                </Dialog>
            </CardContent>
        </Card>
    );
}