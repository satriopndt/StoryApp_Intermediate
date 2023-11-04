package com.example.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.storyapp.pref.AuthToken
import com.example.storyapp.result.Result
import com.example.storyapp.ui.login.main.ViewModelFactory
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.login.main.MainActivity
import com.example.storyapp.ui.login.signup.SignupActivity
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        animationLogin()
        signIn()

        binding.textSignUp.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }


        supportActionBar?.hide()

    }

    private fun animationLogin() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titleWelcome =
            ObjectAnimator.ofFloat(binding.titleSign, View.ALPHA, 1f).setDuration(1000)
        val textSign = ObjectAnimator.ofFloat(binding.textSign, View.ALPHA, 1f).setDuration(1000)
        val emailEdit =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val passEdit =
            ObjectAnimator.ofFloat(binding.passEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val btnSignin = ObjectAnimator.ofFloat(binding.btnSignin, View.ALPHA, 1f).setDuration(1000)
        val tvSignup = ObjectAnimator.ofFloat(binding.tvSignUp, View.ALPHA, 1f).setDuration(1000)
        val textSignup =
            ObjectAnimator.ofFloat(binding.textSignUp, View.ALPHA, 1f).setDuration(1000)

        val together = AnimatorSet().apply {
            playTogether(tvSignup, textSignup)
        }

        AnimatorSet().apply {
            playSequentially(
                titleWelcome,
                textSign,
                emailEdit,
                passEdit,
                btnSignin,
                together
            )
            start()
        }
    }

    private fun signIn() {
        binding.btnSignin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passEditText.text.toString()
            setupAction(email, password)

        }
    }

    private fun setupAction(email: String, password: String) {
        viewModel.login(email, password).observe(this) { result ->
            if (result != null){
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)

                    }
                    is Result.Success -> {
                        showLoading(false)
                        signIn()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                        Toast.makeText(this, "SignIn Success", Toast.LENGTH_SHORT).show()
                        val userData = AuthToken(
                            result.data.loginResult.token,
                            result.data.loginResult.userId.toString(),
                            result.data.loginResult.name.toString(),
                            true)
//                        val sharedPref = getSharedPreferences("story", Context.MODE_PRIVATE)
//                        val editor = sharedPref.edit()
//                        editor.putString("isLogin", "login")
//                        editor.commit()
                        viewModel.saveUserSession(userData)
                        finish()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

    override fun onBackPressed() {
        exitProcess(0)
        super.onBackPressed()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}


