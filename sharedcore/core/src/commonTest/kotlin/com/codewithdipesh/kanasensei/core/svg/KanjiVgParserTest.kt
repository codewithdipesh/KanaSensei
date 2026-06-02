package com.codewithdipesh.kanasensei.core.svg

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KanjiVgParserTest {

    // Real-format KanjiVG document for あ (U+3042): 3 stroke <path>s inside StrokePaths, plus a
    // separate StrokeNumbers group whose <text> elements must NOT be picked up as strokes.
    private val hiraganaA = """
        <?xml version="1.0" encoding="UTF-8"?>
        <!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
        <svg xmlns="http://www.w3.org/2000/svg" width="109" height="109" viewBox="0 0 109 109">
        <g id="kvg:StrokePaths_03042" style="fill:none;stroke:#000000;stroke-width:3;stroke-linecap:round;stroke-linejoin:round;">
        <g id="kvg:03042" kvg:element="あ">
        	<path id="kvg:03042-s1" kvg:type="㇐" d="M36.25,28.62c2.85,0.55,5.62,0.43,8.38,0.05c8.9-1.23,24.62-4,38.12-5.05c2.43-0.19,4.84-0.32,7.25,0.12"/>
        	<path id="kvg:03042-s2" kvg:type="㇑" d="M62.25,12.5c0.97,0.97,1.27,2.25,1.27,3.79c0,12.71-0.02,55.21-0.02,69.46c0,13.5-6.31,3.25-8.31,1.5"/>
        	<path id="kvg:03042-s3" kvg:type="㇒" d="M48.75,42.38c0.36,1.5,0.1,2.91-0.57,4.62C44.5,56.25,31,73.5,17.38,82.75C5.62,90.75-0.5,77,9.25,68.5c10.93-9.53,40.5-19,57.75-10.62c13.62,6.62,9.62,28.25-5.25,28.62"/>
        </g>
        </g>
        <g id="kvg:StrokeNumbers_03042" style="font-size:8;fill:#808080">
        	<text transform="matrix(1 0 0 1 28.50 27.13)">1</text>
        	<text transform="matrix(1 0 0 1 54.50 12.13)">2</text>
        	<text transform="matrix(1 0 0 1 41.25 41.13)">3</text>
        </g>
        </svg>
    """.trimIndent()

    @Test
    fun parsesAllThreeStrokesInOrder() {
        val result = KanjiVgParser.parse(hiraganaA)

        assertEquals(3, result.strokeCount, "あ has exactly 3 strokes")
        // Order is preserved: each stroke's path starts at its own M(ove) command.
        assertTrue(result.strokePaths[0].startsWith("M36.25"), "stroke 1 should be first")
        assertTrue(result.strokePaths[1].startsWith("M62.25"), "stroke 2 should be second")
        assertTrue(result.strokePaths[2].startsWith("M48.75"), "stroke 3 should be third")
    }

    @Test
    fun ignoresStrokeNumberTextElements() {
        val result = KanjiVgParser.parse(hiraganaA)

        // The StrokeNumbers group has 3 <text> nodes ("1","2","3"); none should leak in as strokes.
        assertEquals(3, result.strokeCount)
        assertTrue(result.strokePaths.none { it.length < 5 }, "no tiny number-text fragments")
    }

    @Test
    fun readsViewBoxDimensions() {
        val result = KanjiVgParser.parse(hiraganaA)

        assertEquals(109f, result.viewBoxWidth)
        assertEquals(109f, result.viewBoxHeight)
    }

    @Test
    fun fallsBackToDefaultViewBoxWhenMissing() {
        val noViewBox = """<svg><path d="M10,10 L20,20"/></svg>"""

        val result = KanjiVgParser.parse(noViewBox)

        assertEquals(1, result.strokeCount)
        assertEquals(109f, result.viewBoxWidth)
        assertEquals(109f, result.viewBoxHeight)
    }

    @Test
    fun returnsEmptyForDocumentWithoutPaths() {
        val result = KanjiVgParser.parse("""<svg viewBox="0 0 50 50"></svg>""")

        assertTrue(result.isEmpty)
        assertEquals(0, result.strokeCount)
        // viewBox is still read even when there are no strokes.
        assertEquals(50f, result.viewBoxWidth)
    }
}
