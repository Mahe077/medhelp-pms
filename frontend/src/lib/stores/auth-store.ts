import {create} from 'zustand';
import { persist } from 'zustand/middleware';

export const useAuthStore = create<AuthState>()(
    persist(
        (set, get) => ({
            user: null,
            accessToken: null,
            refreshToken: null,

            setAuth: (user, accessToken, refreshToken) => {
                set({ user, accessToken, refreshToken });
                localStorage.setItem('accessToken', accessToken);
                localStorage.setItem('refreshToken', refreshToken);
            },

            clearAuth: () => {
                set({ user: null, accessToken: null, refreshToken: null });
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
            },

            isAuthenticated: () => {
                const { accessToken } = get();
                return !!accessToken;
            },

            hasPermission: (permission) => {
                const { user } = get();
                return user?.permissions?.includes(permission) || false;
            },

            hasRole: (role) => {
                const { user } = get();
                return user?.role === role;
            },
        }),
        {
            name: 'auth-storage',
        }
    )
);
