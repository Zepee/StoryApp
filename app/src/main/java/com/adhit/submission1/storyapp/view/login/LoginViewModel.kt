package com.adhit.submission1.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhit.submission1.storyapp.data.ResultState
import com.adhit.submission1.storyapp.data.pref.UserModel
import com.adhit.submission1.storyapp.data.repository.Repository
import com.adhit.submission1.storyapp.data.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository) : ViewModel() {

    private val _loginResult = MutableLiveData<ResultState<LoginResponse>>()
    val loginResult: LiveData<ResultState<LoginResponse>> = _loginResult

    private val _isButtonEnabled = MutableLiveData<Boolean>()
    val isButtonEnabled: LiveData<Boolean>
        get() = _isButtonEnabled

    init {
        _isButtonEnabled.value = false
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun checkText(email: String, password: String) {
        val isButtonEnabled = isEmailValid(email) && password.length >= 8
        _isButtonEnabled.value = isButtonEnabled
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = ResultState.Loading
            val result = repository.login(email, password)
            _loginResult.value = result
        }
    }
}