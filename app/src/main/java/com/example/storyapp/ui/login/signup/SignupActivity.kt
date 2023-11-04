package com.example.storyapp.ui.login.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.storyapp.result.Result
import com.example.storyapp.ui.login.main.ViewModelFactory
import com.example.storyapp.databinding.ActivitySignupBinding
import com.example.storyapp.ui.login.LoginActivity

class SignupActivity : AppCompatActivity() {

    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()

        animationSignup()
    }

    private fun animationSignup() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titleWelcome = ObjectAnimator.ofFloat(binding.titlePageSignup, View.ALPHA, 1f).setDuration(1000)
        val nameLayout = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val emailLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val passLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val btnSignup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(1000)

        AnimatorSet().apply {
            playSequentially(
                titleWelcome,
                nameLayout,
                emailLayout,
                passLayout,
                btnSignup)
            start()
        }
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val username = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.register(username, email, password).observe(this){ result ->
                when(result){
                    is Result.Loading -> {
                        showLoading(true)

                    }
                    is Result.Success -> {
                        showLoading(true)
                        viewModel.getSession()
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this, "Sign Up Failed", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}