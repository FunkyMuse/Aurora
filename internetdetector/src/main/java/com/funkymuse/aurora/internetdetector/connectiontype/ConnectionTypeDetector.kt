package com.funkymuse.aurora.internetdetector.connectiontype

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by funkymuse on 8/16/21 to long live and prosper !
 */
@Singleton
class ConnectionTypeDetector @Inject constructor(@ApplicationContext private val context: Context) {

    private val connectivityManager get() = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    @SuppressLint("MissingPermission")
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE])
    val state = callbackFlow {
        val networkCallback = NetworkCallback(this, connectivityManager)
        connectivityManager?.registerDefaultNetworkCallback(networkCallback)
        awaitClose {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        }
    }

    private class NetworkCallback(
        private val producerScope: ProducerScope<ConnectionType>,
        private val connectivityManager: ConnectivityManager?
    ) : ConnectivityManager.NetworkCallback() {

        init {
            produceNetworkState()
        }

        override fun onAvailable(network: Network) {
            produceNetworkState()
        }

        private fun produceNetworkState() {
            producerScope.trySend(getConnectionType())
        }

        override fun onLost(network: Network) {
            producerScope.trySend(ConnectionType.NoConnection)
        }

        @SuppressLint("MissingPermission")
        private fun getConnectionType(): ConnectionType {
            var result: ConnectionType = ConnectionType.NoInfo
            connectivityManager?.run {
                val networkCapabilities = getNetworkCapabilities(activeNetwork) ?: return ConnectionType.NoConnection
                networkCapabilities.run {
                    when {
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> result = ConnectionType.VPN
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> result = ConnectionType.WiFi
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> result = ConnectionType.MobileData
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->result = ConnectionType.Ethernet
                    }
                }
            }
            return result
        }
    }



}
