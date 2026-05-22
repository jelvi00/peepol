"use client";

import React, { useCallback } from "react";
import { useSession } from "@/providers/react-app";
import { logoutAction } from "@/actions/auth.action";
import { PPEventEmitter } from "@/providers/use-react-app";
import { EVENT } from "@/constants";
import { doNothing } from "@/lib";
import { SVG } from "@/components";

interface AppLayoutProps {
  children: React.ReactNode;
}

export function AppLayout({ children }: AppLayoutProps) {
  const session = useSession();

  const logout = useCallback(() => {
    PPEventEmitter.emit(EVENT.ON_LOGOUT);
    logoutAction().catch(doNothing);
  }, []);

  return (
    <div className="flex h-screen bg-gray-100 overflow-hidden">

      <div className="flex-1 overflow-y-auto">
        <header className="bg-white shadow-sm h-16 flex items-center px-6 justify-between">
          <h1 className="text-xl font-sans text-gray-800">Peepol Dashboard</h1>
          {!session ? null : (
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-2">
                <span className="text-sm font-light">{session.username}</span>
                <div className="w-8 h-8 bg-blue-300 rounded-full flex items-center justify-center text-white text-xs uppercase">
                  <SVG name="user" className="invert" />
                </div>
              </div>
              <button
                onClick={logout}
                className="text-sm text-medium-grey hover:text-blue-500 transition-colors font-sans flex items-center gap-1">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                </svg>
                Sign out
              </button>
            </div>
          )}
        </header>
        <main className="p-4 md:p-6 min-h-[calc(100vh-64px)] w-full">
          {children}
        </main>
      </div>
    </div>
  );
}
