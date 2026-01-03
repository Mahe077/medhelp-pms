import { NextIntlClientProvider } from 'next-intl';
import { getMessages } from 'next-intl/server';
import "./globals.css";
import { NotFoundContent } from "@/components/NotFoundPage";
import { Providers } from "@/components/providers/Providers";

export default async function GlobalNotFound() {
    // Provide a default locale for the global 404 page (usually 'en')
    // or attempt to detect, but 'en' is safest for the fallback 404.
    const locale = 'en';
    const messages = await getMessages({ locale });

    return (
        <html lang={locale}>
            <body>
                <NextIntlClientProvider messages={messages} locale={locale}>
                    <Providers enableAuthGuard={false}>
                        <NotFoundContent locale={locale} />
                    </Providers>
                </NextIntlClientProvider>
            </body>
        </html>
    );
}
