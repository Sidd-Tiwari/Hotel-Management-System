import { AuthResponse } from "../types";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

export async function api<T>(
  path: string,
  options: {
    method?: HttpMethod;
    body?: unknown;
    token?: string | null;
  } = {}
): Promise<T> {
  const response = await fetch(`${API_URL}${path}`, {
    method: options.method ?? "GET",
    headers: {
      "Content-Type": "application/json",
      ...(options.token ? { Authorization: `Bearer ${options.token}` } : {})
    },
    body: options.body ? JSON.stringify(options.body) : undefined
  });

  if (!response.ok) {
    let message = "Request failed";
    try {
      const data = (await response.json()) as { message?: string };
      message = data.message || message;
    } catch {
      message = `${response.status} ${response.statusText}`;
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}

export async function requestOtp(email: string, name?: string) {
  return api<{ email: string; message: string }>("/api/auth/request-otp", {
    method: "POST",
    body: { email, name }
  });
}

export async function verifyOtp(email: string, otp: string, name?: string) {
  return api<AuthResponse>("/api/auth/verify-otp", {
    method: "POST",
    body: { email, otp, name }
  });
}
