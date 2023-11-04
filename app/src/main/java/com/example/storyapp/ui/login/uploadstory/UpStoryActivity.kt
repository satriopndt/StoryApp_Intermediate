package com.example.storyapp.ui.login.uploadstory

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.result.Result
import com.example.storyapp.ui.login.main.ViewModelFactory
import com.example.storyapp.databinding.ActivityUpStoryBinding
import com.example.storyapp.ui.login.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import getImageUri
import reduceFileImage
import uriToFile

class UpStoryActivity : AppCompatActivity() {

    private val viewModel by viewModels<UpStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityUpStoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentImageUri: Uri? = null
    private var locationSwitcher: Boolean = false
    private var currentLocation: Location? = null



    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { isGranted: Map<String, Boolean> ->
            val permissionCamera = isGranted[Manifest.permission.CAMERA] ?: false
            val permissionLocation =
                isGranted[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            when {
                permissionCamera -> Toast.makeText(
                    this,
                    "Permission request granted",
                    Toast.LENGTH_LONG
                ).show()

                permissionLocation -> Toast.makeText(
                    this,
                    "Permission request denied",
                    Toast.LENGTH_LONG
                ).show()

                !permissionCamera -> Toast.makeText(
                    this,
                    "Permission request granted",
                    Toast.LENGTH_LONG
                ).show()

                !permissionLocation -> Toast.makeText(
                    this,
                    "Permission request denied",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(
                arrayOf(
                    REQUIRED_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }


        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
        binding.galleryButton.setOnClickListener { startGallery() }
        val switchLoc = binding.switchLocation

        fusedLocationClient = LocationServices.getFusedLocationProviderClient((this))

        switchLoc.setOnCheckedChangeListener { _, isChecked ->
            locationSwitcher = isChecked
            if (isChecked){
                requestUpdateLoc()
            } else{
                binding.tvLocation.text = null
            }
        }
        animationUp()
    }

    private fun requestUpdateLoc() {
        if (locationSwitcher) {
            if (allPermissionsGranted()) {
                try {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            location?.let {
                                currentLocation = it
                                val latitude = it.latitude
                                val longitude = it.longitude
                                Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Location", "Error getting location: ${e.message}")
                        }
                } catch (e: SecurityException) {
                    Log.e("Location", "Location permission denied: ${e.message}")
                }
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun animationUp() {
        ObjectAnimator.ofFloat(binding.ImageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val cameraBtn =
            ObjectAnimator.ofFloat(binding.cameraButton, View.ALPHA, 1f).setDuration(1000)
        val galleryBtn =
            ObjectAnimator.ofFloat(binding.galleryButton, View.ALPHA, 1f).setDuration(1000)
        val descLayout =
            ObjectAnimator.ofFloat(binding.tfDesc, View.ALPHA, 1f).setDuration(1000)
        val uploadBtn =
            ObjectAnimator.ofFloat(binding.uploadButton, View.ALPHA, 1f).setDuration(1000)

        val together = AnimatorSet().apply {
            playTogether(cameraBtn, galleryBtn)
        }

        AnimatorSet().apply {
            playSequentially(
                together,
                descLayout,
                uploadBtn

            )
            start()
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edtDesc.text.toString()
            var lon: Double? = null
            var lat: Double? = null

            if (locationSwitcher) {
                lon = currentLocation?.longitude
                lat = currentLocation?.latitude
            }
            showLoading(true)

            viewModel.uploadImage(imageFile, description, lat, lon).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showToast(result.data.message)
                            showLoading(false)
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }

                        is Result.Error -> {
                            showToast(result.error)
                            showLoading(false)
                        }
                    }
                }
            }
            showLoading(true)
        } ?: showToast(getString(R.string.empty_file_image))
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ImageView.setImageURI(it)
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}