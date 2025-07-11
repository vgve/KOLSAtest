package com.vgve.core.utils.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

object ConnectionExtensions {

    val Fragment.isNotConnected: Boolean
        get() = !requireContext().isConnectionAvailable()

    val AppCompatActivity.isNotConnected: Boolean
        get() = !this.isConnectionAvailable()

    val Context.isNotConnected: Boolean
        get() = !this.isConnectionAvailable()

    val Fragment.isConnected: Boolean
        get() = requireContext().isConnectionAvailable()

    val AppCompatActivity.isConnected: Boolean
        get() = this.isConnectionAvailable()

    val Context.isConnected: Boolean
        get() = this.isConnectionAvailable()

    val Context.isConnectedOrNull: Boolean?
        get() = if (this.isConnectionAvailable()) true else null

    private fun Context.isConnectionAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun Fragment.networkConnection(
        onAvailable: () -> Unit = {},
        onLost: () -> Unit
    ) = requireContext().checkNetworkConnection(onAvailable, onLost)

    fun AppCompatActivity.networkConnection(
        onAvailable: () -> Unit = {},
        onLost: () -> Unit
    ) = this.checkNetworkConnection(onAvailable, onLost)

    private fun Context.checkNetworkConnection(
        onAvailable: () -> Unit,
        onLost: () -> Unit
    ) {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                onAvailable()
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                onLost()
            }
        }
        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }
}

