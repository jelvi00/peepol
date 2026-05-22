import { API_V1 } from "@/constants/api.constants";
import { PPServiceAdapter } from "@/adapters";
import type { Session } from "@/types";
import { HttpStatusCode } from "axios";

const { login, adminLogin } = API_V1.AUTH;

async function getCookies() {
  const { cookies } = await import("next/headers");
  return cookies();
}

export const AuthService = Object.freeze({

  async getAuthToken() {
    try {
      const cookies = await getCookies();
      return cookies.get("authToken")?.value;
    } catch (error) {
      // console.error("[AUTH-SERVICE] Error getting auth token:", error);
      return undefined;
    }
  },

  async getAuthRole() {
    try {
      const cookies = await getCookies();
      return cookies.get("authRole")?.value;
    } catch (error) {
      // console.error("[AUTH-SERVICE] Error getting auth role:", error);
      return undefined;
    }
  },

  async setSessionCookies(session: Session) {
    try {
      const cookies = await getCookies();

      const cookieOptions = {
        httpOnly: true,
        secure: process.env.NODE_ENV === "production",
        sameSite: "lax" as const, // More permissive than "strict" for better compatibility
        maxAge: 60 * 60 * 8, // 8 hours
        path: "/", // Ensure cookies are available for all routes
      };

      cookies.set("authToken", session.token, cookieOptions);
      cookies.set("authRole", session.role, cookieOptions);

      console.log("[AUTH-SERVICE] Session cookies set successfully");
    } catch (error) {
      console.error("[AUTH-SERVICE] Error setting session cookies:", error);
      throw error;
    }
  },

  async removeAuthToken() {
    try {
      const cookies = await getCookies();
      console.log("[AUTH-SERVICE] Removing auth tokens");
      cookies.delete("authToken");
      cookies.delete("authRole");
      console.log("[AUTH-SERVICE] Auth tokens removed successfully");
    } catch (error) {
      console.error("[AUTH-SERVICE] Error removing auth tokens:", error);
      throw error;
    }
  },

  async login(credentials: { username: string; password: string }, isAdmin = false) {
    const response = await PPServiceAdapter.request(
        "POST",
        isAdmin ? adminLogin : login,
        credentials
    );

    if (response?.status === HttpStatusCode.Ok) return response.body;

    console.error("Login failed - invalid response status:", response?.status);
    return null;
  }

});
