import React from "react";

export const AuthLayout = ({ children }: { children: React.ReactNode }) => {
    return (
        <div className="relative h-screen w-screen flex" style={{ overflowX: "hidden" }}>

            <div className={"absolute w-screen h-screen z-[-1] bg-gray-100"}
                 aria-hidden="true"/>

            {children}
        </div>
    );
};
