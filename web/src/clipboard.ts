/**
 * Clipboard access with a safe guard: `navigator.clipboard` is undefined on
 * non-secure origins (plain http), and accessing `.writeText` on it throws a
 * synchronous TypeError. Every failure path resolves to "failed" instead.
 */
export type ClipboardLike = {
  writeText?(text: string): Promise<void>;
};

export function copyText(
  text: string,
  clipboard: ClipboardLike | undefined = globalThis.navigator?.clipboard,
): Promise<'copied' | 'failed'> {
  const write = clipboard?.writeText;
  if (typeof write !== 'function') return Promise.resolve('failed');
  try {
    return write.call(clipboard, text).then(
      () => 'copied' as const,
      () => 'failed' as const,
    );
  } catch {
    return Promise.resolve('failed');
  }
}
