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
         * Aplicar middleware a todas las rutas excepto las que empiecen con:
         * - api (API routes)
         * - _next/static (static files)
         * - _next/image (image optimization files)
         * - favicon.ico
         * - Archivos con extensiones comunes
         */
        '/((?!api|_next/static|_next/image|favicon.ico||icon-.*\\.png|.*\\.png|.*\\.jpg|.*\\.jpeg|.*\\.svg|.*\\.ico).*)',
    ],
};
