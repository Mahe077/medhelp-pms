"use client";

import React, { useState } from "react";
import Link from "next/link";
import { Mail, Lock, LogIn, Pill, Activity, ShieldCheck } from "lucide-react";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/Card";
import { Label } from "@/components/ui/Label";
import { useTranslations } from "next-intl";
import { ThemeToggle } from "@/components/ThemeToggle";
import { LanguageToggle } from "@/components/LanguageToggle";

import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useLogin } from "@/lib/api/auth";
import { useForm } from "react-hook-form";
import { toast } from 'sonner';

const loginSchema = z.object({
    usernameOrEmail: z.string().min(3, 'Username must be at least 3 characters'),
    password: z.string().min(6, 'Password must be at least 6 characters'),
});

type LoginFormData = z.infer<typeof loginSchema>;

export default function LoginPage() {
    const t = useTranslations("Login");

    const login = useLogin();

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<LoginFormData>({
        resolver: zodResolver(loginSchema),
    });

    const [loading, setLoading] = useState(false);
    const [serverError, setServerError] = useState<string | null>(null);

    const onSubmit = async (data: LoginFormData) => {
        setServerError(null);
        setLoading(true);
        try {
            await login.mutateAsync(data);
        } catch (error: unknown) {
            const message = error instanceof Error ? error.message : "Invalid credentials";
            toast.error(message);
            setServerError(message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="relative flex min-h-screen items-center justify-center overflow-hidden bg-background p-4 sm:p-8">
            {/* Top Controls */}
            <div className="absolute top-4 right-4 z-50 flex items-center gap-4 sm:top-8 sm:right-8">
                <LanguageToggle />
                <ThemeToggle />
            </div>
            {/* Background Decorative Elements */}
            <div className="absolute top-[-10%] left-[-10%] h-[40%] w-[40%] rounded-full bg-primary/10 blur-[120px]" />
            <div className="absolute bottom-[-10%] right-[-10%] h-[40%] w-[40%] rounded-full bg-chart-2/10 blur-[120px]" />

            <div className="relative z-10 grid w-full max-w-5xl gap-8 lg:grid-cols-2 lg:items-center">
                {/* Left Side: Branding / Info */}
                <div className="hidden flex-col justify-center space-y-6 lg:flex">
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

                    <div className="grid grid-cols-2 gap-4 pt-4">
                        <div className="flex items-center gap-2 rounded-lg border border-border/50 bg-card/30 p-4 backdrop-blur-sm">
                            <Activity className="h-5 w-5 text-primary" />
                            <span className="text-sm font-medium">{t("analytics")}</span>
                        </div>
                        <div className="flex items-center gap-2 rounded-lg border border-border/50 bg-card/30 p-4 backdrop-blur-sm">
                            <ShieldCheck className="h-5 w-5 text-primary" />
                            <span className="text-sm font-medium">{t("secureRecords")}</span>
                        </div>
                    </div>
                </div>

                {/* Right Side: Login Form */}
                <div className="flex items-center justify-center">
                    <Card className="w-full max-w-md border-border/50 bg-card/80 shadow-2xl backdrop-blur-xl">
                        <CardHeader className="space-y-1 text-center">
                            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-primary/10 lg:hidden">
                                <Pill className="h-6 w-6 text-primary" />
                            </div>
                            <CardTitle className="text-3xl font-bold tracking-tight">{t("title")}</CardTitle>
                            <CardDescription className="text-base text-muted-foreground">
                                {t("description")}
                            </CardDescription>
                        </CardHeader>
                        <form onSubmit={handleSubmit(onSubmit)}>
                            <CardContent className="space-y-4">
                                {serverError && (
                                    <div className="rounded-lg bg-destructive/10 p-3 text-center text-sm font-medium text-destructive">
                                        {serverError}
                                    </div>
                                )}
                                <div className="space-y-2">
                                    <Label htmlFor="usernameOrEmail">{t("usernameLabel")}</Label>
                                    <Input
                                        {...register('usernameOrEmail')}
                                        id="usernameOrEmail"
                                        placeholder={t("usernamePlaceholder")}
                                        icon={<Mail className="h-4 w-4" />}
                                        required
                                    />
                                </div>
                                <div className="space-y-2">
                                    <div className="flex items-center justify-between">
                                        <Label htmlFor="password">{t("passwordLabel")}</Label>
                                        <Link
                                            href="/forgot-password"
                                            className="text-xs font-medium text-primary hover:underline underline-offset-4"
                                        >
                                            {t("forgotPassword")}
                                        </Link>
                                    </div>
                                    <Input
                                        {...register('password')}
                                        id="password"
                                        type="password"
                                        placeholder={t("passwordPlaceholder")}
                                        icon={<Lock className="h-4 w-4" />}
                                        required
                                    />
                                </div>
                                <div className="flex items-center space-x-2">
                                    {/*    <input*/}
                                    {/*        type="checkbox"*/}
                                    {/*        id="remember"*/}
                                    {/*        className="h-4 w-4 rounded border-input bg-background/50 text-primary accent-primary outline-none focus:ring-2 focus:ring-primary/50"*/}
                                    {/*        checked={formData.remember}*/}
                                    {/*        onChange={handleChange}*/}
                                    {/*    />*/}
                                    {/*    <Label htmlFor="remember" className="text-sm font-normal text-muted-foreground">*/}
                                    {/*        {t("rememberMe")}*/}
                                    {/*    </Label>*/}
                                </div>
                            </CardContent>
                            <CardFooter className="flex flex-col space-y-4">
                                <Button className="w-full text-base font-semibold" size="lg" type="submit" disabled={loading}>
                                    <LogIn className="mr-2 h-5 w-5" /> {loading ? "Signing in..." : t("signIn")}
                                </Button>
                                <div className="relative w-full">
                                    <div className="absolute inset-0 flex items-center">
                                        <span className="w-full border-t border-border" />
                                    </div>
                                    <div className="relative flex justify-center text-xs uppercase">
                                        <span className="bg-card px-2 text-muted-foreground">{t("orContinueWith")}</span>
                                    </div>
                                </div>
                                <div className="text-center text-sm text-muted-foreground">
                                    {t("noAccount")}{" "}
                                    <Link
                                        href="/register"
                                        className="font-medium text-primary hover:underline underline-offset-4"
                                    >
                                        {t("signup")}
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
