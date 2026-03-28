import { createContext, useContext, useMemo, useState } from "react";
import type { AuthResponse, AuthUser } from "../types";

type AuthContextValue = {
  user: AuthUser | null;
  token: string | null;
  signIn: (response: AuthResponse) => void;
  signOut: () => void;
};

const STORAGE_KEY = "hotel-management-auth";
const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function readStorage() {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) return { user: null, token: null };
  try {
    return JSON.parse(raw) as { user: AuthUser | null; token: string | null };
  } catch {
    return { user: null, token: null };
  }
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const initial = readStorage();
  const [user, setUser] = useState<AuthUser | null>(initial.user);
  const [token, setToken] = useState<string | null>(initial.token);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      token,
      signIn: (response) => {
        const authUser: AuthUser = {
          userId: response.userId,
          name: response.name,
          email: response.email,
          role: response.role
        };
        setUser(authUser);
        setToken(response.token);
        localStorage.setItem(
          STORAGE_KEY,
          JSON.stringify({ user: authUser, token: response.token })
        );
      },
      signOut: () => {
        setUser(null);
        setToken(null);
        localStorage.removeItem(STORAGE_KEY);
      }
    }),
    [token, user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const value = useContext(AuthContext);
  if (!value) {
    throw new Error("useAuth must be used inside AuthProvider");
  }
  return value;
}
