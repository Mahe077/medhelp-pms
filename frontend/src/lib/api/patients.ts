import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from './client';
import {CreatePatientRequest} from "@/lib/interfaces/patient";

export function usePatients(
    params?: {
        page?: number;
        pageSize?: number;
        search?: string;
    }
) {
    return useQuery({
        queryKey: ['patients', params],
        queryFn: async () => {
            const response = await apiClient.get('/patients', {params});
            return response.data;
        },
    });
}

export function usePatient(id: string) {
    return useQuery({
        queryKey: ['patients', id],
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
            const response = await apiClient.post('/patients', data);
            return response.data.data;
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['patients'] });
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
            queryClient.invalidateQueries({ queryKey: ['patients'] });
            queryClient.invalidateQueries({ queryKey: ['patients', id] });
        },
    });
}