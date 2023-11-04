package com.example.storyapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.pref.AuthToken
import com.example.storyapp.repository.StoryRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun login(email: String, password: String) = storyRepository.login(email, password)

    fun saveUserSession(authToken: AuthToken) {
        viewModelScope.launch {
            storyRepository.saveSession(authToken)
        }
    }

}