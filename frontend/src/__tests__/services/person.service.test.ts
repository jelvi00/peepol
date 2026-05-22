import { PersonService } from '@/services/person.service';
import { PPServiceAdapter } from '@/adapters';
import { HttpStatusCode } from 'axios';

jest.mock('@/adapters', () => ({
  PPServiceAdapter: {
    request: jest.fn(),
  },
}));

describe('PersonService', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should get all persons successfully', async () => {
    const mockPersons = [
      { id: 1, name: 'John Doe', phoneNumber: '123456789', status: 'ENABLED' },
    ];
    (PPServiceAdapter.request as jest.Mock).mockResolvedValue({
      status: HttpStatusCode.Ok,
      body: mockPersons,
    });

    const result = await PersonService.getPersons();

    expect(result).toEqual(mockPersons);
    expect(PPServiceAdapter.request).toHaveBeenCalledWith('GET', '/persons', {
      page: 0,
      size: 10,
      status: '1',
    });
  });

  it('should return an empty array when getPersons fails', async () => {
    (PPServiceAdapter.request as jest.Mock).mockResolvedValue({
      status: HttpStatusCode.InternalServerError,
      body: null,
    });

    const result = await PersonService.getPersons();

    expect(result).toEqual([]);
  });

  it('should search persons successfully', async () => {
    const mockPersons = [
      { id: 1, name: 'John Doe', phoneNumber: '123456789', status: 'ENABLED' },
    ];
    (PPServiceAdapter.request as jest.Mock).mockResolvedValue({
      status: HttpStatusCode.Ok,
      body: mockPersons,
    });

    const result = await PersonService.searchPersons('John');

    expect(result).toEqual(mockPersons);
    expect(PPServiceAdapter.request).toHaveBeenCalledWith('GET', '/persons/search', {
      q: 'John',
      page: 0,
      size: 10,
      status: '1',
    });
  });

  it('should create a person successfully', async () => {
    const newPerson = { name: 'Jane Doe', phoneNumber: '987654321' };
    const mockResponse = { id: 2, ...newPerson, status: 'ENABLED' };
    (PPServiceAdapter.request as jest.Mock).mockResolvedValue({
      status: HttpStatusCode.Created,
      body: mockResponse,
    });

    const result = await PersonService.createPerson(newPerson);

    expect(result).toEqual(mockResponse);
  });

  it('should get a single person by id', async () => {
    const mockPerson = { id: 1, name: 'John Doe', phoneNumber: '123456789', status: 'ENABLED' };
    (PPServiceAdapter.request as jest.Mock).mockResolvedValue({
      status: HttpStatusCode.Ok,
      body: mockPerson,
    });

    const result = await PersonService.getPerson(1);

    expect(result).toEqual(mockPerson);
  });

  it('should return null when getPerson fails', async () => {
    (PPServiceAdapter.request as jest.Mock).mockResolvedValue({
      status: HttpStatusCode.NotFound,
      body: null,
    });

    const result = await PersonService.getPerson(999);

    expect(result).toBeNull();
  });

  it('should update a person successfully', async () => {
    const updateRequest = { id: 1, name: 'John Updated' };
    const mockResponse = { id: 1, name: 'John Updated', phoneNumber: '123456789', status: 'ENABLED' };
    (PPServiceAdapter.request as jest.Mock).mockResolvedValue({
      status: HttpStatusCode.Ok,
      body: mockResponse,
    });

    const result = await PersonService.updatePerson(updateRequest);

    expect(result).toEqual(mockResponse);
  });

  it('should throw error when updatePerson fails', async () => {
    const updateRequest = { id: 1, name: 'John' };
    (PPServiceAdapter.request as jest.Mock).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      body: { message: 'Error updating' },
    });

    await expect(PersonService.updatePerson(updateRequest)).rejects.toThrow('Error updating');
  });

  it('should delete a person successfully', async () => {
    (PPServiceAdapter.request as jest.Mock).mockResolvedValue({
      status: HttpStatusCode.Ok,
      body: { success: true },
    });

    const result = await PersonService.deletePerson(1);

    expect(result).toEqual({ success: true });
  });

  it('should throw error when deletePerson fails', async () => {
    (PPServiceAdapter.request as jest.Mock).mockResolvedValue({
      status: HttpStatusCode.Forbidden,
      body: { message: 'Not allowed' },
    });

    await expect(PersonService.deletePerson(1)).rejects.toThrow('Not allowed');
  });
});
