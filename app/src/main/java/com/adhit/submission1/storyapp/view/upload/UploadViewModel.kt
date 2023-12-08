package com.adhit.submission1.storyapp.view.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.adhit.submission1.storyapp.data.pref.UserModel
import com.adhit.submission1.storyapp.data.repository.Repository
import java.io.File

class UploadViewModel(private val repository: Repository): ViewModel() {
    fun uploadImage(token: String, file: File, description: String) = repository.uploadImage(token, file, description)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}