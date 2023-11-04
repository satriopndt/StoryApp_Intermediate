package com.example.storyapp.ui.login.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.ui.login.maps.MapsActivity
import com.example.storyapp.R
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.ui.login.uploadstory.UpStoryActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    private var storyAdapter = StoryAdapter()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.map_page -> {
               startActivity(Intent(this, MapsActivity::class.java))
                true
            }

            R.id.setting_Page ->{
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }


            R.id.logout_page -> {
                viewModel.logout()
//                val intent = Intent(this, SplashScreen::class.java)
//                intent.flags =
//                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
                finish()
                true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAdd.setOnClickListener {
            val fabAddIntent = Intent(this@MainActivity, UpStoryActivity::class.java)
            startActivity(fabAddIntent)
        }

        supportActionBar?.show()

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        val item = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(item)

        binding.rvStory.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                storyAdapter.retry()
            }
        )

        binding.swiperefresh.setOnRefreshListener {
            storyAdapter.refresh()
            scrollToItem(0)
            Toast.makeText(this@MainActivity, "Refresh", Toast.LENGTH_SHORT).show()
        }


        viewModel.getSession().observe(this){ session ->
            if ( !session.isLogin){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.rvStory.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                storyAdapter.retry()
            }
        )

        viewModel.listStory.observe(this){story ->
            storyAdapter.submitData(lifecycle, story)
            showLoading(false)
            binding.swiperefresh.isRefreshing = false
        }


    }

    private fun scrollToItem(index: Int) {
        val layoutManager = binding.rvStory.layoutManager as LinearLayoutManager
        layoutManager.scrollToPositionWithOffset(index, 0)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

