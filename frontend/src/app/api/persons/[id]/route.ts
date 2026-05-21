import { NextResponse } from 'next/server';
import { PersonService } from '@/services/person.service';

export async function GET(
  _: Request,
  { params }: { params: Promise<{ id: string }> }
) {
  const id = parseInt((await params).id);
  const data = await PersonService.getPerson(id);
  if (!data) return NextResponse.json({ message: 'Not found' }, { status: 404 });
  return NextResponse.json(data);
}

export async function DELETE(
  _: Request,
  { params }: { params: Promise<{ id: string }> }
) {
  const id = parseInt((await params).id);
  try {
    const data = await PersonService.deletePerson(id);
    return NextResponse.json(data);
  } catch (error: any) {
    return NextResponse.json({ message: error.message }, { status: 400 });
  }
}
