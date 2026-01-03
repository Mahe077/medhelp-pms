"use client";

import Link from "next/link";
import { useTranslations, useLocale } from "next-intl";
import { Pill, Home, MoveLeft, HelpCircle } from "lucide-react";
import { getButtonClasses } from "@/components/ui/Button";
import { ThemeToggle } from "@/components/ThemeToggle";
import { LanguageToggle } from "@/components/LanguageToggle";

export function NotFoundContent({ locale }: { locale: string }) {
    const t = useTranslations("NotFound");

    return (
        <div className="relative flex min-h-screen flex-col items-center justify-center overflow-hidden bg-background p-6">
            {/* Top Right Controls */}
            <div className="absolute top-4 right-4 flex items-center gap-2 z-50">
                <LanguageToggle />
                <ThemeToggle />
            </div>

            {/* Background Decorative Blobs */}
            <div className="absolute top-[-10%] left-[-10%] h-[500px] w-[500px] rounded-full bg-primary/5 blur-[120px] animate-pulse" />
            <div className="absolute bottom-[-10%] right-[-10%] h-[400px] w-[400px] rounded-full bg-chart-1/5 blur-[100px] animate-pulse delay-700" />

            <div className="relative z-10 flex flex-col items-center text-center">
                {/* 404 Graphics */}
                <div className="relative mb-8">
                    <h1 className="text-[150px] font-black leading-none tracking-tighter text-foreground/5 sm:text-[200px]">
                        404
                    </h1>
                    <div className="absolute inset-0 flex items-center justify-center">
                        <div className="relative flex h-24 w-24 items-center justify-center rounded-3xl bg-primary shadow-2xl shadow-primary/40 rotate-12 transition-transform hover:rotate-0 duration-500">
                            <Pill className="h-12 w-12 text-primary-foreground" />
                            <div className="absolute -top-2 -right-2 h-6 w-6 rounded-full bg-chart-2 animate-bounce" />
                        </div>
                    </div>
                </div>

                {/* Text Content */}
                <div className="max-w-md space-y-4">
                    <h2 className="text-3xl font-extrabold tracking-tight sm:text-4xl">
                        {t('title')}
                    </h2>
                    <p className="text-lg text-muted-foreground">
                        {t('description')}
                    </p>
                </div>

                {/* Actions */}
                <div className="mt-10 flex flex-col gap-4 sm:flex-row">
                    <Link
                        href={`/${locale}`}
                        className={getButtonClasses("primary", "lg", "rounded-full px-8 shadow-lg shadow-primary/20")}
                    >
                        <Home className="mr-2 h-4 w-4" />
                        {t('backToDashboard')}
                    </Link>
                    <Link
                        href={`/${locale}/help`}
                        className={getButtonClasses("outline", "lg", "rounded-full px-8 backdrop-blur-sm")}
                    >
                        <HelpCircle className="mr-2 h-4 w-4" />
                        {t('contactSupport')}
                    </Link>
                </div>

                {/* Subtle back link */}
                <button
                    onClick={() => window.history.back()}
                    className="mt-8 flex items-center gap-2 text-sm font-medium text-muted-foreground hover:text-primary transition-colors"
                >
                    <MoveLeft className="h-4 w-4" />
                    {t('goBack')}
                </button>
            </div>

            {/* Bottom Branding */}
            <div className="absolute bottom-8 text-sm font-medium text-muted-foreground/40 italic">
                MedHelp Pharmacy Management System (v1.6.0)
            </div>
        </div>
    );
}

export function NotFoundPage() {
    // We don't need useLocale here anymore if purely client-side rendering the content, 
    // but keeping it consistent with the previous structure.
    // However, for Next-Intl clientside, we usually wrap in NextIntlClientProvider or similar if not already.
    // Assuming the parent layout provides it. UseTranslations hook works inside the provider.
    // We just pass locale for the link generation.
    // Actually useTranslations needs to run inside a component that is under the provider.
    // If NotFoundPage is rendered by root global-error or not-found, it might miss provider.
    // But let's stick to the request.
    const locale = useLocale();

    return <NotFoundContent locale={locale} />;
}
