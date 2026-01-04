import { useMutation, useQuery } from '@tanstack/react-query';
import apiClient from './client';
import { useAuthStore } from '@/lib/stores/auth-store';
import { useRouter } from 'next/navigation';

export function useLogin() {
    const setAuth = useAuthStore((state) => state.setAuth);
    const router = useRouter();

    return useMutation({
        mutationFn: async (credentials: LoginRequest) => {
            const response = await apiClient.post<{ data: LoginResponse}>(
                '/auth/login',
                credentials,
            );
            return response.data.data;
        },
        onSuccess: (data) => {
            setAuth(data.user, data.accessToken, data.refreshToken);
            router.push('/dashboard');
        }
    })
}

export function useLogout() {
    const clearAuth = useAuthStore((state) => state.clearAuth);
    const router = useRouter();

    return useMutation({
        mutationFn: async () => {
            await apiClient.post('/auth/logout');
        },
        onSuccess: (data) => {
            clearAuth();
            router.push('/login');
        }
    })
}

export function useCurrentUser() {
    const user = useAuthStore((state) => state.user);
    const isAuthenticated = useAuthStore((state) => state.isAuthenticated());

    return useQuery({
        queryKey: ['currentUser'],
        queryFn: async () => {
            const response = await apiClient.get('/users/me');
            return response.data.data;
        },
        enabled: isAuthenticated && !user,
        staleTime: Infinity,
    });
}