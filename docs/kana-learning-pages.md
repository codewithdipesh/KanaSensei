# Kana Learning Pages — Engineering & UI Guide

How the **Listen / Trace / Stroke / Write** lesson pages work, end to end. Read this first if
you're touching the lesson screen or the kana-drawing code.

---

## 1. The one idea that makes everything simple

A kana like **あ** is drawn from a [KanjiVG](https://kanjivg.tagaini.net/) SVG. The key fact:

> **KanjiVG paths are *median (skeleton) lines* — one `<path>` per stroke, in stroke order — not
> filled outlines.**

So the solid あ you see is just those centerlines drawn with a **thick, round-cap brush**. That
single realization collapses four "different" pages into **one set of paths rendered four ways**:

| Page       | Same stroke paths, rendered as…                                         |
|------------|-------------------------------------------------------------------------|
| **Listen** | thick · full opacity                                                    |
| **Trace**  | thick · low opacity (the faint guide)                                   |
| **Stroke** | faint guide **+** the same paths revealed one-by-one (animation)        |
| **Write**  | optional faint guide **+** user's ink matched against the same paths    |

Because every layer is **the same geometry through the same transform**, the "make the stroke line
up exactly on top of the faint background" problem **disappears** — alignment is guaranteed by
construction, not by math we could get wrong.

That shared transform is [`KanaViewport`](#kanaviewport-the-alignment-guarantee).

---

## 2. Big-picture data flow

```
 Firestore                     Network (Ktor)          Pure parser
 ┌──────────┐  getKana()       ┌──────────────┐        ┌───────────────┐
 │characters│ ───────────────▶ │ raw .svg text│ ─────▶ │ KanjiVgParser │
 │  (svgUrl)│  getKanaSvg(url)  └──────────────┘        └──────┬────────┘
 └──────────┘                                                  │ KanaStrokes
                                                               │ (viewBox + List<d-string>)
                                                               ▼
                                            ┌─────────────────────────────────┐
                                            │ LessonViewModel.load()          │
                                            │  builds:                        │
                                            │   kanaById:   Map<id,Character> │
                                            │   strokesById:Map<id,KanaStrokes>│
                                            └────────────────┬────────────────┘
                                                             │ LessonUiState
                                                             ▼
                  NavGraph ──▶ LessonScreen ──▶ LessonComponent ──▶ KanaStage
                                                                       │
                          rememberKanaPaths()  (d-string → Compose Path, viewBox space)
                                                                       │
                            ┌──────────────┬───────────────┬──────────┴─────────┐
                            ▼              ▼               ▼                    ▼
                        KanaGlyph     KanaGlyph     KanaStrokeAnimator     KanaWritePad
                        (Listen)       (Trace)         (Stroke)             (Write)
```

Everything below `KanaStage` works in **viewBox coordinates** (KanjiVG is `0 0 109 109`) and is
mapped to pixels by `KanaViewport`.

---

## 3. Where the code lives

Components sit in **`sharedfeature/learning`** (not `sharedcore/ui`) because they need both
`KanaStrokes` (from `:sharedcore:core`) and Compose (from `:sharedcore:ui`), and only the
`learning` module depends on both.

### Data layer — `:sharedcore:core`
| File | Role |
|------|------|
| `model/content/KanaStrokes.kt` | Holds `viewBoxWidth/Height` + `strokePaths: List<String>` (raw `d` attrs). No graphics dependency. |
| `svg/KanjiVgParser.kt` | `parse(svg) -> KanaStrokes`. Regex-extracts every `<path d="…">` in order; reads `viewBox`. KanjiVG is machine-generated so a full XML parser isn't needed. |
| `repository/LearningRepository.kt` | Interface; added `getKanaSvg(svgUrl): String?`. |
| `repository/LearningRepositoryImpl.kt` (androidMain) | Fetches SVG text via the shared Ktor `HttpClient`, with an in-memory session cache. |

### UI layer — `:sharedfeature:learning` → `lesson/components/kana/`
| File | Role |
|------|------|
| `KanaViewport.kt` | The shared viewBox→canvas transform + `withKanaViewport{}` + `rememberKanaPaths()` + `KanaGlyphDefaults` (brush width, guide alpha). |
| `KanaGlyph.kt` | Draws a `List<Path>` as brush strokes. Used by **Listen** (full) and **Trace** (faint). |
| `KanaStrokeAnimator.kt` | **Stroke** page: reveals strokes in order with a moving brush tip via `PathMeasure`. |
| `StrokeMatcher.kt` | Pure handwriting-matching logic (no Compose UI, fully unit-tested). |
| `KanaWritePad.kt` | **Write** page: captures finger input, matches per stroke, snaps/flashes, reports completion. |
| `KanaStage.kt` | The cream card; switches on `LessonPageType` and composes the right layers. **Start here to read the UI.** |

### Screen plumbing — `:sharedfeature:learning` → `lesson/`
| File | Role |
|------|------|
| `LessonViewModel.kt` | Loads pages + characters, fetches & parses SVGs in parallel, exposes `kanaById`/`strokesById`; `next()` advances pages. |
| `LessonUiState.kt` | Adds `kanaById` + `strokesById`. |
| `LessonScreen.kt` | Picks the selected page's kana + strokes and renders `LessonComponent`. |
| `components/LessonComponent.kt` | Page chrome: top bar, audio buttons, the `KanaStage`, replay/eye button, "Nice Work!", gated Continue. |

### Entry point — `:mobile`
`navigation/KanaSenseiNavGraph.kt` builds `LessonViewModel`, calls `LessonScreen`, wires
`onClose` (pop) and `onContinue` (`next()` or pop on last page).

---

## 4. `KanaViewport` — the alignment guarantee

```kotlin
data class KanaViewport(val scale, val offset, val viewBoxWidth, val viewBoxHeight) {
    fun toCanvas(p: Offset): Offset   // viewBox unit  → screen px
    fun toViewBox(p: Offset): Offset  // screen px      → viewBox unit
    companion object { fun fit(canvas: Size, vbW, vbH): KanaViewport }
}
```

- `fit()` scales the glyph uniformly to fit the card and centers it (like SVG
  `preserveAspectRatio="xMidYMid meet"`).
- `withKanaViewport(vp){ … }` transforms the `DrawScope` so paths parsed straight from the SVG draw
  with their native coordinates. **Stroke widths in viewBox units scale automatically**, so the
  brush weight is resolution-independent.
- Touch input is converted the other way (`toViewBox`) so matching tolerances are constant
  regardless of screen size.

**Rule:** any new layer that must line up with the glyph should draw through `withKanaViewport`
(or convert points with `toViewBox`). Never hand-roll a second transform.

---

## 5. Each page, precisely

### Listen
`KanaGlyph(paths, color = learningDrawing, alpha = 1f)`. That's it. Audio buttons live in
`TopElement` (`LessonComponent`).

### Trace
Same as Listen but `alpha = GUIDE_ALPHA` (0.18). The faint character to trace over.

### Stroke
Faint guide **+** `KanaStrokeAnimator`:
- One `PathMeasure` per stroke (cached) gives its length.
- An `Animatable` drives `progress 0→1`; `getSegment(0, progress·length)` reveals the active stroke;
  a dot at `getPosition(progress·length)` is the brush tip (direction is free — paths start at the
  stroke's beginning).
- Finished strokes stay solid; upcoming strokes are just the faint guide behind.
- `replayKey++` (the replay button) restarts from stroke one.

### Write
Optional faint guide **+** `KanaWritePad`:
- `detectDragGestures` collects the current finger stroke (canvas px).
- On finger-up, points are mapped to viewBox space and checked by `StrokeMatcher` against the
  **current** target stroke (strict order).
- **Pass:** snap to the clean target stroke, advance, `correctHaptic()`; when the last stroke passes,
  `onComplete()` fires → "Nice Work!" + Continue enabled.
- **Miss:** `wrongHaptic()` + a brief red flash of the attempt; the user retries the same stroke.
- `replayKey++` resets all progress.

---

## 6. The matching algorithm (`StrokeMatcher`)

Lenient and learner-friendly by design. Everything in **viewBox units** (109×109 space):

A user stroke is accepted only if **all** hold:
1. **Start** within `startEnd` (24) of the target's start.
2. **End** within `startEnd` of the target's end. *(1 + 2 also enforce direction.)*
3. **≥ 68%** of the user's points lie within the `tube` (20) around the stroke — they didn't wander.
4. **≥ 55%** of the target is covered by the user's points — they drew most of it, not a stub.

All thresholds live in `StrokeMatcher.Tolerance`, so tuning never touches the algorithm. Covered by
`StrokeMatcherTest` (follows / reversed / partial / off-tube / too-few-points).

---

## 7. Tuning knobs (all centralized)

| Want to change… | Edit |
|-----------------|------|
| Brush thickness | `KanaGlyphDefaults.STROKE_WIDTH` (viewBox units) |
| Faint guide opacity | `KanaGlyphDefaults.GUIDE_ALPHA` |
| Animation speed / pacing | `MS_PER_VIEWBOX_UNIT`, `MIN/MAX_STROKE_MS`, `PAUSE_BETWEEN_STROKES_MS` in `KanaStrokeAnimator.kt` |
| Brush tip size | `tipRadius` in `KanaStrokeAnimator.kt` |
| Matching strictness | `StrokeMatcher.Tolerance` |
| Colors | `KanaColors.learningDrawing` / `learningBackground` / `success` / `error` |
| Card shape/padding | `KanaCard` + `glyphModifier` in `KanaStage.kt` |

---

## 8. How to extend

**Add a new page type** (e.g. a "ghost replay" of the user's writing):
1. Add the value to `LessonPageType` (`:sharedcore:core`).
2. Add a `when` branch in `KanaStage.kt` composing the layers you need — reuse `KanaGlyph` /
   `withKanaViewport` so it stays aligned.
3. If it needs controls, extend `BottomElement` in `LessonComponent.kt`.

**Swap the data source** (e.g. bundle SVGs as assets, or preprocess to JSON):
- Keep `KanaStrokes` as the contract. Only change who produces it: replace `getKanaSvg` + the
  `KanjiVgParser.parse(...)` call in `LessonViewModel`. Nothing in the UI changes.

**Make stroke pages work fully offline:**
- Today the SVG cache is in-memory (per session). Add a Room entity keyed by `svgUrl` and have
  `getKanaSvg` read/write it, mirroring how content sync already caches lessons.

---

## 9. Testing

| Test | Module | Run |
|------|--------|-----|
| `KanjiVgParserTest` | `:sharedcore:core` | `./gradlew :sharedcore:core:testAndroidHostTest` |
| `StrokeMatcherTest` | `:sharedfeature:learning` | `./gradlew :sharedfeature:learning:testAndroidHostTest` |

The two hardest-to-eyeball pieces — SVG parsing and handwriting matching — are pure functions with
unit tests, so they can be tuned with confidence. The visual layers are verified on-device.

---

## 10. Known limitations / TODO

- **Assumes KanjiVG-shaped SVGs** (separate `<path>` per stroke, in order). A single filled-outline
  SVG would render but couldn't animate stroke order or match per-stroke.
- **SVG cache is session-only** (see §8 for the offline upgrade).
- **No paging UI yet** — `next()` advances one page; the top progress bar from the Figma isn't wired.
- **Quiz page** (`LessonPageType.QUIZ`) renders nothing yet.
- Matching is intentionally lenient; a stricter, scored mode (proportion/coverage similarity) can be
  added behind the same `StrokeMatcher` interface.

---

*Mental model in one line: **one set of KanjiVG median paths, one shared viewBox→canvas transform,
rendered/animated/matched four different ways.***
