import { AuthService } from '@/services/auth.service';
import { PPServiceAdapter } from '@/adapters';
import { HttpStatusCode } from 'axios';

jest.mock('@/adapters', () => ({
  PPServiceAdapter: {
    request: jest.fn(),
  },
}));

// Mock next/headers
jest.mock('next/headers', () => ({
  cookies: jest.fn(() => ({
    get: jest.fn(),
    set: jest.fn(),
    delete: jest.fn(),
  })),
}));

describe('AuthService', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should login successfully', async () => {
    const credentials = { username: 'admin', password: 'password' };
    const mockResponse = { id: 1, username: 'admin', token: 'fake-token', role: 'ADMIN' };
    
    (PPServiceAdapter.request as jest.Mock).mockResolvedValue({
      status: HttpStatusCode.Ok,
      body: mockResponse,
    });

    const result = await AuthService.login(credentials);

    expect(result).toEqual(mockResponse);
    expect(PPServiceAdapter.request).toHaveBeenCalledWith('POST', '/auth/login', credentials);
  });

  it('should return null when login fails', async () => {
    (PPServiceAdapter.request as jest.Mock).mockResolvedValue({
      status: HttpStatusCode.Unauthorized,
      body: { message: 'Unauthorized' },
    });

    const result = await AuthService.login({ username: 'user', password: 'wrong' });

    expect(result).toBeNull();
  });
});
