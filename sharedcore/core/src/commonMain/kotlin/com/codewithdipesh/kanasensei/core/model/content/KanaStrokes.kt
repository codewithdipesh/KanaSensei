package com.codewithdipesh.kanasensei.core.model.content

import kotlinx.serialization.Serializable

/**
 * The stroke geometry of a single kana, extracted from a KanjiVG SVG.
 *
 * KanjiVG paths are *median* (skeleton) lines — one [path][strokePaths] per stroke, listed in
 * stroke order, expressed in the SVG's [viewBox][viewBoxWidth] coordinate space (usually 109x109).
 * Every lesson page (listen / trace / stroke / write) renders these same paths through one shared
 * viewBox->canvas transform, so layers always line up pixel-perfectly.
 *
 * The raw `d` attribute strings are kept as-is; the UI layer turns them into drawable paths with
 * Compose's PathParser. Keeping them as strings means this model has no UI/graphics dependency and
 * stays in `commonMain`.
 */
@Serializable
data class KanaStrokes(
    val viewBoxWidth: Float = DEFAULT_VIEW_BOX,
    val viewBoxHeight: Float = DEFAULT_VIEW_BOX,
    val strokePaths: List<String> = emptyList()
) {
    val strokeCount: Int get() = strokePaths.size

    val isEmpty: Boolean get() = strokePaths.isEmpty()

    companion object {
        const val DEFAULT_VIEW_BOX = 109f
    }
}
