package com.codewithdipesh.kanasensei.core.model.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonPage(
    val autoPlay: Boolean = false,
    val badge: String = "",
    val content: String = "",
    val createdAt: String = "",
    val hintText: String = "",
    val id: String = "",
    val kanaId: String = "",
    val order: Int = 0,
    val showGuide: Boolean = false,
    val title: String = "",
    val type: LessonPageType = LessonPageType.LISTEN, //firebase auto convert to enum if the name is in bold letter
    val quizConfig : QuizConfig? = null,
    val updatedAt: String = ""
){
    fun toQuizDetails() : QuizDetail {
        val quizDetail = QuizDetail(
            question = this.quizConfig?.metadata?.title ?:  "Issue fetching the quiz",
            options = this.quizConfig?.legacy?.options ?: emptyList(),
            correctOption = this.quizConfig?.legacy?.correctOption ?: 1,
            kanaId = this.kanaId,
            questionType = this.quizConfig?.source?.modality ?: QuizQuestionType.romaji,
            answerType = this.quizConfig?.responseMode ?: QuizResponseMode.mcq
        )
        return quizDetail
    }
}


enum class LessonPageType(val value: String) {
    LISTEN("LISTEN"),
    INFO("INFO"),
    STROKE("STROKE"),
    WRITE("WRITE"),
    QUIZ("QUIZ")
}

/**
 * Universal quiz configuration used by both CMS and Android.
 *
 * Every quiz page contains a QuizConfig.
 *
 * Examples:
 * - Audio -> Kana MCQ
 * - Kana -> Romaji MCQ
 * - Romaji -> Kana Typing
 * - Kana Drawing Practice
 * - Word Composition
 */
@Serializable
data class QuizConfig(

    val version: Int = 1,

    /**
     * High level quiz category.
     *
     * recognition = identify something
     * production  = create/type/draw something
     * composition = combine multiple parts
     * legacy      = old MCQ implementation
     */
    val kind: QuizKind = QuizKind.recognition,

    /**
     * Internal template identifier.
     *
     * Examples:
     * - single-answer
     * - parts-to-output
     */
    val template: String = "",

    /**
     * Quiz scope.
     *
     * Examples:
     * - kana
     * - word
     * - combo
     * - custom
     */
    val scope: String = "",

    /**
     * What the user sees/hears first.
     */
    val source: QuizSource = QuizSource(),

    /**
     * How the user responds.
     *
     * Examples:
     * - MCQ
     * - Typing
     * - Drawing
     * - Ordering
     */
    val responseMode: QuizResponseMode = QuizResponseMode.mcq,

    /**
     * Expected answer configuration.
     */
    val answer: QuizAnswer = QuizAnswer(),

    /**
     * Future auto-generated MCQ support.
     *
     * Currently not heavily used.
     */
    val distractors: DistractorConfig = DistractorConfig(),

    /**
     * Used by composition quizzes.
     */
    val composition: CompositionConfig = CompositionConfig(),

    /**
     * UI metadata.
     */
    val metadata: QuizMetadata = QuizMetadata(),

    /**
     * Legacy MCQ data.
     *
     * Current MCQ implementation still uses this.
     * Do not remove until MCQ is fully migrated.
     */
    val legacy: LegacyQuiz = LegacyQuiz()
)

@Serializable
enum class QuizKind {
    @SerialName("recognition")
    recognition,
    @SerialName("production")
    production,
    @SerialName("composition")
    composition,
    @SerialName("legacy")
    legacy
}

@Serializable
enum class QuizResponseMode {
    @SerialName("mcq")
    mcq,
    @SerialName("typing")
    typing,
    @SerialName("drawing")
    drawing,
    @SerialName("ordering")
    ordering,
    @SerialName("matching")
    matching
}

@Serializable
enum class QuizQuestionType {
    @SerialName("audio")
    audio,
    @SerialName("kana")
    kana,
    @SerialName("romaji")
    romaji,
    @SerialName("svg")
    svg,
    @SerialName("word")
    word
}

@Serializable
data class QuizSource(
    //audio/svg, word like this
    val modality: QuizQuestionType = QuizQuestionType.romaji,
    /**
     * Related Firestore ids.
     *
     * Usually contains kana ids.
     */
    val refIds: List<String> = emptyList(),
    val value: String = ""
)
@Serializable
data class QuizAnswer(
    val modality: String = "",
    val refIds: List<String> = emptyList(),
    val value: String = "",
    val acceptedValues: List<String> = emptyList()
)
@Serializable
data class DistractorConfig(
    val strategy: String = "",
    val count: Int = 0
)
@Serializable
data class CompositionConfig(
    val parts: List<String> = emptyList(),
    val output: String = ""
)
@Serializable
data class QuizMetadata(
    val title: String = "",
    val hint: String = "",
    val tags: List<String> = emptyList()
)
@Serializable
data class LegacyQuiz(
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctOption: Int = 0
)


//easier quiz details model
@Serializable
data class QuizDetail(
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctOption: Int = 0 , //between 1-4
    val kanaId: String? = null,
    val questionType : QuizQuestionType = QuizQuestionType.romaji, //what to show
    val answerType : QuizResponseMode = QuizResponseMode.mcq, //what to expect
)
