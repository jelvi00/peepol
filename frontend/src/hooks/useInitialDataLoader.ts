"use client";

import { useState } from "react";
import { PersonCliService } from "@/services/client/person.cli.service";
import { PPEventEmitter } from "@/providers/use-react-app";
import { EVENT } from "@/constants";
import { wait } from "@/lib";

export function useInitialDataLoader() {
  const [loadingState, setLoadingState] = useState({
    isLoading: false,
    currentStep: "",
    progress: 0,
    error: null as string | null,
  });

  const loadInitialData = async () => {
    setLoadingState({ isLoading: true, currentStep: "Loading people...", progress: 20, error: null });

    try {

      await wait(300)

      // Load initial people (first page)
      const persons = await PersonCliService.getPersons(0, 10);

      setLoadingState(prev => ({ ...prev, currentStep: "Mapping data...", progress: 60 }));
      await new Promise(resolve => setTimeout(resolve, 200));

      setLoadingState(prev => ({ ...prev, currentStep: "Finalizing...", progress: 90 }));

      // Save to global context
      PPEventEmitter.emit(EVENT.SET_CONTEXT_STATE, { initialPersons: persons });

      await new Promise(resolve => setTimeout(resolve, 100));
      setLoadingState(prev => ({ ...prev, isLoading: false, progress: 100 }));

    } catch (error: any) {
      console.error("Error loading initial data:", error);
      setLoadingState(prev => ({ ...prev, isLoading: false, error: "Error loading initial data" }));
      throw error;
    }
  };

  return { loadingState, loadInitialData };
}
