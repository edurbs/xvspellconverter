import { describe, expect, it } from 'vitest';
import { copyText, type ClipboardLike } from '../src/clipboard';

describe('copyText', () => {
  it('resolves "copied" when the clipboard write succeeds', async () => {
    const clipboard: ClipboardLike = {
      writeText: () => Promise.resolve(),
    };
    await expect(copyText('texto', clipboard)).resolves.toBe('copied');
  });

  it('resolves "failed" when the clipboard write rejects', async () => {
    const clipboard: ClipboardLike = {
      writeText: () => Promise.reject(new Error('denied')),
    };
    await expect(copyText('texto', clipboard)).resolves.toBe('failed');
  });

  // Non-secure origins (plain http) expose no navigator.clipboard at all —
  // the guard must not throw synchronously.
  it('resolves "failed" when no clipboard API exists (non-secure context)', async () => {
    await expect(copyText('texto', undefined)).resolves.toBe('failed');
  });

  it('resolves "failed" when clipboard lacks writeText', async () => {
    await expect(copyText('texto', {} as ClipboardLike)).resolves.toBe('failed');
  });
});
