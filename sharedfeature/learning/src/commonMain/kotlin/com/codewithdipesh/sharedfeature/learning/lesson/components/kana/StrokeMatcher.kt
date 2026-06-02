package com.codewithdipesh.sharedfeature.learning.lesson.components.kana

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathMeasure
import kotlin.math.hypot
import kotlin.math.max

/**
 * Lenient, learner-friendly handwriting matching, all in viewBox space (so tolerances are the same
 * regardless of screen size). A user stroke is accepted when it roughly follows the target stroke:
 * it starts near the start, ends near the end, mostly stays inside a tube around the stroke, and
 * actually covers most of the stroke (so a tiny scribble near the start can't pass).
 *
 * Direction falls out for free from the start/end checks. Strictness is all in [Tolerance] so it's
 * easy to hand-tune on a real device without touching the algorithm.
 */
object StrokeMatcher {

    /** All distances are in viewBox units (KanjiVG is 109x109). */
    data class Tolerance(
        val startEnd: Float = 24f,
        val tube: Float = 20f,
        val minUserInTube: Float = 0.68f,
        val minTargetCovered: Float = 0.55f,
    )

    /** Samples a target stroke into evenly spaced points along its length (in viewBox space). */
    fun sampleTarget(measure: PathMeasure, step: Float = 3f): List<Offset> {
        val length = measure.length
        if (length <= 0f) return emptyList()
        val count = max(2, (length / step).toInt())
        return (0..count).map { i -> measure.getPosition(length * i / count) }
    }

    /**
     * @param user the user's points for one stroke, already mapped into viewBox space.
     * @param target sampled target points from [sampleTarget].
     */
    fun matches(
        user: List<Offset>,
        target: List<Offset>,
        tolerance: Tolerance = Tolerance()
    ): Boolean {
        if (user.size < 2 || target.size < 2) return false

        // Start & end must land near the stroke's ends (this also enforces direction).
        if (dist(user.first(), target.first()) > tolerance.startEnd) return false
        if (dist(user.last(), target.last()) > tolerance.startEnd) return false

        // Most of the user's ink should sit inside the tube around the stroke (not wandering off).
        val userInTube = user.count { u -> target.minOf { dist(u, it) } <= tolerance.tube }
            .toFloat() / user.size
        if (userInTube < tolerance.minUserInTube) return false

        // The user should have covered most of the stroke, not just part of it.
        val targetCovered = target.count { t -> user.minOf { dist(t, it) } <= tolerance.tube }
            .toFloat() / target.size
        if (targetCovered < tolerance.minTargetCovered) return false

        return true
    }

    private fun dist(a: Offset, b: Offset): Float = hypot(a.x - b.x, a.y - b.y)
}
