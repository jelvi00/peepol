import { NextRequest, NextResponse } from "next/server";
import { AuthService } from "@/services";

export async function AuthMiddleware(request: NextRequest) {

    const { pathname } = request.nextUrl;

    if (["/login"].includes(pathname)) return NextResponse.next();

    const session = Boolean(await AuthService.getAuthToken());

    if (!session) return NextResponse.redirect(new URL('/login', request.url));

}
