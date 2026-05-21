import { NextRequest, NextResponse } from "next/server";
import { AuthService } from "@/services";
import { Role } from "@/enums";

/**
 * @fileoverview Access Control Middleware
 * @implements Role-based access control, route protection
 * @features Multi-role support (admin, user)
 */
export async function AccessMiddleware(request: NextRequest) {
    const { pathname } = request.nextUrl;

    const role = await AuthService.getAuthRole();

    if (pathname.startsWith('/admin') && role !== Role.ADMIN)
        return NextResponse.redirect(new URL('/dashboard', request.url));

    if (pathname === "/")
        return NextResponse.redirect(new URL('/dashboard', request.url));

    return NextResponse.next();
}
