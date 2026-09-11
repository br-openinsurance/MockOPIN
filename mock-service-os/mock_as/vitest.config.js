import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    globals: true,
    include: ['**/__tests__/**/*.test.js'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'html'],
      include: ['utils/**/*.js'],
      exclude: ['utils/**/__tests__/**'],
      reportsDirectory: './coverage',
    },
  },
});
