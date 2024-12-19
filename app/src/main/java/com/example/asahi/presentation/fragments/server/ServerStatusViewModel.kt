package com.example.asahi.presentation.fragments.server

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.asahi.core.common.Constants.BASE_URL
import com.example.asahi.core.common.ServerStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class ServerStatusViewModel @Inject constructor(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {

    private val connectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _serverStatus = MutableStateFlow(
        savedStateHandle.get<String>("serverStatus")?.let { statusString ->
            when (statusString) {
                "AVAILABLE" -> ServerStatus.AVAILABLE
                "UNAVAILABLE" -> ServerStatus.UNAVAILABLE
                "NO_INTERNET" -> ServerStatus.NO_INTERNET
                else -> ServerStatus.AVAILABLE
            }
        } ?: ServerStatus.AVAILABLE
    )
    val serverStatus: MutableStateFlow<ServerStatus> = _serverStatus

    private var serverStatusCheckJob: Job? = null
    private val checkIntervalMillis = 3000L
    private var connectTimeoutMillis = 3000
    private var isCheckingEnabled = true

    var wasServerUnavailable: Boolean = savedStateHandle["wasServerUnavailable"] ?: false
        private set

    init {
        startCheckingServerStatus()
        registerNetworkCallback()
    }

    private fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(
            request,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) = updateNetworkStatus(true)
                override fun onLost(network: Network) = updateNetworkStatus(false)
            })

        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        val isConnectedNow =
            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        _isConnected.value = isConnectedNow
    }

    private fun updateNetworkStatus(isConnected: Boolean) {
        _isConnected.value = isConnected
    }

    fun startCheckingServerStatus() {
        if (_serverStatus.value == ServerStatus.AVAILABLE) return
        stopCheckingServerStatus()

        serverStatusCheckJob = viewModelScope.launch {
            while (isActive && isCheckingEnabled) {
                val status = checkServerStatus()
                _serverStatus.value = status
                delay(checkIntervalMillis)
            }
        }
    }

    private fun stopCheckingServerStatus() {
        serverStatusCheckJob?.cancel()
        serverStatusCheckJob = null
    }

    private suspend fun checkServerStatus(): ServerStatus {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(BASE_URL)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "HEAD"
                    connectTimeout = connectTimeoutMillis
                    connect()
                }
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    wasServerUnavailable = false
                    ServerStatus.AVAILABLE
                } else {
                    wasServerUnavailable = true
                    ServerStatus.UNAVAILABLE
                }
            } catch (e: IOException) {
                wasServerUnavailable = true
                ServerStatus.UNAVAILABLE
            } catch (e: Exception) {
                wasServerUnavailable = true
                ServerStatus.UNAVAILABLE
            }
        }
    }

    fun resetServerUnavailableFlag() {
        wasServerUnavailable = false
    }

    override fun onCleared() {
        savedStateHandle["serverStatus"] = _serverStatus.value.name
        savedStateHandle["wasServerUnavailable"] = wasServerUnavailable
        super.onCleared()
    }

}