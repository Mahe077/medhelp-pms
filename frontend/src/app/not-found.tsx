"use client";

import "./globals.css";
import { NotFoundContent } from "@/components/NotFoundPage";

export default function GlobalNotFound() {
    return (
        <html lang="en">
            <body>
                <NotFoundContent locale="en" />
            </body>
        </html>
    );
}
