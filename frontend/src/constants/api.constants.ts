export const API_V1 = {
  AUTH: {
    login: "/auth/login",
  },
  PERSONS: {
    persons: "/persons",
    personSearch: "/persons/search",
    personDetail: (id: number) => `/persons/${id}/detail`,
    personRemove: (id: number) => `/persons/${id}/person`,
  },
};
