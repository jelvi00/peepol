import { test, expect } from '@playwright/test';

test.describe('Login Flow', () => {
  test('should show login page with all elements', async ({ page }) => {
    await page.goto('/login');

    // Check title or some identifying text
    await expect(page).toHaveTitle(/Peepol/);

    // Check form elements
    await expect(page.getByPlaceholder('Username')).toBeVisible();
    await expect(page.getByPlaceholder('Password')).toBeVisible();
    await expect(page.getByRole('button', { name: /Sign in/i })).toBeVisible();
  });

  test('should show error message on invalid credentials', async ({ page }) => {
    await page.goto('/login');

    await page.getByPlaceholder('Username').fill('invalid_user');
    await page.getByPlaceholder('Password').fill('invalid_password');
    await page.getByRole('button', { name: /Sign in/i }).click();

    // The error message should appear. According to LoginForm.tsx, it uses <p className="text-red-500">
    const errorMessage = page.locator('p.text-red-500');
    await expect(errorMessage).toBeVisible();
    await expect(errorMessage).not.toBeEmpty();
  });

  test('should allow toggling password visibility', async ({ page }) => {
    await page.goto('/login');

    const passwordInput = page.getByPlaceholder('Password');
    await expect(passwordInput).toHaveAttribute('type', 'password');

    // Click the eye icon button
    await page.locator('button').filter({ has: page.locator('img[alt="eye"]') }).click();
    await expect(passwordInput).toHaveAttribute('type', 'text');

    await page.locator('button').filter({ has: page.locator('img[alt="eye-slash"]') }).click();
    await expect(passwordInput).toHaveAttribute('type', 'password');
  });

  test('should login successfully with mocked action', async ({ page, context }) => {

    // Intercept the POST request to /login (Server Action)
    await page.route('**/login', async (route) => {
      if (route.request().method() === 'POST') {

        await context.addCookies([
          { name: 'authToken', value: 'mocked-token', domain: 'localhost', path: "/" },
          { name: 'authRole', value: 'USER', domain: 'localhost', path: "/" },
        ])
        await route.fulfill({
          status: 200,
          contentType: 'text/x-component',
          body: '1:{"success":true,"error":"","content":{"user":{"token":"fake-token","id":2,"username":"aricardo","role":"USER"}}}\n'
        });
      } else {
        await route.continue();
      }
    });

    await page.goto('/login');

    await page.getByPlaceholder('Username').fill('testuser');
    await page.getByPlaceholder('Password').fill('Password123!');
    await page.getByRole('button', { name: /Sign in/i }).click();

    await page.goto('/dashboard');
    await expect(page.locator('h1')).toContainText('Peepol Dashboard');

    // After successful login, it should redirect to dashboard
    await expect(page).toHaveURL(/.*dashboard/, { timeout: 10000 });
    await expect(page.locator('h1')).toContainText('Peepol Dashboard');
  });
});

test.describe('Authentication Guard', () => {
  test('should redirect unauthenticated users from dashboard to login', async ({ page }) => {
    await page.goto('/dashboard');
    await expect(page).toHaveURL(/.*login/);
  });
});
