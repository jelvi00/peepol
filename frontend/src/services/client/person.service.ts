import type { Person, PersonAddRequest, PersonUpdateRequest } from "@/types";

export const PersonService = {

  async getPersons(page = 0, size = 10, status: string = '1') {
    const response = await fetch(`/api/persons?page=${page}&size=${size}&status=${status}`);
    if (response.ok) return await response.json() as Person[];
    return [];
  },

  async searchPersons(query: string, page = 0, size = 10, status: string = '1') {
    const response = await fetch(`/api/persons?q=${encodeURIComponent(query)}&page=${page}&size=${size}&status=${status}`);
    if (response.ok) return await response.json() as Person[];
    return [];
  },

  async getPerson(id: number) {
    const response = await fetch(`/api/persons/${id}`);
    if (response.ok) return await response.json() as Person;
    return null;
  },

  async createPerson(person: PersonAddRequest) {
    const response = await fetch('/api/persons', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(person)
    });

    const data = await response.json();
    if (response.ok) return data as Person;

    throw new Error(data.message || "Error al crear persona");
  },

  async updatePerson(person: PersonUpdateRequest) {
    const response = await fetch('/api/persons', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(person)
    });

    const data = await response.json();
    if (response.ok) return data as Person;

    throw new Error(data.message || "Error al actualizar persona");
  },

  async deletePerson(id: number) {
    const response = await fetch(`/api/persons/${id}`, {
      method: 'DELETE'
    });

    const data = await response.json();
    if (response.ok) return data;

    throw new Error(data.message || "Error al eliminar persona");
  },
};
