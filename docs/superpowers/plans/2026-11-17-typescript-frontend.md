# TypeScript Frontend-Only Converter Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Port the Xavante orthography converter to a static TypeScript app in `web/` (Vite + vanilla TS + Vitest) with the 558-word dictionary embedded — no backend.

**Architecture:** `converter.ts` is a pure function `convertText(text, dictionary)` mirroring Java `ConvertUseCase` step-for-step; `dictionary.ts` parses the embedded `words.tsv` (Vite `?raw`) into a case-sensitive `Map`; `main.ts` wires the DOM. Vitest ports the 8 Java test cases verbatim as the parity contract.

**Tech Stack:** TypeScript (strict), Vite 7, Vitest, Node 22 (`PATH=~/.local/node/bin:$PATH`).

**Spec:** `docs/superpowers/specs/2026-11-17-typescript-frontend-design.md`

## Global Constraints

- All commands run from repo root `/home/eduardo/vscode/xvspellconverter` with `PATH=~/.local/node/bin:$PATH` prefix
- New code lives **only** under `web/`; Java/Gradle files at the repo root are never modified
- The conversion contract is the ported Java test suite — exact input/expected strings below; no behavior drift allowed
- Unicode pins: ꞌ = U+A78C; ĩ̱ replacement = U+0129 + U+0331; `'`→U+A78C; nbsp U+00A0→space; `—` in the split class is U+2014; curly quotes U+201C U+201D U+2018 U+2019
- Dictionary keys are post-letter-conversion forms (e.g. `Modzétsi`, `Dzuwã`); lookups are case-sensitive exact `Map.get` (miss → undefined)
- `npm run build` must produce a clean static `dist/`; `npm run test` green before each commit

## Review Focus

Failure modes the tests may not exercise, most likely first:

1. **Space handling in the split regex** — the Java split class contains a literal space, so tokens never contain spaces and the `(^| )` alternative in the i-rules can only match at token start; a port that drops the space from the class would glue words. Pinned by `shouldConvertAllWordFromALine` (Task 2).
2. **Rule order (z→dz then s→ts)** — "Mozésihi" must become "Modzétsihi" (both rules), then suffix strip yields dictionary key "Modzétsi" → "Moisés". Pinned by `shouldConvertNameWithSuffix` (Task 2).
3. **Case sensitivity + composed Unicode in dictionary keys** — "teofilu" must miss, "Abiꞌata" (U+A78C inside) must hit. Pinned by dictionary tests (Task 1).
4. **First-person ĩ rules only at token start** — "norĩ" and "aihĩni" must stay unchanged while "ina"→"ĩna". Pinned by `shouldConvertI` (Task 2).
5. **Clipboard requires secure context** — `navigator.clipboard` fails on plain http; the UI must degrade to a visible notification instead of throwing. Pinned in Task 3's UI error handling (no test; verified by code reading in review).

---

### Task 1: Scaffold the app and embed the dictionary

**Files:**
- Create: `web/package.json`, `web/tsconfig.json`, `web/index.html`, `web/src/vite-env.d.ts`, `web/src/style.css`, `web/src/main.ts` (minimal stub that imports nothing else yet)
- Create: `web/src/data/words.tsv` (copied from branch `upgrade-jmix3-no-db`)
- Create: `web/src/dictionary.ts`
- Test: `web/tests/dictionary.test.ts`
- Modify: `.gitignore` (ignore `web/node_modules/`, `web/dist/`)

**Interfaces:**
- Produces: `loadDictionary(): Map<string, string>` in `web/src/dictionary.ts` — reads `./data/words.tsv?raw`, skips blank lines, splits on `\t` (limit 2), UTF-8; first field = key. Used by Task 2's tests via the real file and by `main.ts` in Task 3.

- [ ] **Step 1: Scaffold files**

`package.json` scripts: `dev` = `vite`, `build` = `tsc && vite build`, `test` = `vitest run`; devDependencies: `typescript`, `vite`, `vitest` (latest stable; install and let npm resolve versions). `tsconfig.json`: `strict: true`, `target: ES2022`, `module: ESNext`, `moduleResolution: bundler`, `include: ["src", "tests"]`. Copy the dictionary:

```bash
mkdir -p web/src/data web/tests
git show upgrade-jmix3-no-db:src/main/resources/words/words.tsv > web/src/data/words.tsv
wc -l web/src/data/words.tsv   # expect 558
```

`.gitignore`: append `web/node_modules/` and `web/dist/`.

- [ ] **Step 2: Write the failing dictionary test**

```ts
import { describe, expect, it } from 'vitest';
import { loadDictionary } from '../src/dictionary';

describe('loadDictionary', () => {
  const dict = loadDictionary();

  it('loads the 558 seed entries', () => {
    expect(dict.size).toBe(558);
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
```

- [ ] **Step 3: Run it to verify it fails**

Run: `cd web && PATH=~/.local/node/bin:$PATH npx vitest run`
Expected: FAIL — `dictionary.ts` does not exist (module not found).

- [ ] **Step 4: Implement `dictionary.ts`**

```ts
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
```

- [ ] **Step 5: Run tests to verify green; install deps first if not done**

Run: `cd web && PATH=~/.local/node/bin:$PATH npm install && PATH=~/.local/node/bin:$PATH npx vitest run`
Expected: PASS (2 tests).

- [ ] **Step 6: Commit**

