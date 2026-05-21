import { NextResponse } from 'next/server';
import { PersonService } from '@/services/person.service';

export async function GET(request: Request) {
  const { searchParams } = new URL(request.url);
  const q = searchParams.get('q');
  const page = parseInt(searchParams.get('page') || '0');
  const size = parseInt(searchParams.get('size') || '10');
  const status = searchParams.get('status') || '1';

  if (q) {
    const data = await PersonService.searchPersons(q, page, size, status);
    return NextResponse.json(data);
  }

  const data = await PersonService.getPersons(page, size, status);
  return NextResponse.json(data);
}

export async function POST(request: Request) {
  const body = await request.json();
  try {
    const data = await PersonService.createPerson(body);
    return NextResponse.json(data, { status: 201 });
  } catch (error: any) {
    return NextResponse.json({ message: error.message }, { status: 400 });
  }
}

export async function PUT(request: Request) {
  const body = await request.json();
  try {
    const data = await PersonService.updatePerson(body);
    return NextResponse.json(data);
  } catch (error: any) {
    return NextResponse.json({ message: error.message }, { status: 400 });
  }
}
