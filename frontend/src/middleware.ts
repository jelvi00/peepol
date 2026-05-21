import { NextRequest, NextResponse } from "next/server";
import { AccessMiddleware, AuthMiddleware } from "@/middlewares";

export async function middleware(request: NextRequest) {

    let response = await AuthMiddleware(request);

    if (!response) response = await AccessMiddleware(request);

    return response || NextResponse.next();
}

export const config = {
    matcher: [
        /*
         * Apply middleware to all routes except those starting with:
         * - api (API routes)
         * - _next/static (static files)
         * - _next/image (image optimization files)
         * - favicon.ico
         * - Files with common extensions
         */
        '/((?!api|_next/static|_next/image|favicon.ico||icon-.*\\.png|.*\\.png|.*\\.jpg|.*\\.jpeg|.*\\.svg|.*\\.ico).*)',
    ],
};
