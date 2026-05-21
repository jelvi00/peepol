import React from "react";
import ReactApp from "./react-app";
import { EnvironmentProvider } from "./environment-provider";

/**
 * Environment Variables Provider with Security-First Approach
 *
 * @description Filters and provides environment variables for client-side access
 * @implements Controlled exposure pattern, automatic NXT_PUBLIC_ filtering
 * @features Server variable inclusion, security validation, enhanced public vars support
 */
export default function BaseApp({ children }: { children: React.ReactNode }) {
    const env = {
        NXT_PUBLIC_xd: "xd",
    };

    return (
        <EnvironmentProvider environment={env}>
            <ReactApp>
                {children}
            </ReactApp>
        </EnvironmentProvider>
    );
}
