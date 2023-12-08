package com.adhit.submission1.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhit.submission1.storyapp.R
import com.adhit.submission1.storyapp.databinding.ActivityMainBinding
import com.adhit.submission1.storyapp.view.ViewModelFactory
import com.adhit.submission1.storyapp.view.adapter.ListStoryAdapter
import com.adhit.submission1.storyapp.view.adapter.LoadingStateAdapter
import com.adhit.submission1.storyapp.view.maps.MapsActivity
import com.adhit.submission1.storyapp.view.upload.UploadActivity
import com.adhit.submission1.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        setupView()

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, UploadActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                viewModel.logout()
            }
            R.id.action_map -> {
                val intent = Intent(this@MainActivity, MapsActivity:: class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupView() {

        val adapter = ListStoryAdapter()
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
        viewModel.getSession().observe(this) { user ->
            viewModel.loadStories(user.token).observe(this) { stories ->
                adapter.submitData(lifecycle, stories)
                Log.d("MainActivity", "Data loaded: $stories ")
            }
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                showToast(it)
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



}