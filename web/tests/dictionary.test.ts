import { describe, expect, it } from 'vitest';
import { loadDictionary } from '../src/dictionary';

describe('loadDictionary', () => {
  const dict = loadDictionary();

  it('loads the 558 seed rows as 550 unique keys', () => {
    // 558 rows; 8 duplicate from-keys collapse in the Map (2 are spelling variants,
    // last row wins: Ituredza→Itureia, Tsaratiꞌé→Sealtiel)
    expect(dict.size).toBe(550);
    expect(dict.get('Teofilu')).toBe('Teófilo');
    expect(dict.get('Provébius')).toBe('Provérbios');
    expect(dict.get('Abiꞌata')).toBe('Abiatar'); // ꞌ = U+A78C
    expect(dict.get('Dzuwã')).toBe('João');
    expect(dict.get('Modzétsi')).toBe('Moisés');
  });

  it('is case-sensitive', () => {
    expect(dict.get('teofilu')).toBeUndefined();
  });
});
