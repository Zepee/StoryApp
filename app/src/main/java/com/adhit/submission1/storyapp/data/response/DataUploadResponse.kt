package com.adhit.submission1.storyapp.data.response

import com.google.gson.annotations.SerializedName

data class DataUploadResponse(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String
)