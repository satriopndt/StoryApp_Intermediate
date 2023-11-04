package com.example.storyapp.ui.login.uploadstory

import androidx.lifecycle.ViewModel
import com.example.storyapp.repository.StoryRepository
import java.io.File

class UpStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun uploadImage(imageFile: File, description: String, lat: Double?, lon:Double?) =
        storyRepository.uploadImage(imageFile, description, lat, lon)

}