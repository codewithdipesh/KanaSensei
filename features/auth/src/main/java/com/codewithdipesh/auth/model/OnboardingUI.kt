package com.codewithdipesh.auth.model

import com.codewithdipesh.data.model.user.MotivationSource

data class OnboardingUI(
    val motivationSource: MotivationSource? = null,
    val name : String = "",
    val japaneseName : String = "",

    val selectedPage : Int = 0
)