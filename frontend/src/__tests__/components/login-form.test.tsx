import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { LoginForm } from '@/components/login/login-form';
import { useRouter } from 'next/navigation';
import { useInitialDataLoader } from '@/hooks/useInitialDataLoader';
import { useActionState } from 'react';

// Mock the dependencies
jest.mock('next/navigation', () => ({
  useRouter: jest.fn(),
}));

jest.mock('@/hooks/useInitialDataLoader', () => ({
  useInitialDataLoader: jest.fn(),
}));

jest.mock('@/actions/auth.action', () => ({
  loginAction: jest.fn(),
}));

jest.mock('react', () => ({
  ...jest.requireActual('react'),
  useActionState: jest.fn(),
}));

jest.mock('@/providers', () => ({
  PPEventEmitter: {
    emit: jest.fn(),
  },
}));

describe('LoginForm', () => {
  const mockPush = jest.fn();
  const mockLoadInitialData = jest.fn();

  beforeEach(() => {
    (useRouter as jest.Mock).mockReturnValue({ push: mockPush });
    (useInitialDataLoader as jest.Mock).mockReturnValue({
      loadingState: { isLoading: false, currentStep: '', progress: 0, error: null },
      loadInitialData: mockLoadInitialData,
    });
    // Default mock for useActionState: [state, action, isPending]
    (useActionState as jest.Mock).mockReturnValue([
      { success: false, error: '', content: { user: {} } },
      jest.fn(),
      false,
    ]);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('renders login form correctly', () => {
    render(<LoginForm />);
    expect(screen.getByPlaceholderText('Username')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Password')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
  });

  it('shows error message when login fails', () => {
    (useActionState as jest.Mock).mockReturnValue([
      { success: false, error: 'Unable to complete action', content: { user: {} } },
      jest.fn(),
      false,
    ]);

    render(<LoginForm />);
    expect(screen.getByText('Unable to complete action')).toBeInTheDocument();
  });

  it('shows loading state during login', () => {
    (useActionState as jest.Mock).mockReturnValue([
      { success: false, error: '', content: { user: {} } },
      jest.fn(),
      true, // isPending = true
    ]);

    render(<LoginForm />);
    expect(screen.getAllByText('Signing in...').length).toBeGreaterThan(0);
    expect(screen.getByRole('button', { name: /signing in/i })).toBeDisabled();
  });

  it('navigates to dashboard after successful login and data loading', async () => {
    const mockFormAction = jest.fn();

    (useActionState as jest.Mock).mockReturnValue([
      { success: true, error: '', content: { user: { id: 1, username: 'admin' } } },
      mockFormAction,
      false,
    ]);

    render(<LoginForm />);

    await waitFor(() => {
      expect(mockLoadInitialData).toHaveBeenCalled();
    });

    await waitFor(() => {
      expect(mockPush).toHaveBeenCalledWith('/dashboard');
    });
  });
});
