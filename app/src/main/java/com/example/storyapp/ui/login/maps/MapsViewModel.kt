package com.example.storyapp.ui.login.maps

import androidx.lifecycle.ViewModel
import com.example.storyapp.repository.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun getStoryLocation() = storyRepository.getStoriesWithLocation()

}