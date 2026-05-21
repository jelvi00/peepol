
export const doNothing = (x: unknown) => x

export function wait(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}
