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