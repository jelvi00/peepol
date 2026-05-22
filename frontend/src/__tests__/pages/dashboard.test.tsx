import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import DashboardPage from '@/app/dashboard/page';
import { useAppContext } from '@/providers/react-app';
import { PersonCliService } from '@/services/client/person.cli.service';

// Mock dependencies
jest.mock('@/providers/react-app', () => ({
  useAppContext: jest.fn(),
  useSession: jest.fn(() => ({ username: 'testuser' })),
}));

jest.mock('@/services/client/person.cli.service', () => ({
  PersonCliService: {
    getPersons: jest.fn(),
    searchPersons: jest.fn(),
    createPerson: jest.fn(),
  },
}));

// Mock components that might interfere with testing
jest.mock('@/components/layout', () => ({
  AppLayout: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}));

jest.mock('@/components', () => ({
  SVG: () => <div data-testid="svg-icon" />,
}));

// Mock next/link
jest.mock('next/link', () => {
  return ({ children, href }: { children: React.ReactNode, href: string }) => (
    <a href={href}>{children}</a>
  );
});

// Mock UI components
jest.mock('@/components/ui/dialog', () => ({
  Dialog: ({ children, open }: { children: React.ReactNode, open: boolean }) => open ? <div>{children}</div> : null,
}));

describe('DashboardPage', () => {
  const mockPersons = [
    { id: 1, name: 'Juan Garcia', phoneNumber: '+34 600000001', status: 'ENABLED' },
    { id: 2, name: 'Maria Rodriguez', phoneNumber: '+34 600000002', status: 'DISABLED' },
  ];

  beforeEach(() => {
    (useAppContext as jest.Mock).mockReturnValue({
      state: { initialPersons: mockPersons },
    });
    (PersonCliService.getPersons as jest.Mock).mockResolvedValue(mockPersons);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('renders dashboard with initial persons', () => {
    render(<DashboardPage />);

    expect(screen.getByPlaceholderText(/Search people/i)).toBeInTheDocument();
    expect(screen.getByText('Juan Garcia')).toBeInTheDocument();
    expect(screen.getByText('Maria Rodriguez')).toBeInTheDocument();
  });

  it('filters persons when search input changes', async () => {
    (PersonCliService.searchPersons as jest.Mock).mockResolvedValue([mockPersons[0]]);

    render(<DashboardPage />);

    const searchInput = screen.getByPlaceholderText(/Search people/i);
    fireEvent.change(searchInput, { target: { value: 'Juan' } });

    await waitFor(() => {
      expect(PersonCliService.searchPersons).toHaveBeenCalledWith('Juan', 0, 10, '1');
    });
  });

  it('shows create modal when clicking "Add New Person"', () => {
    render(<DashboardPage />);

    const addButton = screen.getByText('Add New Person');
    fireEvent.click(addButton);

    expect(screen.getByText('New Person')).toBeInTheDocument();
  });

  it('updates status filter when checkboxes are clicked', async () => {
    render(<DashboardPage />);

    const inactiveCheckbox = screen.getByLabelText('Inactive');
    fireEvent.click(inactiveCheckbox);

    await waitFor(() => {
      // It should trigger fetch with status "0,1" or "0" depending on how it's implemented
      // In DashboardPage: if enabled and disabled are true, it returns "0,1"
      expect(PersonCliService.getPersons).toHaveBeenCalled();
    });
  });
});
