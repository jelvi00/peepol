"use client";

import { loginAction } from "@/actions/auth.action";
import { PPEventEmitter } from "@/providers";
import { EVENT } from "@/constants";
import { useRouter } from "next/navigation";
import React, { useActionState, useEffect, useState, useRef } from "react";
import { SVG } from "@/components";
import { useInitialDataLoader } from "@/hooks/useInitialDataLoader";

interface LoginState {
  success: boolean;
  error: string;
  content: {
    user: any;
  };
}

const initialState: LoginState = {
  success: false,
  error: "",
  content: { user: {} },
};

export function LoginForm() {
  const router = useRouter();
  const { loadingState, loadInitialData } = useInitialDataLoader();

  const [showPassword, setShowPassword] = useState(false);

  const postLoginExecutedRef = useRef(false);

  const [state, formAction, isPending] = useActionState(loginAction, initialState);

  useEffect(() => {
    if (state.success && !loadingState.isLoading && !postLoginExecutedRef.current) {

      (async () => {
        postLoginExecutedRef.current = true;

        console.log("LoginForm - Starting post-login data loading process");

        try {
          PPEventEmitter.emit(EVENT.ON_LOGIN, state.content?.user);

          await loadInitialData();

          console.log("LoginForm - Post-login data loading completed successfully");

        } catch (error) {
          console.error(" LoginForm - Error loading initial data:", error);
        }

        router.push("/dashboard");
      })();

    }
  }, [state.success, loadingState.isLoading, router]);

  useEffect(() => {
    if (state.error && !state.success) postLoginExecutedRef.current = false;
  }, [state.error, state.success]);

  // Show loading overlay when loading initial data
  const isLoadingData = loadingState.isLoading;
  const showLoadingOverlay = isPending || isLoadingData;

  return (
    <div className="relative">
      <form className="space-y-4" action={formAction}>
        <div className="relative w-full">
          <SVG
            name={"user-rounded-gray"}
            className="absolute top-3.25 left-5"
          />
          <input
            className="bg-light-blue px-14 py-3 rounded-full w-full"
            name="username"
            placeholder="Nombre de usuario"
            required
            disabled={showLoadingOverlay}
          />
        </div>

        <div className="relative w-full">
          <button
            type="button"
            onClick={() => setShowPassword(!showPassword)}
            className={`absolute top-3.75 right-5 ${
              showPassword && "brightness-110"
            }`}
            disabled={showLoadingOverlay}
          >
            <SVG name={"eye-gray"} />
          </button>
          <input
            className="bg-light-blue px-7 py-3 rounded-full w-full"
            type={showPassword ? "text" : "password"}
            name="password"
            placeholder="Contraseña"
            required
            disabled={showLoadingOverlay}
          />
        </div>

        <button
          className="font-sans text-white bg-basic-red w-full py-3 rounded-full disabled:opacity-50"
          disabled={showLoadingOverlay}
        >
          {isPending
            ? "Ingresando..."
            : isLoadingData
            ? loadingState.currentStep
            : "Ingresar"}
        </button>

        <section className="flex items-center justify-center">
          {state.error && <p className="text-red-500">{state.error}</p>}
          {loadingState.error && (
            <p className="text-red-500">{loadingState.error}</p>
          )}
        </section>
      </form>

      {/* Loading Overlay */}
      {showLoadingOverlay && (
        <div className="absolute inset-0 bg-white bg-opacity-90 flex flex-col items-center justify-center rounded-lg z-10">
          <div className="text-center space-y-4">
            {/* Loading Spinner */}
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-basic-red mx-auto"></div>

            {/* Current Step */}
            <div className="space-y-2">
              <p className="text-gray-700 font-medium">
                {isPending ? "Iniciando sesión..." : loadingState.currentStep}
              </p>

              {/* Progress Bar */}
              {isLoadingData && (
                <div className="w-64 bg-gray-200 rounded-full h-2">
                  <div
                    className="bg-basic-red h-2 rounded-full transition-all duration-300 ease-out"
                    style={{ width: `${loadingState.progress}%` }}
                  ></div>
                </div>
              )}

              {/* Progress Percentage */}
              {isLoadingData && (
                <p className="text-sm text-gray-500">
                  {loadingState.progress}% completado
                </p>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
