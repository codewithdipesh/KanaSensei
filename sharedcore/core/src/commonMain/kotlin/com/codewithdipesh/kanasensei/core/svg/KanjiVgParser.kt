package com.codewithdipesh.kanasensei.core.svg

import com.codewithdipesh.kanasensei.core.model.content.KanaStrokes

/**
 * Extracts stroke geometry from a KanjiVG SVG document.
 *
 * KanjiVG files are machine-generated and very regular, so we don't need a full XML parser:
 *  - every `<path>` element is exactly one stroke, already in stroke order;
 *  - the stroke-order *numbers* live in a separate group as `<text>` elements (no `d` attribute),
 *    so matching `d="..."` only on `<path>` tags naturally skips them;
 *  - the coordinate space is declared once in the root `viewBox` (normally `0 0 109 109`).
 *
 * Path `d` values never contain a double quote, so a simple attribute regex is safe and robust.
 */
object KanjiVgParser {

    // <path ... d="M36.2 19.5c..." .../> — only matches the d attribute of <path> elements.
    private val PATH_D = Regex(
        pattern = """<path\b[^>]*?\bd\s*=\s*"([^"]*)"""",
        option = RegexOption.IGNORE_CASE
    )

    // viewBox="minX minY width height"
    private val VIEW_BOX = Regex(
        pattern = """viewBox\s*=\s*"([^"]*)"""",
        option = RegexOption.IGNORE_CASE
    )

    private val WHITESPACE_OR_COMMA = Regex("""[\s,]+""")

    /**
     * Parses [svg] into ordered stroke paths plus the viewBox size. Returns an empty
     * [KanaStrokes] (no strokes, default viewBox) if no `<path>` elements are found, rather than
     * throwing — callers can treat that as "no stroke data available".
     */
    fun parse(svg: String): KanaStrokes {
        val strokes = PATH_D.findAll(svg)
            .map { it.groupValues[1].trim() }
            .filter { it.isNotEmpty() }
            .toList()

        val (width, height) = parseViewBox(svg)

        return KanaStrokes(
            viewBoxWidth = width,
            viewBoxHeight = height,
            strokePaths = strokes
        )
    }

    private fun parseViewBox(svg: String): Pair<Float, Float> {
        val raw = VIEW_BOX.find(svg)?.groupValues?.get(1)?.trim()
        if (raw != null) {
            val parts = raw.split(WHITESPACE_OR_COMMA)
            if (parts.size == 4) {
                val width = parts[2].toFloatOrNull()
                val height = parts[3].toFloatOrNull()
                if (width != null && height != null && width > 0f && height > 0f) {
                    return width to height
                }
            }
        }
        return KanaStrokes.DEFAULT_VIEW_BOX to KanaStrokes.DEFAULT_VIEW_BOX
    }
}
