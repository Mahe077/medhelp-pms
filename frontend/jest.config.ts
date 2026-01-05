import type { Config } from "jest";
import nextJest from "next/jest.js";

const createJestConfig = nextJest({
  // Provide the path to your Next.js app to load next.config.js and .env files in your test environment
  dir: "./",
});

// Add any custom config to be passed to Jest
const config: Config = {
  coverageProvider: "v8",
  testEnvironment: "jsdom",

  // Setup files to run after Jest is has been set up
  setupFilesAfterEnv: ["<rootDir>/jest.setup.ts"],

  // Module name mapper for path aliases
  moduleNameMapper: {
    "^@/(.*)$": "<rootDir>/src/$1",
  },

  // Test file patterns
  testMatch: ["**/__tests__/**/*.[jt]s?(x)", "**/?(*.)+(spec|test).[jt]s?(x)"],

  // Ignore patterns
  testPathIgnorePatterns: ["<rootDir>/node_modules/", "<rootDir>/.next/"],

  // Coverage configuration
  collectCoverageFrom: [
    "src/**/*.{js,jsx,ts,tsx}",
    "!src/**/*.d.ts",
    "!src/**/index.ts",
    "!src/app/**/layout.tsx",
    "!src/app/**/loading.tsx",
  ],

  // Coverage thresholds
  coverageThreshold: {
    global: {
      branches: 50,
      functions: 50,
      lines: 50,
      statements: 50,
    },
  },
};

// createJestConfig is exported this way to ensure that next/jest can load the Next.js config which is async
export default createJestConfig(config);
