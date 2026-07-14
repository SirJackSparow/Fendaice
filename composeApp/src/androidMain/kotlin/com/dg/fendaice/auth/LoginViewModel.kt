package com.dg.fendaice.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    fun onSignInResult(isSuccess: Boolean, errorMessage: String?) {
        _state.update { it.copy(
            isSignInSuccessful = isSuccess,
            signInError = errorMessage
        ) }
    }

    fun resetState() {
        _state.update { LoginUiState() }
    }
}
