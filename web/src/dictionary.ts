import raw from './data/words.tsv?raw';

export function loadDictionary(): Map<string, string> {
  const dict = new Map<string, string>();
  for (const line of raw.split('\n')) {
    if (!line.trim()) continue;
    const idx = line.indexOf('\t');
    if (idx > 0) dict.set(line.slice(0, idx), line.slice(idx + 1));
  }
  return dict;
}
