package com.codewithdipesh.sharedfeature.learning.lesson.components.kana

import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StrokeMatcherTest {

    // A horizontal target stroke from (10,50) to (90,50) in viewBox space, sampled every 8 units.
    private val target: List<Offset> = (10..90 step 8).map { Offset(it.toFloat(), 50f) }

    private fun line(fromX: Int, toX: Int, y: Float, step: Int = 4): List<Offset> {
        val range = if (toX >= fromX) (fromX..toX step step) else (fromX downTo toX step step)
        return range.map { Offset(it.toFloat(), y) }
    }

    @Test
    fun acceptsAStrokeThatFollowsTheTarget() {
        val user = line(11, 89, 51f) // basically on top of the target, slight jitter in y
        assertTrue(StrokeMatcher.matches(user, target))
    }

    @Test
    fun rejectsAReversedStroke() {
        // Right-to-left: ends are swapped, so start/end proximity fails (enforces direction).
        val user = line(89, 11, 50f)
        assertFalse(StrokeMatcher.matches(user, target))
    }

    @Test
    fun rejectsAStrokeThatOnlyCoversPartOfTheTarget() {
        // Only the first third — fails the "covered most of the stroke" check.
        val user = line(11, 35, 50f)
        assertFalse(StrokeMatcher.matches(user, target))
    }

    @Test
    fun rejectsAStrokeThatWandersOffTheTube() {
        // Correct endpoints but bulges far away in the middle.
        val user = listOf(
            Offset(10f, 50f),
            Offset(50f, 100f), // way off the line
            Offset(90f, 50f),
        )
        assertFalse(StrokeMatcher.matches(user, target))
    }

    @Test
    fun rejectsTooFewPoints() {
        assertFalse(StrokeMatcher.matches(listOf(Offset(10f, 50f)), target))
    }
}
