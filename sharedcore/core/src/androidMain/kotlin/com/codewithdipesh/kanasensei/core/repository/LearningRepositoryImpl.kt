package com.codewithdipesh.kanasensei.core.repository

import com.codewithdipesh.kanasensei.core.firestore.FirestorePaths
import com.codewithdipesh.kanasensei.core.model.content.Character
import com.codewithdipesh.kanasensei.core.model.content.Lesson
import com.codewithdipesh.kanasensei.core.model.content.LessonPage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LearningRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val httpClient: HttpClient
): LearningRepository {

    // Session cache
    private val svgCache = mutableMapOf<String, String>()

    override suspend fun getLessonPages(
        lessonId: String 
    ): List<LessonPage> =  withContext(Dispatchers.IO){

        firestore
            .collection(FirestorePaths.LESSONS)
            .document(lessonId)
            .collection(FirestorePaths.LESSONPAGES)
            .orderBy(
                FirestorePaths.LessonPageFields.ORDER,
                Query.Direction.ASCENDING
            )
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                runCatching { doc.toObject(LessonPage::class.java) }.getOrNull()
            }

    }

    override suspend fun getLesson(
        lessonId: String
    ): Lesson? = withContext(Dispatchers.IO){
        firestore
            .collection(FirestorePaths.LESSONS)
            .document(lessonId)
            .get()
            .await()
            .toObject(Lesson::class.java)
    }

    override suspend fun getKana(
        kanaId: String
    ): Character? = withContext(Dispatchers.IO){
        if (kanaId.isBlank()) return@withContext null
        firestore
            .collection(FirestorePaths.CHARACTERS)
             .document(kanaId)
            .get()
            .await()
            .toObject(Character::class.java)
    }

    override suspend fun getKanaSvg(
        svgUrl: String
    ): String? = withContext(Dispatchers.IO) {
        if (svgUrl.isBlank()) return@withContext null
        svgCache[svgUrl]?.let { return@withContext it }

        runCatching {
            httpClient.get(svgUrl).bodyAsText()
        }.getOrNull()?.also { svg ->
            svgCache[svgUrl] = svg
        }
    }

}