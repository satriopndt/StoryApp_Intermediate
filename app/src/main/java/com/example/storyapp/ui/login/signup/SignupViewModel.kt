package com.example.storyapp.ui.login.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.repository.StoryRepository
import kotlinx.coroutines.launch

class SignupViewModel(private val storyRepository: StoryRepository): ViewModel() {

    fun register(username: String, email: String, password: String) = storyRepository.register(username, email, password)

    fun getSession() {
        viewModelScope.launch {
            storyRepository.getSession()
        }
    }
}