interface User {
    id: string;
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    phone: string;
    role: string;
    userType: "INTERNAL" | "EXTERNAL";
    isActive: boolean;
    isEmailVerified: boolean;
    preferredLanguage: string;
    preferredTheme: string;
    permissions: string[];
}

interface AuthState {
    user: User | null;
    accessToken: string | null;
    refreshToken: string | null;
    setAuth: (user: User, accessToken: string, refreshToken: string) => void;
    clearAuth: () => void;
    isAuthenticated: () => boolean;
    hasPermission: (permission: string) => boolean;
    hasRole: (role: string) => boolean;
}

interface LoginRequest {
    usernameOrEmail: string;
    password: string;
}

interface LoginResponse {
    accessToken: string;
    refreshToken: string;
    expiresIn: number;
    user: {
        id: string;
        username: string;
        email: string;
        firstName: string;
        lastName: string;
        phone: string;
        role: string;
        userType: "INTERNAL" | "EXTERNAL";
        isActive: boolean;
        isEmailVerified: boolean;
        preferredLanguage: string;
        preferredTheme: string;
        permissions: string[];
    };
}
