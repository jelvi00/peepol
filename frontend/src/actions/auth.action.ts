"use server";

import { AuthService } from "@/services";
import { redirect } from "next/navigation";
import { wait } from "@/lib";

interface LoginActionResult {
  success: boolean;
  error: string;
  content: {
    user: unknown;
  };
}

export async function loginAction(
  _prevState: unknown,
  formData: FormData
): Promise<LoginActionResult> {
  const username = String(formData.get("username"));
  const password = String(formData.get("password"));
  const isAdmin = String(formData.get("admin")) === "true";

  const user = await AuthService.login({ username, password }, isAdmin);

  if (user?.token) {
    await AuthService.setSessionCookies(user);

    // Small delay to ensure cookies are properly set before returning
    await wait(100);
    console.log("[LOGIN-ACTION] Login completed successfully");
  } else {
    console.error("[LOGIN-ACTION] Login failed - no token received");
  }

  return {
    success: Boolean(user?.id),
    error: Boolean(user?.id) ? "" : "Unable to complete action",
    content: { user: user || {} },
  };
}

export async function logoutAction() {
  try {
    await AuthService.removeAuthToken();
  } catch (error) {
    console.error("Error during logout:", error);
  }
  redirect("/login");
}
