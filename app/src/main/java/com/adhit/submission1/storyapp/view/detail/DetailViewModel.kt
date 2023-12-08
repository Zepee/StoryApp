package com.adhit.submission1.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.adhit.submission1.storyapp.data.ResultState
import com.adhit.submission1.storyapp.data.pref.UserModel
import com.adhit.submission1.storyapp.data.repository.Repository
import com.adhit.submission1.storyapp.data.response.DetailResponse
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: Repository) : ViewModel() {

    private val _story = MutableLiveData<DetailResponse>()
    val story: LiveData<DetailResponse> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadStoryDetail(token: String, storyId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getStoryDetails(token ,storyId)

            if (result is ResultState.Success) {
                val story = result.data
                _story.value = story
            } else if (result is ResultState.Error) {
                _errorMessage.value = result.error
            }
            _isLoading.value = false
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}