"use client";

import React, { createContext, useContext } from "react";

const EnvironmentContext = createContext<Record<string, unknown>>({});

export function EnvironmentProvider({ children, environment }: { children: React.ReactNode; environment: Record<string, unknown> }) {
  return (
    <EnvironmentContext.Provider value={environment}>
      {children}
    </EnvironmentContext.Provider>
  );
}

export function useEnvironment() {
  const context = useContext(EnvironmentContext);
  if (context === undefined) {
    throw new Error("useEnvironment must be used within an EnvironmentProvider");
  }
  return context;
}
