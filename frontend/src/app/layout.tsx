import type { Metadata } from "next";
import "./globals.css";
import React from "react";
import BaseApp from "@/providers/base-app";

export const metadata: Metadata = {
  title: "Peepol",
  description: "Peepol App",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <BaseApp>
          {children}
        </BaseApp>
      </body>
    </html>
  );
}
