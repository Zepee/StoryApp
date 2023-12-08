package com.adhit.submission1.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.adhit.submission1.storyapp.data.pref.UserModel
import com.adhit.submission1.storyapp.data.repository.Repository

class MapsViewModel(private val repository: Repository) : ViewModel() {

    fun loadStoriesWithLocation(token: String) = repository.getStoryWithLocation(token)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}