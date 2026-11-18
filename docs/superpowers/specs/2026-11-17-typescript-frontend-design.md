# TypeScript Frontend-Only Converter — Design

## Intent

Rewrite the Xavante orthography converter ("Conversor de Grafia Xavante", xvspellconverter)
as a **pure frontend application in TypeScript** — no backend, no database, no server.
The current Java/JMix code stays in the repository untouched as reference; the new app
lives in `web/` and builds to static files deployable anywhere (the site is currently
conversorxv.eduardo.soares.nom.br).

Why: the app is one page of text-in → text-out; the JMix/Vaadin/Postgres stack exists
only to host it and a dictionary lookup. A static page with the dictionary embedded
does the same with zero infrastructure.

## Goals

- Identical conversion behavior to the Java implementation (which the green unit
  tests on branch `upgrade-jmix3-no-db` pin down)
- Same UI: title, input textarea (autofocus), Converter + Limpar buttons, readonly
  output textarea, auto-copy to clipboard after conversion, empty-input error
  notification, success notification
- 558-word dictionary embedded in the frontend bundle
- Type-safe (strict TS), tested (Vitest port of the Java tests), fast build (Vite)

## Non-goals

- No login, no admin UI, no user management (matching the DB-free decision)
- No server, no SSR, no API
- No removal of the Java code in this change (a later cleanup can delete it)

## Stack (decided)

- **Vite 7 + vanilla TypeScript** (no framework — one page, no state complexity)
- **Vitest** for unit tests
- Node 22 / npm 10 (already installed user-locally at `~/.local/node`)

## Repo layout

```
web/                  ← new; the TS app (Java/Gradle stays at the repo root)
  index.html
  package.json, tsconfig.json
  src/
    main.ts           DOM wiring: buttons, textareas, notifications, clipboard
    converter.ts      port of ConvertUseCase — pure function convertText(text): string
    dictionary.ts     parse of embedded words.tsv → Map<string,string>
    data/words.tsv    copy of the 558-line dictionary (from upgrade-jmix3-no-db)
  tests/converter.test.ts   port of ConvertUseCaseTest (same inputs/expected values)
```

## Behavior spec (ported from ConvertUseCase — the binding contract)

For input text:
1. Normalize NFC (Java matched with CANON_EQ, which is canonical-equivalence
   matching; NFC input makes the JS regexes behave identically for composed text)
2. Clean: U+00A0 → space; `'` → ꞌ (U+A78C)
3. Split into tokens on boundaries `(?=[.,:;! “”‘’()—\n\r])|(?<=...)` — the class
   includes a literal space; JS lookbehind is ES2018+ ✓
4. Per non-blank token, in order:
   a. letter rules: `(^| )ĩ→ĩ̱`, `(^| )Ĩ→Ĩ̱`, `(^| )i→ĩ`, `(^| )I→Ĩ` (the `i→ĩ` rule
      fires only at word start / after space); `Z[upper]→DZ+char`, `z[lower]→dz+char`,
      `Z[lower]→Dz+char`, `S[upper]→TS+char`, `s[lower]→ts+char`, `S[lower]→Ts+char`;
      `â→ö`, `Â→Ö` — applied in this order, so "Mozésihi" → "Modzétsihi"
   b. suffix rule: if token ends with `h<lastChar>`, strip 2 chars and dictionary-lookup
      the stem; hit → use the stem's `to`
   c. whole-word rule: length > 1 → dictionary-lookup the token (case-sensitive
      exact, keys are post-letter-conversion forms like "Modzétsi", "Dzuwã")
5. Blank tokens pass through unchanged

Dictionary: 558 `from→to` pairs, parsed once at startup from `data/words.tsv`
(imported with Vite's `?raw`).

## Verification

- `npm run test` — the ported Java test suite (8 cases, same strings), green
- `npm run dev` — paste text, convert, output + clipboard match the Java app
- `npm run build` — static `dist/` builds clean

## Left to the builder

- Exact CSS (functional styling; current app has minimal theme-specific styling)
- Notification UX: native text near the buttons instead of Vaadin's notification
  component (same messages: "Sem texto para converter!" / "Copiado para a área de transferência")
