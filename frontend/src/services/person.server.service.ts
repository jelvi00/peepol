import { PPServiceAdapter } from "@/adapters";
import { API_V1 } from "@/constants/api.constants";
import type { Person, PersonAddRequest, PersonUpdateRequest } from "@/types";
import { HttpStatusCode } from "axios";

const { persons, personSearch, personDetail, personRemove } = API_V1.PERSONS;

export const PersonServerService = {

  async getPersons(page = 0, size = 10, status: string = '1') {
    const response = await PPServiceAdapter.request("GET", persons, { page, size, status });

    if (response?.status === HttpStatusCode.Ok) return response.body as Person[];

    return [];
  },

  async searchPersons(query: string, page = 0, size = 10, status: string = '1') {
    const response = await PPServiceAdapter.request("GET", personSearch, { q: query, page, size, status });

    if (response?.status === HttpStatusCode.Ok) return response.body as Person[];

    return [];
  },

  async getPerson(id: number) {
    const response = await PPServiceAdapter.request("GET", personDetail(id));

    if (response?.status === HttpStatusCode.Ok) return response.body as Person;
    return null;
  },

  async createPerson(person: PersonAddRequest) {
    const response = await PPServiceAdapter.request("POST", persons, person);

    if (response?.status === HttpStatusCode.Ok || response?.status === HttpStatusCode.Created)
      return response.body as Person;

    throw new Error(response?.body?.message || "Error al crear persona");
  },

  async updatePerson(person: PersonUpdateRequest) {
    const response = await PPServiceAdapter.request("PUT", persons, person);

    if (response?.status === HttpStatusCode.Ok) return response.body as Person;

    throw new Error(response?.body?.message || "Error al actualizar persona");
  },

  async deletePerson(id: number) {
    const response = await PPServiceAdapter.request("DELETE", personRemove(id));

    if (response?.status === HttpStatusCode.Ok) return response.body;

    throw new Error(response?.body?.message || "Error al eliminar persona");
  },
};
