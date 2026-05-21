import { AuthService } from "@/services";
import { ServiceAdapter } from "./service.adapter";

const NXT_BE_URL = process.env.NXT_BE_URL;
const API_PATH = "/peepol-api";
const BASE_URL = `${NXT_BE_URL}${API_PATH}`;

class _PPServiceAdapter extends ServiceAdapter {

  constructor() {
    super({ baseUrl: BASE_URL });
  }

  private buildRequestHeaders(authToken: string | undefined): Record<string, string> {

    const headers: Record<string, string> = { "Content-Type": "application/json" };
    if (authToken) headers.Authorization = `Bearer ${authToken}`;

    return headers;
  }

  async request(
    method: string,
    endpoint: string,
    data?: any,
  ) {

    const [authToken, authRole] = await Promise.all([
      AuthService.getAuthToken(),
      AuthService.getAuthRole(),
    ]);

    if (authRole === 'admin') endpoint = '/admin/'.concat(endpoint);

    if (!endpoint.includes("/auth/login") && !authToken) {
      console.error(`[PP-ADAPTER] No auth token for ${method} ${endpoint}:`,
        JSON.stringify(
          {
            authToken: authToken ? "PRESENT" : "MISSING",
            authRole: authRole || "MISSING",
            endpoint
          },
          null,
          2,
        ),
      );

      return {
        status: 401,
        message: "Authentication required",
        body: null,
      };
    }

    const requestHeaders = this.buildRequestHeaders(authToken);

    return super.makeHTTPRequest(
        method,
        endpoint,
        data,
        requestHeaders,
    );
  }
}

export const PPServiceAdapter = new _PPServiceAdapter();
