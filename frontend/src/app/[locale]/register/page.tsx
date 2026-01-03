"use client";

import React, { useState } from "react";
import Link from "next/link";
import { Mail, Lock, User as UserIcon, Phone, UserPlus, Pill, ShieldCheck, Activity } from "lucide-react";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/Card";
import { Label } from "@/components/ui/Label";
import { useTranslations } from "next-intl";
import { ThemeToggle } from "@/components/ThemeToggle";
import { LanguageToggle } from "@/components/LanguageToggle";
import { useRouter } from "@/i18n/routing";
import { useAuth } from "@/hooks/useAuth";

export default function RegisterPage() {
    const t = useTranslations("Register");
    const router = useRouter();
    const { register } = useAuth();

    const [formData, setFormData] = useState({
        username: "",
        email: "",
        password: "",
        firstName: "",
        lastName: "",
        phone: ""
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState(false);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.id]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        try {
            await register(formData);
            setSuccess(true);
        } catch (err: unknown) {
            const message = err instanceof Error ? err.message : "Something went wrong";
            setError(message);
        } finally {
            setLoading(false);
        }
    };

    if (success) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-background p-4">
                <Card className="w-full max-w-md border-border/50 bg-card/80 shadow-2xl backdrop-blur-xl text-center">
                    <CardHeader>
                        <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-primary/20">
                            <ShieldCheck className="h-10 w-10 text-primary" />
                        </div>
                        <CardTitle className="text-2xl font-bold">{t("title")}</CardTitle>
                        <CardDescription className="text-lg">
                            {t("description")}
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <p className="text-muted-foreground">Registration successful! Please check your email to verify your account.</p>
                    </CardContent>
                    <CardFooter>
                        <Button className="w-full" onClick={() => router.push("/login")}>
                            {t("signIn")}
                        </Button>
                    </CardFooter>
                </Card>
            </div>
        );
    }

    return (
        <div className="relative flex min-h-screen items-center justify-center overflow-hidden bg-background p-4 sm:p-8">
            {/* Top Controls */}
            <div className="absolute top-4 right-4 z-50 flex items-center gap-4 sm:top-8 sm:right-8">
                <LanguageToggle />
                <ThemeToggle />
            </div>

            {/* Background Decorative Elements */}
            <div className="absolute top-[10%] right-[10%] h-[40%] w-[40%] rounded-full bg-primary/10 blur-[120px]" />
            <div className="absolute bottom-[-5%] left-[-5%] h-[30%] w-[30%] rounded-full bg-chart-1/10 blur-[100px]" />

            <div className="relative z-10 grid w-full max-w-6xl gap-8 lg:grid-cols-2 lg:items-start">
                {/* Left Side: Onboarding Info */}
                <div className="hidden flex-col justify-center space-y-8 pt-12 lg:flex">
                    <div className="flex items-center gap-3">
                        <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary shadow-lg shadow-primary/30">
                            <Pill className="h-7 w-7 text-primary-foreground" />
                        </div>
                        <h1 className="text-3xl font-bold tracking-tight">{t("brandingTitle")}</h1>
                    </div>

                    <div className="space-y-4">
                        <h2 className="text-5xl font-extrabold leading-tight text-foreground">
                            {t.rich("brandingHeadline", {
                                span: (chunks) => <span className="text-primary">{chunks}</span>
                            })}
                        </h2>
                        <p className="max-w-[500px] text-xl text-muted-foreground">
                            {t("brandingSubheadline")}
                        </p>
                    </div>

                    <div className="space-y-6 pt-4">
                        <div className="flex items-start gap-4 rounded-xl border border-border/50 bg-card/30 p-5 backdrop-blur-sm transition-all hover:bg-card/40">
                            <div className="mt-1 flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                                <ShieldCheck className="h-6 w-6 text-primary" />
                            </div>
                            <div>
                                <h3 className="text-lg font-bold">{t("secureOnboarding")}</h3>
                                <p className="text-sm text-muted-foreground">Advanced encryption for all clinical data on-boarding.</p>
                            </div>
                        </div>
                        <div className="flex items-start gap-4 rounded-xl border border-border/50 bg-card/30 p-5 backdrop-blur-sm transition-all hover:bg-card/40">
                            <div className="mt-1 flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                                <Activity className="h-6 w-6 text-primary" />
                            </div>
                            <div>
                                <h3 className="text-lg font-bold">{t("verifyIdentity")}</h3>
                                <p className="text-sm text-muted-foreground">Ensure high-standard healthcare compliance with identity verification.</p>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Right Side: Register Form */}
                <div className="flex items-center justify-center lg:pt-0">
                    <Card className="w-full max-w-xl border-border/50 bg-card/80 shadow-2xl backdrop-blur-xl">
                        <CardHeader className="space-y-1 text-center">
                            <CardTitle className="text-3xl font-bold tracking-tight">{t("title")}</CardTitle>
                            <CardDescription className="text-base text-muted-foreground">
                                {t("description")}
                            </CardDescription>
                        </CardHeader>
                        <form onSubmit={handleSubmit}>
                            <CardContent className="grid gap-4 sm:grid-cols-2">
                                {error && (
                                    <div className="col-span-full rounded-lg bg-destructive/10 p-3 text-center text-sm font-medium text-destructive">
                                        {error}
                                    </div>
                                )}
                                <div className="space-y-2">
                                    <Label htmlFor="firstName">{t("firstNameLabel")}</Label>
                                    <Input
                                        id="firstName"
                                        placeholder={t("firstNamePlaceholder")}
                                        icon={<UserIcon className="h-4 w-4" />}
                                        value={formData.firstName}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>
                                <div className="space-y-2">
                                    <Label htmlFor="lastName">{t("lastNameLabel")}</Label>
                                    <Input
                                        id="lastName"
                                        placeholder={t("lastNamePlaceholder")}
                                        icon={<UserIcon className="h-4 w-4" />}
                                        value={formData.lastName}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>
                                <div className="col-span-full space-y-2">
                                    <Label htmlFor="username">{t("usernameLabel")}</Label>
                                    <Input
                                        id="username"
                                        placeholder={t("usernamePlaceholder")}
                                        icon={<UserIcon className="h-4 w-4" />}
                                        value={formData.username}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>
                                <div className="col-span-full space-y-2">
                                    <Label htmlFor="email">{t("emailLabel")}</Label>
                                    <Input
                                        id="email"
                                        type="email"
                                        placeholder={t("emailPlaceholder")}
                                        icon={<Mail className="h-4 w-4" />}
                                        value={formData.email}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>
                                <div className="space-y-2">
                                    <Label htmlFor="phone">{t("phoneLabel")}</Label>
                                    <Input
                                        id="phone"
                                        placeholder={t("phonePlaceholder")}
                                        icon={<Phone className="h-4 w-4" />}
                                        value={formData.phone}
                                        onChange={handleChange}
                                    />
                                </div>
                                <div className="space-y-2">
                                    <Label htmlFor="password">{t("passwordLabel")}</Label>
                                    <Input
                                        id="password"
                                        type="password"
                                        placeholder={t("passwordPlaceholder")}
                                        icon={<Lock className="h-4 w-4" />}
                                        value={formData.password}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>
                            </CardContent>
                            <CardFooter className="flex flex-col space-y-4">
                                <Button className="w-full text-base font-semibold" size="lg" type="submit" disabled={loading}>
                                    <UserPlus className="mr-2 h-5 w-5" /> {loading ? "Processing..." : t("submit")}
                                </Button>
                                <div className="text-center text-sm text-muted-foreground">
                                    {t("alreadyHaveAccount")}{" "}
                                    <Link
                                        href="/login"
                                        className="font-medium text-primary hover:underline underline-offset-4"
                                    >
                                        {t("signIn")}
                                    </Link>
                                </div>
                            </CardFooter>
                        </form>
                    </Card>
                </div>
            </div>
        </div>
    );
}