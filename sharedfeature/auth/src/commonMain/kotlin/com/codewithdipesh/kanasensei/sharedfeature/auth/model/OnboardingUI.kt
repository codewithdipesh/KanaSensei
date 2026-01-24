package com.codewithdipesh.kanasensei.sharedfeature.auth.model

import com.codewithdipesh.kanasensei.core.model.user.MotivationSource

data class OnboardingUI(
    val motivationSource: MotivationSource? = null,
    val name : String = "",
    val japaneseName : String = "",
    val isTranslating : Boolean = false
)