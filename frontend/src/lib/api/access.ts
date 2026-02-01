import apiClient from "./client";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";

// --- Types ---

interface ApiResponse<T> {
  data: T;
  message?: string;
}

export interface Permission {
  id: string;
  name: string;
  description: string;
  resource: string;
  action: string;
}

export interface Role {
  id: string;
  name: string;
  description: string;
  isSystem: boolean;
  permissions?: Permission[];
}

export interface User {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: Role[];
  isActive: boolean;
  lastLoginAt: string;
}

export interface CreateRoleRequest {
  name: string;
  description: string;
}

export interface UpdateRolePermissionsRequest {
  permissionIds: string[];
}

// Users
export const getUsers = async (): Promise<User[]> => {
  const response = await apiClient.get<ApiResponse<User[]>>("/access/users");
  return response.data.data;
};

export const assignRoleToUser = async (
  userId: string,
  roleId: string,
): Promise<void> => {
  await apiClient.post(`/access/users/${userId}/roles/${roleId}`);
};

export const removeRoleFromUser = async (
  userId: string,
  roleId: string,
): Promise<void> => {
  await apiClient.delete(`/access/users/${userId}/roles/${roleId}`);
};

// --- API Functions ---

// Roles
export const getRoles = async (): Promise<Role[]> => {
  const response = await apiClient.get<ApiResponse<Role[]>>("/access/roles");
  return response.data.data;
};

export const createRole = async (data: CreateRoleRequest): Promise<Role> => {
  const response = await apiClient.post<ApiResponse<Role>>(
    "/access/roles",
    data,
  );
  return response.data.data;
};

export const updateRolePermissions = async (
  roleId: string,
  permissionIds: string[],
): Promise<Role> => {
  const response = await apiClient.put<ApiResponse<Role>>(
    `/access/roles/${roleId}/permissions`,
    { permissionIds },
  );
  return response.data.data;
};

// Permissions
export const getPermissions = async (): Promise<Permission[]> => {
  const response = await apiClient.get<ApiResponse<Permission[]>>(
    "/access/permissions",
  );
  return response.data.data;
};

export interface CreatePermissionRequest {
  name: string;
  description: string;
  resource: string;
  action: string;
}

export const createPermission = async (
  data: CreatePermissionRequest,
): Promise<Permission> => {
  const response = await apiClient.post<ApiResponse<Permission>>(
    "/access/permissions",
    data,
  );
  return response.data.data;
};

export const assignPermissionToRole = async (
  roleId: string,
  permissionId: string,
): Promise<void> => {
  await apiClient.post(`/access/roles/${roleId}/permissions`, { permissionId });
};

export const removePermissionFromRole = async (
  roleId: string,
  permissionId: string,
): Promise<void> => {
  await apiClient.delete(`/access/roles/${roleId}/permissions/${permissionId}`);
};

// Audit Logs
export interface AuditLog {
  id: string;
  eventType: string;
  description: string;
  aggregateType?: string;
  aggregateId?: string;
  occurredAt: string;
  userId?: string;
  userName?: string;
  eventData?: string;
  sequenceNumber?: number;
}

export const getAuditLogs = async (): Promise<AuditLog[]> => {
  const response =
    await apiClient.get<ApiResponse<AuditLog[]>>("/access/audit-logs");
  return response.data.data;
};

export const getPermissionsForRole = async (
  roleId: string,
): Promise<Permission[]> => {
  const response = await apiClient.get<ApiResponse<Permission[]>>(
    `/access/roles/${roleId}/permissions`,
  );
  return response.data.data;
};

// --- Hooks ---

export const useRoles = () => {
  return useQuery({
    queryKey: ["roles"],
    queryFn: getRoles,
  });
};

export const useRolePermissions = (roleId: string | null) => {
  return useQuery({
    queryKey: ["role-permissions", roleId],
    queryFn: () => getPermissionsForRole(roleId!),
    enabled: !!roleId,
  });
};

export const usePermissions = () => {
  return useQuery({
    queryKey: ["permissions"],
    queryFn: getPermissions,
  });
};

export const useAuditLogs = () => {
  return useQuery({
    queryKey: ["audit-logs"],
    queryFn: getAuditLogs,
  });
};

export const useCreateRole = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createRole,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["roles"] });
    },
  });
};

export const useCreatePermission = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createPermission,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["permissions"] });
    },
  });
};

export const useAssignPermission = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      roleId,
      permissionId,
    }: {
      roleId: string;
      permissionId: string;
    }) => assignPermissionToRole(roleId, permissionId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["roles"] });
    },
  });
};

export const useRemovePermission = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      roleId,
      permissionId,
    }: {
      roleId: string;
      permissionId: string;
    }) => removePermissionFromRole(roleId, permissionId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["roles"] });
    },
  });
};

export const useUsers = () => {
  return useQuery({
    queryKey: ["users"],
    queryFn: getUsers,
  });
};

export const useAssignRoleToUser = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ userId, roleId }: { userId: string; roleId: string }) =>
      assignRoleToUser(userId, roleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
    },
  });
};

export const useRemoveRoleFromUser = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ userId, roleId }: { userId: string; roleId: string }) =>
      removeRoleFromUser(userId, roleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
    },
  });
};
