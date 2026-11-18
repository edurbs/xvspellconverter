import { describe, expect, it } from 'vitest';
import { convertText } from '../src/converter';
import { loadDictionary } from '../src/dictionary';

const convert = (text: string) => convertText(text, loadDictionary());

describe('convertText (port of ConvertUseCaseTest)', () => {
  it('shouldConvertZtoDZ', () => {
    expect(convert('aiwazé')).toBe('aiwadzé');
    expect(convert('zara')).toBe('dzara');
    expect(convert('Zara')).toBe('Dzara');
    expect(convert('ZARA')).toBe('DZARA');
  });

  it('shouldConvertTtoTS', () => {
    expect(convert('pese')).toBe('petse');
    expect(convert('saré')).toBe('tsaré');
    expect(convert('Saré')).toBe('Tsaré');
    expect(convert('SARÉ')).toBe('TSARÉ');
  });

  it('shouldConvertAtoO', () => {
    expect(convert('â')).toBe('ö');
    expect(convert('Â')).toBe('Ö');
    expect(convert('âiba')).toBe('öiba');
    expect(convert('Âiba')).toBe('Öiba');
    expect(convert('ÂIBA')).toBe('ÖIBA');
    expect(convert('aibâ')).toBe('aibö');
    expect(convert('Aibâ')).toBe('Aibö');
    expect(convert('AIBÂ')).toBe('AIBÖ');
  });

  it('shouldConvertI', () => {
    expect(convert('ĩna')).toBe('ĩ\u0331na'); // ĩ + combining macron below
    expect(convert('Ĩna')).toBe('Ĩ\u0331na');
    expect(convert('ina')).toBe('ĩna');
    expect(convert('Ina')).toBe('Ĩna');
    expect(convert('INA')).toBe('ĨNA');
    expect(convert('norĩ')).toBe('norĩ'); // ĩ not at token start: unchanged
    expect(convert('NORĨ')).toBe('NORĨ');
    expect(convert('ĩmorĩ')).toBe('ĩ\u0331morĩ');
    expect(convert('aihĩni')).toBe('aihĩni');
  });

  it('shouldRemovePhantomSpace', () => {
    expect(convert('text\u00A0text')).toBe('text text'); // nbsp → space
  });

  it('shouldConvertGlottal', () => {
    expect(convert("danhipti'a")).toBe('danhiptiꞌa'); // ' → U+A78C
  });

  it('shouldConvertNameWithSuffix', () => {
    // z→dz AND s→ts give "Modzétsihi"; "hi" strips → "Modzétsi" → dictionary → "Moisés"
    expect(convert('Mozésihi')).toBe('Moisés');
  });

  it('shouldConvertAllWordFromALine', () => {
    const textToConvert = "i'azada'âbâzém na ãhã zama tahã. 48 Ãhã Zuwã Mozésihi Imama norĩ hã";
    const expected = 'ĩꞌadzadaꞌöbödzém na ãhã dzama tahã. 48 Ãhã João Moisés Ĩmama norĩ hã';
    expect(convert(textToConvert)).toBe(expected);
  });
});
