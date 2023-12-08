package com.adhit.submission1.storyapp.view.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.adhit.submission1.storyapp.data.response.DetailResponse
import com.adhit.submission1.storyapp.databinding.ActivityDetailBinding
import com.adhit.submission1.storyapp.view.ViewModelFactory
import com.adhit.submission1.storyapp.view.welcome.WelcomeActivity
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    private val detailViewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(application)
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                val storyId = intent.getStringExtra("ID") ?: ""

                if (storyId.isNotEmpty()) {
                    detailViewModel.loadStoryDetail(user.token, storyId)

                    detailViewModel.story.observe(this, Observer<DetailResponse?> { detailUserResponse ->
                        detailUserResponse?.let {
                            populateStoryData(it)
                        }
                    })

                    detailViewModel.isLoading.observe(this, Observer<Boolean> { isLoading ->
                        showLoading(isLoading)
                    })

                    detailViewModel.isLoading.observe(this) { isLoading ->
                        if (isLoading) {
                            binding.progressBar.visibility = View.VISIBLE
                        } else {
                            binding.progressBar.visibility = View.GONE
                        }
                    }

                    detailViewModel.errorMessage.observe(this, Observer { errorMessage ->
                        errorMessage?.let {
                            showToast(it)
                        }
                    })
                }
            }
        }


    }

    private fun populateStoryData(story: DetailResponse) {
        binding.apply {
            Glide.with(binding.root.context)
                .load(story.story?.photoUrl)
                .override(350,550)
                .into(binding.imageView)
            binding.titleTextView.text = story.story?.name
            binding.descriptionTextView.text = story.story?.description
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}