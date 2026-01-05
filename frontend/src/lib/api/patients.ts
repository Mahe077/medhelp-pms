import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import apiClient from "./client";
import { CreatePatientRequest } from "@/lib/interfaces/patient";
import { toast } from "sonner";

export function usePatients(params?: {
  page?: number;
  pageSize?: number;
  search?: string;
}) {
  return useQuery({
    queryKey: ["patients", params],
    queryFn: async () => {
      const response = await apiClient.get("/patients", { params });
      return response.data;
    },
  });
}

export function usePatient(id: string) {
  return useQuery({
    queryKey: ["patients", id],
    queryFn: async () => {
      const response = await apiClient.get(`/patients/${id}`);
      return response.data.data;
    },
    enabled: !!id,
  });
}

export function useCreatePatient() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: CreatePatientRequest) => {
      const response = await apiClient.post("/patients", data);
      return response.data.data;
    },
    onSuccess: () => {
      toast.success("Patient created successfully");
      queryClient.invalidateQueries({ queryKey: ["patients"] });
    },
    onError: (error: any) => {
      const message =
        error.response?.data?.error?.message || "Failed to create patient";
      toast.error(message);
    },
  });
}

export function useUpdatePatient(id: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: Partial<CreatePatientRequest>) => {
      const response = await apiClient.patch(`/patients/${id}`, data);
      return response.data.data;
    },
    onSuccess: () => {
      toast.success("Patient updated successfully");
      queryClient.invalidateQueries({ queryKey: ["patients"] });
      queryClient.invalidateQueries({ queryKey: ["patients", id] });
    },
    onError: (error: any) => {
      const message =
        error.response?.data?.error?.message || "Failed to update patient";
      toast.error(message);
    },
  });
}
