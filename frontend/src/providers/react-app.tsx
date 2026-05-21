"use client";

import React, { createContext, useContext } from "react";
import type { Session } from "@/types";
import { useReactApp } from "@/providers/use-react-app";
import { Dialog } from "@/components/ui/dialog";

const AppContext = createContext<Record<string, unknown>>({});

export default function ReactApp({ children }: { children: React.ReactNode }) {
  const { state, context, onCloseDialog } = useReactApp();

  function showDialog() {
    return (
        <Dialog open={state.isDialogOpen as boolean} onOpenChange={onCloseDialog}>
          {state.dialogContent as React.ReactNode}
        </Dialog>
    );
  }

  return (
      <AppContext.Provider value={context}>
        {children}
        {showDialog()}
      </AppContext.Provider>
  );
}

export function useAppContext() {
  return useContext(AppContext);
}

export function useSession(): Session | null {
  const { state } = useContext(AppContext) as {
    state: Record<string, unknown>;
  };

  const session = state.session as Session;

  return session || null;
}
