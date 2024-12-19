package com.example.asahi.presentation.fragments.server

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.asahi.R
import com.example.asahi.core.common.ServerStatus
import com.example.asahi.core.utils.viewBinding
import com.example.asahi.databinding.FragmentServerErrorBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ServerErrorFragment : Fragment(R.layout.fragment_server_error) {

    private val binding by viewBinding(FragmentServerErrorBinding::bind)

    private val viewModel: ServerStatusViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.serverStatus.collect { status -> handleServerStatus(status) }
            }
        }

        binding.btnUpdate.setOnClickListener {
            viewModel.startCheckingServerStatus()
            showSnackbar(getString(R.string.checking_server))
            viewLifecycleOwner.lifecycleScope.launch {
                delay(2000)
                if (viewModel.serverStatus.value == ServerStatus.AVAILABLE) {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun handleServerStatus(status: ServerStatus) {
        val message = when (status) {
            ServerStatus.AVAILABLE -> getString(R.string.server_ok)
            ServerStatus.UNAVAILABLE -> getString(R.string.server_error)
            ServerStatus.NO_INTERNET -> getString(R.string.no_internet_message)
        }
        if (message.isNotEmpty()) showSnackbar(message)

        val isUnavailable = status == ServerStatus.UNAVAILABLE
        binding.tvErrorMessage.isVisible = isUnavailable
        binding.btnUpdate.isVisible = isUnavailable
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

}