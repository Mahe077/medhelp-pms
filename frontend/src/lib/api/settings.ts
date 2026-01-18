import apiClient from "./client";

// Types
export interface ProfileSettings {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  licenseNumber?: string;
  role: string;
  userType: string;
  isActive: boolean;
  isEmailVerified: boolean;
}

export interface NotificationPreferences {
  id?: string;
  userId?: string;
  emailNotificationsEnabled: boolean;
  smsNotificationsEnabled: boolean;
  pushNotificationsEnabled: boolean;
  prescriptionReadyEmail: boolean;
  prescriptionReadySms: boolean;
  prescriptionReadyPush: boolean;
  refillReminderEmail: boolean;
  refillReminderSms: boolean;
  refillReminderPush: boolean;
  refillReminderDaysBefore: number;
  marketingEmailsEnabled: boolean;
  promotionalSmsEnabled: boolean;
  systemAlertsEmail: boolean;
  systemAlertsPush: boolean;
}

export interface UserPreferences {
  preferredLanguage: string;
  preferredTheme: string;
}

export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface AuditLog {
  id: string;
  eventType: string;
  aggregateType: string;
  aggregateId: string;
  eventData: string;
  occurredAt: string;
  userId: string;
  userName: string;
  sequenceNumber: number;
}

export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

// API Functions

// Profile Settings
export const getProfileSettings = async (): Promise<ProfileSettings> => {
  const response = await apiClient.get("/api/settings/profile");
  return response.data;
};

export const updateProfileSettings = async (
  data: Partial<ProfileSettings>,
): Promise<ProfileSettings> => {
  const response = await apiClient.put("/api/settings/profile", data);
  return response.data;
};

// Security
export const changePassword = async (
  data: PasswordChangeRequest,
): Promise<void> => {
  await apiClient.put("/api/settings/security/password", data);
};

// Notification Preferences
export const getNotificationPreferences =
  async (): Promise<NotificationPreferences> => {
    const response = await apiClient.get("/api/settings/notifications");
    return response.data;
  };

export const updateNotificationPreferences = async (
  data: NotificationPreferences,
): Promise<NotificationPreferences> => {
  const response = await apiClient.put("/api/settings/notifications", data);
  return response.data;
};

// User Preferences
export const getUserPreferences = async (): Promise<UserPreferences> => {
  const response = await apiClient.get("/api/settings/preferences");
  return response.data;
};

export const updateUserPreferences = async (
  data: UserPreferences,
): Promise<UserPreferences> => {
  const response = await apiClient.put("/api/settings/preferences", data);
  return response.data;
};

// Audit Trail
export const getAuditTrail = async (
  page: number = 0,
  size: number = 20,
): Promise<PageResponse<AuditLog>> => {
  const response = await apiClient.get("/api/settings/audit-trail", {
    params: { page, size },
  });
  return response.data;
};

// React Query Hooks
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";

export const useProfileSettings = () => {
  return useQuery({
    queryKey: ["profile-settings"],
    queryFn: getProfileSettings,
  });
};

export const useUpdateProfileSettings = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: updateProfileSettings,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["profile-settings"] });
      queryClient.invalidateQueries({ queryKey: ["current-user"] });
    },
  });
};

export const useChangePassword = () => {
  return useMutation({
    mutationFn: changePassword,
  });
};

export const useNotificationPreferences = () => {
  return useQuery({
    queryKey: ["notification-preferences"],
    queryFn: getNotificationPreferences,
  });
};

export const useUpdateNotificationPreferences = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: updateNotificationPreferences,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notification-preferences"] });
    },
  });
};

export const useUserPreferences = () => {
  return useQuery({
    queryKey: ["user-preferences"],
    queryFn: getUserPreferences,
  });
};

export const useUpdateUserPreferences = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: updateUserPreferences,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["user-preferences"] });
      queryClient.invalidateQueries({ queryKey: ["current-user"] });
    },
  });
};

export const useAuditTrail = (page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: ["audit-trail", page, size],
    queryFn: () => getAuditTrail(page, size),
  });
};
