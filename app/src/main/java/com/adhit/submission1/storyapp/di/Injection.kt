package com.adhit.submission1.storyapp.di

import android.content.Context
import com.adhit.submission1.storyapp.data.pref.UserPreference
import com.adhit.submission1.storyapp.data.pref.dataStore
import com.adhit.submission1.storyapp.data.repository.Repository
import com.adhit.submission1.storyapp.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return Repository.getInstance(pref, apiService)
    }
}