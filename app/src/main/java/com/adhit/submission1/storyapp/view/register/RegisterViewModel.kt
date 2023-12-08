package com.adhit.submission1.storyapp.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhit.submission1.storyapp.data.ResultState
import com.adhit.submission1.storyapp.data.repository.Repository
import com.adhit.submission1.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: Repository) : ViewModel() {

    private val _registrationResult = MutableLiveData<ResultState<RegisterResponse>>()
    val registrationResult: LiveData<ResultState<RegisterResponse>> = _registrationResult

    private val _isButtonEnabled = MutableLiveData<Boolean>()

    val isButtonEnabled: LiveData<Boolean>
        get() = _isButtonEnabled

    init {
        _isButtonEnabled.value = false
    }

    fun checkText(name: String, email: String, password: String) {
        val isButtonEnabled = name.isNotEmpty() && isEmailValid(email) && password.length >= 8
        _isButtonEnabled.value = isButtonEnabled
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registrationResult.value = ResultState.Loading
            val result = repository.register(name, email, password)
            _registrationResult.value = result
        }
    }
}