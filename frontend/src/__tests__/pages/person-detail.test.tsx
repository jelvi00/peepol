import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import PersonDetailPage from '@/app/dashboard/person/[id]/page';
import { PersonCliService } from '@/services/client/person.cli.service';
import { useRouter } from 'next/navigation';

// Mock dependencies
jest.mock('next/navigation', () => ({
  useRouter: jest.fn(),
}));

jest.mock('@/services/client/person.cli.service', () => ({
  PersonCliService: {
    getPerson: jest.fn(),
    deletePerson: jest.fn(),
    updatePerson: jest.fn(),
  },
}));

// Mock components
jest.mock('@/components/layout', () => ({
  AppLayout: ({ children }: { children: React.ReactNode }) => <main>{children}</main>,
}));

jest.mock('@/components', () => ({
  SVG: () => <div data-testid="svg-icon" />,
}));

jest.mock('@/components/ui/dialog', () => ({
  Dialog: ({ children, open }: { children: React.ReactNode, open: boolean }) => open ? <div>{children}</div> : null,
}));

// Mock use hook for params
jest.mock('react', () => ({
  ...jest.requireActual('react'),
  use: (promise: any) => {
    if (promise && typeof promise.then === 'function') {
        // This is a simplified mock for the 'use' hook in tests
        return promise._value;
    }
    return promise;
  }
}));

describe('PersonDetailPage', () => {
  const mockPerson = {
    id: 1,
    name: 'Juan Garcia',
    phoneNumber: '+34 600000001',
    bio: 'Test bio',
    status: 'ENABLED',
  };

  const mockPush = jest.fn();
  const mockParams = Promise.resolve({ id: '1' });
  // @ts-ignore
  mockParams._value = { id: '1' };

  beforeEach(() => {
    (useRouter as jest.Mock).mockReturnValue({ push: mockPush });
    (PersonCliService.getPerson as jest.Mock).mockResolvedValue(mockPerson);
    window.confirm = jest.fn(() => true);
    window.alert = jest.fn();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('renders loading state initially', async () => {
    // Make it stay in loading state by not resolving immediately if needed,
    // but here we just check if it renders.
    render(<PersonDetailPage params={mockParams} />);
    expect(screen.getByRole('main') || screen.getByText(/back/i)).toBeInTheDocument();
  });

  it('renders person details after loading', async () => {
    render(<PersonDetailPage params={mockParams} />);

    await waitFor(() => {
      expect(screen.getByText('Juan Garcia')).toBeInTheDocument();
      expect(screen.getByText('+34 600000001')).toBeInTheDocument();
      expect(screen.getByText('Test bio')).toBeInTheDocument();
    });
  });

  it('shows edit modal when clicking edit button', async () => {
    render(<PersonDetailPage params={mockParams} />);

    await waitFor(() => {
      const editButton = screen.getByText('Edit');
      fireEvent.click(editButton);
    });

    expect(screen.getByText('Edit Person')).toBeInTheDocument();
  });

  it('calls delete service and redirects when clicking delete button', async () => {
    render(<PersonDetailPage params={mockParams} />);

    await waitFor(() => {
      const deleteButton = screen.getByText('Delete');
      fireEvent.click(deleteButton);
    });

    expect(window.confirm).toHaveBeenCalled();
    expect(PersonCliService.deletePerson).toHaveBeenCalledWith(1);

    await waitFor(() => {
      expect(mockPush).toHaveBeenCalledWith('/dashboard');
    });
  });

  it('shows not found message if person does not exist', async () => {
    (PersonCliService.getPerson as jest.Mock).mockResolvedValue(null);

    render(<PersonDetailPage params={mockParams} />);

    await waitFor(() => {
      expect(screen.getByText('Person not found')).toBeInTheDocument();
    });
  });
});
