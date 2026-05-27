import { NextRequest, NextResponse } from "next/server";

export function GET(_: NextRequest) {
    return NextResponse.json(
        { status: 'OK', message: 'Web is available.' },
        { status: 200 }
    );
}
