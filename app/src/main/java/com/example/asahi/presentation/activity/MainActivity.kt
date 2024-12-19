package com.example.asahi.presentation.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.asahi.R
import com.example.asahi.core.common.Constants.APP_ACTIVITY
import com.example.asahi.core.common.ServerStatus
import com.example.asahi.core.utils.Extensions.gone
import com.example.asahi.core.utils.Extensions.visible
import com.example.asahi.databinding.ActivityMainBinding
import com.example.asahi.presentation.fragments.server.ServerStatusViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<ServerStatusViewModel>()

    private var textToSpeech: TextToSpeech? = null

    private lateinit var navController: NavController

    private val fragmentsWithBlueStatusBar = setOf(
        R.id.splashFragment,
    )

    private val fragmentsWithBlackToolbar = setOf(
        R.id.simpleSearchFragment,
    )

    private val fragmentsWithAbout = setOf(
        R.id.filterFragment,
    )

    private val fragmentsWithBottomNav = setOf(
        R.id.searchHomeFragment,
        R.id.favoriteFragment,
        R.id.myToursFragment,
        R.id.profileFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        APP_ACTIVITY = this

        if (resources.configuration.smallestScreenWidthDp < 600) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        textToSpeech = TextToSpeech(this, this)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavigation
        navController = findNavController(R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateStatusBarColor(destination.id)

            val isFragmentWithBottomNav = fragmentsWithBottomNav.contains(destination.id)
            navView.visibility = if (isFragmentWithBottomNav) View.VISIBLE else View.GONE

            if (isFragmentWithBottomNav) {
                showBottomNavigationView(navView)
                supportActionBar?.show()
            } else {
                hideBottomNavigationView(navView)
                supportActionBar?.hide()
            }
        }
        navView.setupWithNavController(navController)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            controller?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.serverStatus.collect { status ->
                    if (status == ServerStatus.UNAVAILABLE && viewModel.wasServerUnavailable) {
                        Log.d("Navigation", "Navigating to serverErrorFragment")
                        delay(3000)
                        if (viewModel.serverStatus.value == ServerStatus.UNAVAILABLE) {
                            if (navController.currentDestination?.id != R.id.serverErrorFragment &&
                                navController.currentDestination?.id != R.id.authFragment &&
                                navController.currentDestination?.id != R.id.splashFragment
                            ) {
                                navController.navigate(R.id.serverErrorFragment)
                                viewModel.resetServerUnavailableFlag()
                            }
                        }
                    }
                }
            }
        }

        viewModel.startCheckingServerStatus()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isConnected.collect { isConnected ->
                    if (isConnected) {
                        binding.networkWarning.gone()
                    } else {
                        binding.networkWarning.visible()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatusBarColor()
    }

    private fun updateStatusBarColor(currentDestinationId: Int? = null) {
        val destinationId = currentDestinationId ?: navController.currentDestination?.id
        if (destinationId != null) {
            val isFragmentWithBlueStatusBar = fragmentsWithBlueStatusBar.contains(destinationId)
            val isFragmentWithAbout = fragmentsWithAbout.contains(destinationId)
            val isFragmentWithBlackToolbar = fragmentsWithBlackToolbar.contains(destinationId)

            when {
                isFragmentWithBlueStatusBar -> {
                    window.statusBarColor = ContextCompat.getColor(this, R.color.white)
                }

                isFragmentWithAbout -> {
                    window.statusBarColor = ContextCompat.getColor(this, R.color.white)
                }

                isFragmentWithBlackToolbar -> {
                    window.statusBarColor = ContextCompat.getColor(this, R.color.black)
                }

                else -> {
                    window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
                }
            }
        }
    }

    private fun hideBottomNavigationView(view: BottomNavigationView) {
        if (view.visibility == View.GONE) return
        view.clearAnimation()
        view.animate().translationY(view.height.toFloat()).setDuration(300)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                view.visibility = View.GONE
            }
    }

    private fun showBottomNavigationView(view: BottomNavigationView) {
        if (view.visibility == View.VISIBLE) return
        view.visibility = View.VISIBLE
        view.clearAnimation()
        view.animate().translationY(0f).setDuration(300)
            .setInterpolator(DecelerateInterpolator())
    }

    @SuppressLint("SwitchIntDef")
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {}
            Configuration.ORIENTATION_PORTRAIT -> {}
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.setLanguage(Locale.getDefault())?.let { result ->
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported")
                }
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }

    fun speakText(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

}