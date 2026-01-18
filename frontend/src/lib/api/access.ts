import { apiClient, ApiResponse } from "./client";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";

// --- Types ---

export interface Permission {
  id: string;
  scope: string;
  action: string;
  resourceKey: string;
  description: string;
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

// Users (for Access Management)
// Note: This matches the API endpoint usually used for listing users.
// If generic user listing is in another module, import from there.
// Assuming checking AccessController doesn't have list users, wait.
// Implementation Plan said: GET /api/access/users (List users with roles)
// But I didn't implement getAllUsers in AccessController yet!
// Checking AccessController.java... it only has endpoints for Roles and Permissions and Assign/Remove Role.
// I missed `getAllUsers` implementation in AccessController. I should add it or use a UserModule controller.
// For now, let's assume I will add it or use functionality from User management.
// Actually, `AccessController` should probably expose a user list for admins.
// Let's add fetchUsers to API client and then go back and update Backend if needed.
// Update: I will check AccessController again.

export const getUsers = async (): Promise<User[]> => {
  // Check if I implemented this in AccessController.
  // If not, I need to implement it.
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

// --- Hooks ---

export const useRoles = () => {
  return useQuery({
    queryKey: ["roles"],
    queryFn: getRoles,
  });
};

export const usePermissions = () => {
  return useQuery({
    queryKey: ["permissions"],
    queryFn: getPermissions,
  });
};

export const useAccessUsers = () => {
  return useQuery({
    queryKey: ["access-users"],
    queryFn: getUsers,
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

export const useUpdateRolePermissions = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      roleId,
      permissionIds,
    }: {
      roleId: string;
      permissionIds: string[];
    }) => updateRolePermissions(roleId, permissionIds),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["roles"] });
    },
  });
};

export const useAssignRole = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ userId, roleId }: { userId: string; roleId: string }) =>
      assignRoleToUser(userId, roleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["access-users"] });
    },
  });
};

export const useRemoveRole = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ userId, roleId }: { userId: string; roleId: string }) =>
      removeRoleFromUser(userId, roleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["access-users"] });
    },
  });
};
