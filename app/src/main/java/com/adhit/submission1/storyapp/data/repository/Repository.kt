package com.adhit.submission1.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.adhit.submission1.storyapp.data.ResultState
import com.adhit.submission1.storyapp.data.paging.StoryPagingSource
import com.adhit.submission1.storyapp.data.pref.UserModel
import com.adhit.submission1.storyapp.data.pref.UserPreference
import com.adhit.submission1.storyapp.data.response.DataUploadResponse
import com.adhit.submission1.storyapp.data.response.DetailResponse
import com.adhit.submission1.storyapp.data.response.ErrorResponse
import com.adhit.submission1.storyapp.data.response.ListStoryItem
import com.adhit.submission1.storyapp.data.response.LoginResponse
import com.adhit.submission1.storyapp.data.response.RegisterResponse
import com.adhit.submission1.storyapp.data.response.StoryResponse
import com.adhit.submission1.storyapp.data.retrofit.ApiConfig
import com.elapp.storyapp.data.remote.auth.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class Repository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): ResultState<RegisterResponse> {
        return try {
            val response = apiService.register(name, email, password)
            ResultState.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            ResultState.Error(errorResponse.message ?: "Registration failed")
        } catch (e: Exception) {
            ResultState.Error("Registration failed")
        }
    }

    suspend fun login(email: String, password: String): ResultState<LoginResponse> {
        return try {
            val response = apiService.login(email, password)
            ResultState.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            ResultState.Error(errorResponse.message ?: "Login failed")
        } catch (e: Exception) {
            ResultState.Error("Login failed")
        }
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(token, apiService)
            }
        ).liveData
    }

    suspend fun getStoryDetails(token: String, id: String): ResultState<DetailResponse> {
        return try {
            val response = apiService.getDetailStory("Bearer $token", id)
            Log.d("DetailActivity", "API Response: $response")
            ResultState.Success(response)
        } catch (e: HttpException) {
            Log.e("DetailActivity", "API Error: ${e.message}")
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            ResultState.Error(errorResponse.message ?: "Load Detail Story failed")
        } catch (e: Exception) {
            Log.e("DetailActivity", "API Exception: ${e.message}")
            ResultState.Error("Load Detail Story failed")
        }
    }

    fun getStoryWithLocation(token: String): LiveData<ResultState<StoryResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.getStoriesWithLocation("Bearer $token", 1)
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            Log.e("ListStoryViewModel", "getStoryWithLocation: ${e.message.toString()}")
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun uploadImage(token: String, imageFile: File, description: String) = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            userPreference.getSession()
            val user = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val successResponse =
                apiService.uploadImage("Bearer $token", multipartBody, requestBody)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, DataUploadResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
        }
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference, apiService)
            }.also { instance = it }
    }
}