```bash
git add -A && git commit -m "feat(web): scaffold Vite TS app with embedded 558-word dictionary"
```

### Task 2: Port the converter (TDD against the Java suite)

**Files:**
- Create: `web/src/converter.ts`
- Test: `web/tests/converter.test.ts`

**Interfaces:**
- Consumes: `Map<string, string>` dictionary (Task 1's shape; tests may pass their own fixtures)
- Produces: `convertText(text: string, dictionary: Map<string, string>): string` — pure, no I/O

- [ ] **Step 1: Write the failing tests — the ported Java suite, strings verbatim**

```ts
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
    expect(convert('ĩna')).toBe('ĩ\u0331na');   // ĩ + combining macron below
    expect(convert('Ĩna')).toBe('Ĩ\u0331na');
    expect(convert('ina')).toBe('ĩna');
    expect(convert('Ina')).toBe('Ĩna');
    expect(convert('INA')).toBe('ĨNA');
    expect(convert('norĩ')).toBe('norĩ');        // ĩ not at token start: unchanged
    expect(convert('NORĨ')).toBe('NORĨ');
    expect(convert('ĩmorĩ')).toBe('ĩ\u0331morĩ');
    expect(convert('aihĩni')).toBe('aihĩni');
  });

  it('shouldRemovePhantomSpace', () => {
    expect(convert('text\u00A0text')).toBe('text text'); // nbsp → space
  });

  it('shouldConvertGlottal', () => {
    expect(convert("danhipti'a")).toBe('danhiptiꞌa');   // ' → U+A78C
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
```

- [ ] **Step 2: Run to verify RED**

Run: `cd web && PATH=~/.local/node/bin:$PATH npx vitest run`
Expected: FAIL — `converter.ts` does not exist.

- [ ] **Step 3: Implement `converter.ts`**

Port order, exactly: NFC-normalize input → clean (replace `\u00A0` with `' '`, `'` with `ꞌ`/U+A78C) → split with `/(?=[.,:;! \u201C\u201D\u2018\u2019()\u2014\n\r])|(?<=[.,:;! \u201C\u201D\u2018\u2019()\u2014\n\r])/` → per non-blank token: `convertLetters` → `convertWordWithSuffix` → `convertWord` → append.

- `convertLetters`: `replace(/(^| )ĩ/g, '$1ĩ\u0331')` then the same for `Ĩ`, `i`→`ĩ`, `I`→`Ĩ` (keep the capture group so the Java space-eating semantics are preserved exactly); then pair rules via `word.replace(/Z[A-ZÀ-ÖØ-ÝĀ-Ž]/g, (m) => 'DZ' + m.slice(1))` and the five remaining z/S rules; then `â→ö`, `Â→Ö`.
- `convertWordWithSuffix(word)`: if `word.length >= 2` and `word.endsWith('h' + word.at(-1))`, stem = `word.slice(0, -2)`; if `stem.length >= 2` and `dict.get(stem)` is a non-empty string → return it; else return `word`.
- `convertWord(word)`: if `word.length > 1`, hit = `dict.get(word)`; non-empty hit → return it; else return `word`.

- [ ] **Step 4: Run to verify GREEN**

Run: `cd web && PATH=~/.local/node/bin:$PATH npx vitest run`
Expected: PASS (all 8 ported cases + the 2 dictionary tests).

- [ ] **Step 5: Commit**

```bash
git add -A && git commit -m "feat(web): port ConvertUseCase to TypeScript, parity-verified against the Java suite"
```

### Task 3: Wire the UI and produce the static build

**Files:**
- Modify: `web/index.html` (full page structure), `web/src/main.ts` (real wiring), `web/src/style.css`
- Produce: `web/dist/` (git-ignored)

**Interfaces:**
- Consumes: `convertText` (Task 2), `loadDictionary` (Task 1)

- [ ] **Step 1: Page structure in `index.html`**

Elements with these ids (mirroring the Vaadin view): `h1` "Conversor de Grafia Xavante"; `<textarea id="textAreaFrom" placeholder="Cole aqui o texto para converter" autofocus>`; buttons `#buttonConvert` (label "Converter") and `#buttonClean` ("Limpar"); `<textarea id="textAreaTo" readonly>`; a `#notification` region for messages. `<title>XV Converter</title>`. Script tag `type="module" src="/src/main.ts"`; link `style.css`.

- [ ] **Step 2: Wire `main.ts`**

Load the dictionary once. On `#buttonConvert` click: read `textAreaFrom.value`; if blank → show "Sem texto para converter!" (error styling); else `convertText`, write to `textAreaTo`, `navigator.clipboard.writeText(result)` then show "Copiado para a área de transferência" — clipboard failure (non-secure context) must show the success text without the copy claim OR a fallback message, never an unhandled rejection. On `#buttonClean`: clear both textareas. Keep `textAreaTo.focus()` after conversion.

- [ ] **Step 3: Build and smoke**

Run: `cd web && PATH=~/.local/node/bin:$PATH npm run build && PATH=~/.local/node/bin:$PATH npx vitest run`
Expected: `tsc` clean (strict), `dist/` produced, all tests green.

Run: `cd web && PATH=~/.local/node/bin:$PATH npx vite preview --port 4173` in background; `curl -s http://localhost:4173/ | grep -c "textAreaFrom"` → `1`; stop the server.

- [ ] **Step 4: Commit**

```bash
git add -A && git commit -m "feat(web): converter UI with clipboard + notifications, static build"
```
