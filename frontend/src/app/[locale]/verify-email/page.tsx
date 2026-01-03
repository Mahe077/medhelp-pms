"use client";

import React, { useEffect, useState } from "react";
import { useSearchParams } from "next/navigation";
import { useRouter } from "@/i18n/routing";
import { ShieldCheck, ShieldAlert, Loader2 } from "lucide-react";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";

export default function VerifyEmailPage() {
    const searchParams = useSearchParams();
    const router = useRouter();
    const token = searchParams.get("token");
    const [status, setStatus] = useState<'loading' | 'success' | 'error' | 'resending'>('loading');
    const [message, setMessage] = useState("Verifying your email...");
    const [email, setEmail] = useState("");

    useEffect(() => {
        if (!token) {
            const timer = setTimeout(() => {
                setStatus('error');
                setMessage("Invalid or missing verification token.");
            }, 0);
            return () => clearTimeout(timer);
        }

        const verify = async () => {
            try {
                const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL || "http://localhost:8000/api/v1"}/auth/verify-email?token=${token}`, {
                    method: "POST"
                });
                const data = await response.json();
                if (data.success) {
                    setStatus('success');
                    setMessage("Your email has been successfully verified! You can now sign in.");
                } else {
                    setStatus('error');
                    // Check if it's an expiration message to suggest resending
                    if (data.message && data.message.toLowerCase().includes("expired")) {
                        setMessage("This verification link has expired. Please request a new one.");
                    } else {
                        setMessage(data.message || "Verification failed.");
                    }
                }
            } catch {
                setStatus('error');
                setMessage("Could not connect to the server. Please try again later.");
            }
        };

        verify();
    }, [token]);

    const handleResend = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!email) return;

        setStatus('resending');
        try {
            const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL || "http://localhost:8000/api/v1"}/auth/resend-verification?email=${encodeURIComponent(email)}`, {
                method: "POST"
            });
            const data = await response.json();
            if (data.success) {
                setMessage("A new verification link has been sent to " + email);
                setStatus('error'); // Keep in error/info state but updated message
            } else {
                setMessage(data.message || "Failed to resend verification email.");
                setStatus('error');
            }
        } catch {
            setMessage("Network error. Please try again.");
            setStatus('error');
        }
    };

    return (
        <div className="flex min-h-screen items-center justify-center bg-background p-4">
            <Card className="w-full max-w-md border-border/50 bg-card/80 shadow-2xl backdrop-blur-xl text-center">
                <CardHeader>
                    <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-primary/20">
                        {(status === 'loading' || status === 'resending') && <Loader2 className="h-10 w-10 text-primary animate-spin" />}
                        {status === 'success' && <ShieldCheck className="h-10 w-10 text-primary" />}
                        {status === 'error' && <ShieldAlert className="h-10 w-10 text-destructive" />}
                    </div>
                    <CardTitle className="text-2xl font-bold">
                        {status === 'loading' && "Verifying..."}
                        {status === 'resending' && "Sending..."}
                        {status === 'success' && "Verified!"}
                        {status === 'error' && "Verification Failed"}
                    </CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                    <p className="text-muted-foreground">{message}</p>

                    {(status === 'error' || status === 'resending') && (
                        <form onSubmit={handleResend} className="text-left space-y-2 pt-4 border-t border-border/50">
                            <Label htmlFor="resend-email">Enter email to resend link</Label>
                            <div className="flex gap-2">
                                <Input
                                    id="resend-email"
                                    type="email"
                                    placeholder="name@example.com"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                                <Button type="submit" variant="outline" disabled={status === 'resending'}>
                                    Resend
                                </Button>
                            </div>
                        </form>
                    )}
                </CardContent>
                <CardFooter>
                    <Button
                        className="w-full"
                        onClick={() => router.push("/login")}
                        variant={status === 'error' ? 'ghost' : 'primary'}
                    >
                        {status === 'success' ? "Go to Sign In" : "Back to Login"}
                    </Button>
                </CardFooter>
            </Card>
        </div>
    );
}
