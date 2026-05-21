"use server"

import { LoginForm } from "@/components/login/login-form";
import { AuthLayout } from "@/components/layout";

export default async function Login() {


    return (
        <AuthLayout>
            <div className="relative font-light z-30 h-screen w-screen flex items-center justify-center">
                <section
                    className="w-150 bg-white rounded-lg drop-shadow-lg flex gap-5 flex-col items-center p-20">
                    <section className={'z-1 flex flex-col items-center'}>
                        <h1 className="text-basic-red my-1 font-sans text-[24px]">
                            {'Peepol Login'}
                        </h1>
                        <LoginForm />
                    </section>
                </section>
            </div>
        </AuthLayout>
    );
}
