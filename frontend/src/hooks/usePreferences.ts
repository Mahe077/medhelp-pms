"use client";

import { useTheme } from "next-themes";
import { usePathname, useRouter } from "@/i18n/routing";
import { authService, User } from "@/lib/auth";
import { useLocale } from "next-intl";
import { useCallback } from "react";

export function usePreferences() {
  const { setTheme, theme } = useTheme();
  const currentLocale = useLocale();
  const pathname = usePathname();
  const router = useRouter();

  const updatePreferences = useCallback(
    async (newPrefs: { language?: string; theme?: string }) => {
      const user = authService.getCurrentUser();

      // Optimistically update local state
      if (newPrefs.theme) {
        setTheme(newPrefs.theme);
      }

      if (newPrefs.language && newPrefs.language !== currentLocale) {
        router.push(pathname, { locale: newPrefs.language });
      }

      // If authenticated, sync with backend
      if (user) {
        try {
          await authService.updatePreferences({
            preferredLanguage: newPrefs.language,
            preferredTheme: newPrefs.theme,
          });
        } catch (err) {
          console.error("Failed to sync preferences with backend", err);
        }
      }
    },
    [currentLocale, pathname, router, setTheme]
  );

  const applyStoredPreferences = useCallback(
    (user: User) => {
      // Apply theme
      if (user.preferredTheme) {
        setTheme(user.preferredTheme);
      }

      // Apply language
      if (user.preferredLanguage && user.preferredLanguage !== currentLocale) {
        router.replace(pathname, { locale: user.preferredLanguage });
      }
    },
    [currentLocale, pathname, router, setTheme]
  );

  return {
    updatePreferences,
    applyStoredPreferences,
    currentTheme: theme,
    currentLanguage: currentLocale,
  };
}
