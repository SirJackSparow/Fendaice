package com.dg.fendaice.auth

data class LoginUiState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
