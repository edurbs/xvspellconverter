/**
 * Port of the Java ConvertUseCase (infra/app hexagonal core), step for step.
 * The dictionary maps post-letter-conversion forms to their replacements
 * (e.g. "Modzétsi" → "Moisés", "Dzuwã" → "João").
 */

const GLOTTAL = 'ꞌ'; // U+A78C
const TOKEN_BOUNDARY = /(?=[.,:;! \u201C\u201D\u2018\u2019()\u2014\n\r])|(?<=[.,:;! \u201C\u201D\u2018\u2019()\u2014\n\r])/;

export function convertText(textToConvert: string, dictionary: Map<string, string>): string {
  const clean = textToConvert.normalize('NFC').replace(/\u00A0/g, ' ').replace(/'/g, GLOTTAL);
  const result: string[] = [];
  for (const token of clean.split(TOKEN_BOUNDARY)) {
    result.push(token.trim() ? convert(token, dictionary) : token);
  }
  return result.join('');
}

function convert(token: string, dictionary: Map<string, string>): string {
  let word = convertLetters(token);
  word = convertWordWithSuffix(word, dictionary);
  word = convertWord(word, dictionary);
  return word;
}

function convertLetters(word: string): string {
  // first-person ĩ keeps a macron below; third-person i/Ĩ rules fire only at token start
  word = word.replace(/(^| )ĩ/g, '$1ĩ\u0331');
  word = word.replace(/(^| )Ĩ/g, '$1Ĩ\u0331');
  word = word.replace(/(^| )i/g, '$1ĩ');
  word = word.replace(/(^| )I/g, '$1Ĩ');
  // z → dz, s → ts, preserving the case pattern of the following letter
  word = replaceLetter(word, /Z[A-ZÀ-ÖØ-ÝĀ-Ž]/g, 'DZ');
  word = replaceLetter(word, /z[a-zà-öø-ýā-ž]/g, 'dz');
  word = replaceLetter(word, /Z[a-zà-öø-ýā-ž]/g, 'Dz');
  word = replaceLetter(word, /S[A-ZÀ-ÖØ-ÝĀ-Ž]/g, 'TS');
  word = replaceLetter(word, /s[a-zà-öø-ýā-ž]/g, 'ts');
  word = replaceLetter(word, /S[a-zà-öø-ýā-ž]/g, 'Ts');
  return word.replace(/â/g, 'ö').replace(/Â/g, 'Ö');
}

function replaceLetter(word: string, regex: RegExp, replaceWith: string): string {
  return word.replace(regex, (matched) => replaceWith + matched.slice(1));
}

function convertWordWithSuffix(originalWord: string, dictionary: Map<string, string>): string {
  if (!originalWord || originalWord.trim() === '') return originalWord;
  const wordLength = originalWord.length;
  const lastChar = originalWord.charAt(wordLength - 1);
  const suffix = 'h' + lastChar;
  if (!originalWord.endsWith(suffix)) return originalWord;
  const wordWithoutSuffix = originalWord.substring(0, wordLength - 2);
  if (wordWithoutSuffix.length < 2) return originalWord;
  const wordConverted = dictionary.get(wordWithoutSuffix);
  if (wordConverted !== undefined && wordConverted !== '') return wordConverted;
  return originalWord;
}

function convertWord(originalWord: string, dictionary: Map<string, string>): string {
  if (originalWord.length > 1) {
    const newWord = dictionary.get(originalWord);
    if (newWord !== undefined && newWord !== '') return newWord;
  }
  return originalWord;
}
