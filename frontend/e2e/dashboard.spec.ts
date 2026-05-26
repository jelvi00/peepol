import { test, expect } from '@playwright/test';

const DEFAULT_PERSONS = [
  { id: 1, name: 'Juan Garcia', phoneNumber: '+34 600000001', bio: 'Bio of Juan Garcia', status: 'ENABLED' },
  { id: 2, name: 'Maria Rodriguez', phoneNumber: '+34 600000002', bio: 'Bio of Maria Rodriguez', status: 'ENABLED' },
  { id: 3, name: 'Pedro Gonzalez', phoneNumber: '+34 600000003', bio: 'Bio of Pedro Gonzalez', status: 'ENABLED' }
]

test.describe('Person CRUD Operations', () => {
  let mockPersons = [ ...DEFAULT_PERSONS ];

  test.beforeEach(async ({ page, context }) => {

    mockPersons = [ ...DEFAULT_PERSONS ];

    await page.goto('/login');

    await context.addCookies([
      { name: 'authToken', value: 'fake-token', domain: 'localhost', path: '/' },
      { name: 'authRole', value: 'ADMIN', domain: 'localhost', path: '/' }
    ]);

    await page.addInitScript(() => {
      window.sessionStorage.setItem('session', JSON.stringify({
        token: 'fake-token',
        role: 'ADMIN',
        username: 'admin',
        id: 1
      }));
    });

    // Mock API Routes for persons
    await page.route('**/api/persons**', async (route) => {
      const method = route.request().method();
      const url = route.request().url();

      if (method === 'GET') {
        // Person Detail
        if (/\/api\/persons\/\d+$/.test(url)) {
          const id = parseInt(url.split('/').pop() || '0');
          const person = mockPersons.find(p => p.id === id);
          return route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify(person || null)
          });
        }
      }

      if (method === 'POST') {
        const newPerson = { id: Date.now(), ...route.request().postDataJSON(), status: 'ENABLED' };
        mockPersons.push(newPerson);
        return route.fulfill({
          status: 201,
          contentType: 'application/json',
          body: JSON.stringify(newPerson)
        });
      }

      if (method === 'PUT') {
        const updated = route.request().postDataJSON();
        const index = mockPersons.findIndex(p => p.id === updated.id);
        if (index !== -1) {
          mockPersons[index] = { ...mockPersons[index], ...updated };
        }
        return route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify(mockPersons[index])
        });
      }

      if (method === 'DELETE') {
        const id = parseInt(url.split('/').pop() || '0');
        const index = mockPersons.findIndex(p => p.id === id);
        if (index !== -1) {
          mockPersons[index].status = 'DISABLED';
          return route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify(mockPersons[index])
          });
        }
      }

      await route.continue();
    });

    // Mock initial data fetch that sometimes happens on dashboard load
    await page.route('**/api/persons?**', async (route) => {

      const method = route.request().method();
      const url = route.request().url();

      if (method === 'GET') {

        // Person List / Search
        const urlObj = new URL(url);
        const query = urlObj.searchParams.get('q')?.toLowerCase();
        const statusFilter = urlObj.searchParams.get('status');

        let filtered = [...mockPersons];
        if (query) {
          filtered = filtered.filter(p => p.name.toLowerCase().includes(query.toLowerCase()) || p.phoneNumber.includes(query));
        }
        if (statusFilter) {
          const statuses = statusFilter.split(',');
          const statusMap: Record<string, string> = { '1': 'ENABLED', '0': 'DISABLED' };
          const allowedStatuses = statuses.map(s => statusMap[s]);
          filtered = filtered.filter(p => allowedStatuses.includes(p.status));
        }

        return route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify(filtered)
        });
      }
    });

    await page.goto('/dashboard');
    await expect(page).toHaveURL(/.*dashboard/);
  });

  test('should list persons and handle pagination', async ({ page }) => {
    // Wait for the grid to be visible with increased timeout
    await expect(page.locator('div.grid')).toBeVisible({ timeout: 10000 });
    await expect(page.locator('h1')).toContainText('Peepol Dashboard');

    // Get person cards count
    const personCards = page.locator('div.grid > a');
    await expect(personCards).toHaveCount(mockPersons.length);

    // If there are more than 10, check for "Load more"
    const loadMoreButton = page.getByRole('button', { name: /Load more/i });
    if (await loadMoreButton.isVisible()) {
      await loadMoreButton.click();
      // Wait for more cards to appear
      await page.waitForTimeout(500);
    }
  });

  test('should search for persons by name', async ({ page }) => {
    const searchInput = page.getByPlaceholder(/Search people/i);
    await searchInput.fill('Juan');

    // Wait for debounce (500ms in code)
    await page.waitForTimeout(1000);

    // All visible cards should contain "Juan" or we should see "no results"
    // (the app doesn't seem to show "no results" explicitly in a special way,
    // just an empty grid)
    const cardTitles = page.locator('h3');
    const count = await cardTitles.count();
    for (let i = 0; i < count; i++) {
      await expect(cardTitles.nth(i)).toContainText(/Juan/i);
    }
  });

  test('should create, edit and delete a person', async ({ page }) => {
    const uniqueName = `Test Person ${Date.now()}`;
    const uniquePhone = `+34 ${Math.floor(100000000 + Math.random() * 900000000)}`;

    // 1. Create
    await page.getByRole('button', { name: /Add New Person/i }).click();
    await expect(page.locator('h2')).toContainText('New Person');

    await page.locator('input[required]').nth(0).fill(uniqueName); // Name
    await page.locator('input[required]').nth(1).fill(uniquePhone); // Phone
    await page.locator('textarea').fill('This is a test biography.');

    await page.getByRole('button', { name: /Create/i }).click();

    // Wait for modal to close and card to appear
    await expect(page.locator('h2')).not.toBeVisible();
    await page.getByPlaceholder(/Search people/i).fill(uniqueName);
    await page.waitForTimeout(1000);
    await expect(page.locator('h3', { hasText: uniqueName })).toBeVisible();

    // 2. View Detail
    await page.locator('h3', { hasText: uniqueName }).click();
    await expect(page).toHaveURL(/.*person\/\d+/);
    await expect(page.getByRole('heading', { name: /^Test Person/i })).toHaveText(uniqueName);
    await expect(page.locator('p', { hasText: uniquePhone })).toBeVisible();

    // 3. Edit
    await page.getByRole('button', { name: /Edit/i }).click();
    await expect(page.getByRole('heading', { name: /^Edit Person/i })).toContainText('Edit Person');

    const updatedName = `${uniqueName} Updated`;
    await page.locator('input[required]').nth(0).fill(updatedName);
    await page.getByRole('button', { name: /Update/i }).click();

    await expect(page.getByRole('heading', { name: /^Test Person/i })).toHaveText(updatedName);

    // 4. Delete (Inactivate)
    // The app uses window.confirm, which Playwright handles by auto-dismissing (canceling)
    // unless we set up a listener.
    page.once('dialog', dialog => dialog.accept());
    await page.getByRole('button', { name: /Delete/i }).click();

    // After delete, it redirects to dashboard
    await expect(page).toHaveURL(/.*dashboard/);

    // Check if it appears as Inactive if we filter
    await page.getByPlaceholder(/Search people/i).fill(updatedName);
    // Click "Inactive" checkbox
    await page.locator('label', { hasText: 'Inactive' }).click();
    await page.waitForTimeout(1000);
    await expect(page.locator('span > span', { hasText: 'Inactive' })).toBeVisible();
  });

  test('should not show delete button for non-admin users', async ({ page, context }) => {

    await context.clearCookies();
    await page.evaluate(() => window.sessionStorage.clear());

    await context.addCookies([
      { name: 'authToken', value: 'fake-token', domain: 'localhost', path: '/' },
      { name: 'authRole', value: 'USER', domain: 'localhost', path: '/' }
    ]);

    await page.addInitScript(() => {
      window.sessionStorage.setItem('session', JSON.stringify({
        token: 'fake-user-token',
        role: 'USER',
        username: 'user',
        id: 3
      }));
    });

    await page.reload();

    // Go to first-person detail
    await page.locator('div.grid > a').first().click();
    await expect(page).toHaveURL(/.*person\/\d+/);

    // Verify delete button is NOT visible
    await expect(page.getByRole('button', { name: /Delete/i })).not.toBeVisible();

    // Verify Edit button IS visible
    await expect(page.getByRole('button', { name: /Edit/i })).toBeVisible();
  });
});
