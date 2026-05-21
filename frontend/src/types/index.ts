export interface Session {
  id: string | number;
  role: string;
  token: string;
  username: string;
}

export interface Person {
  id: number;
  name: string;
  phoneNumber: string;
  bio?: string;
  status: string;
}

export interface PersonAddRequest {
  name: string;
  phoneNumber: string;
  bio?: string;
}

export interface PersonUpdateRequest {
  id: number;
  name?: string;
  phoneNumber?: string;
  bio?: string;
}
