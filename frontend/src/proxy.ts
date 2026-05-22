import { NextRequest, NextResponse } from "next/server";
import { AccessMiddleware, AuthMiddleware } from "@/middlewares";

export default async function proxy(request: NextRequest) {

    return AuthMiddleware(request)
        .then(response => response || AccessMiddleware(request))
        .then(response => response || NextResponse.next())
}

export const config = {
    matcher: [
        /*
         * Apply proxy to all routes except those starting with:
         * - api (API routes)
         * - _next/static (static files)
         * - _next/image (image optimization files)
         * - favicon.ico
         * - Files with common extensions
         */
        '/((?!api|_next/static|_next/image|favicon.ico|icon-.*\\.png|.*\\.png|.*\\.jpg|.*\\.jpeg|.*\\.svg|.*\\.ico).*)',
    ],
};
