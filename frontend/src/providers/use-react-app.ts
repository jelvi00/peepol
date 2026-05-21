"use client";

import React, { useEffect, useRef, useState } from "react";
import { EVENT } from "@/constants";
import { doNothing, wait } from "@/lib/utils";
import EventEmitter from "eventemitter3";
import { getStorageItem, setStorageItem } from "@/lib/session";

const INITIAL_STATE = {
  session: undefined,
  isDialogOpen: false,
  dialogContent: null
};

export const PPEventEmitter = new EventEmitter();

export function useReactApp() {

  const [state, setState] = useState<Record<string, unknown | boolean>>({
    ...INITIAL_STATE,
  });

  const listenersRegisteredRef = useRef(false);

  useEffect(() => {
    setSession().catch(doNothing);

    if (!listenersRegisteredRef.current) {
      setEvents();
      listenersRegisteredRef.current = true;
    }

    return () => {
      PPEventEmitter.off(EVENT.SET_CONTEXT_STATE, handleContextChange);
      PPEventEmitter.off(EVENT.ON_LOGIN, onLoggedIn);
      PPEventEmitter.off(EVENT.ON_LOGOUT, onLogout);
      PPEventEmitter.off(EVENT.TOGGLE_DIALOG, toggleDialog);
      listenersRegisteredRef.current = false;
    };
  }, []);

  function handleContextChange(props: Record<string, unknown>) {
    setState((prevState: Record<string, unknown>) => ({
      ...prevState,
      ...props,
    }));
  }

  function setEvents() {
    PPEventEmitter.on(EVENT.SET_CONTEXT_STATE, handleContextChange);
    PPEventEmitter.on(EVENT.ON_LOGIN, onLoggedIn);
    PPEventEmitter.on(EVENT.ON_LOGOUT, onLogout);
    PPEventEmitter.on(EVENT.TOGGLE_DIALOG, toggleDialog);
  }

  async function setSession() {
    const session = getStorageItem("session");

    if (!session) await onLogout();

    handleContextChange({ session });
  }

  async function onLoggedIn(session: Record<string, unknown>) {
    console.log(
      "[REACT-APP] Login successful",
      {
        role: session.role,
        userId: session.userId || session.id,
        timestamp: new Date().toISOString(),
      }
    );

    handleContextChange({ session });

    setStorageItem("session", session);
  }

  async function onLogout() {
    handleContextChange({ session: undefined });
    setStorageItem("session", undefined);
  }

  function toggleDialog(component: React.JSX.Element | null | undefined) {
    if (component) handleContextChange({ isDialogOpen: true, dialogContent: component });
    else onCloseDialog(false);
  }

  function onCloseDialog(isDialogOpen: boolean) {
    if (!isDialogOpen) {
      handleContextChange({ isDialogOpen });
      wait(100).then(() => handleContextChange({ dialogContent: null }));
    }
  }

  return {
    state,
    context: { state },
    onCloseDialog
  };
}
