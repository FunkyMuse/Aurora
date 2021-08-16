package com.funkymuse.aurora.internetdetector.connectiontype

/**
 * Created by funkymuse on 8/16/21 to long live and prosper !
 */
sealed interface ConnectionType {
    object Idle : ConnectionType
    object NoConnection : ConnectionType
    object NoInfo : ConnectionType
    object WiFi : ConnectionType
    object VPN : ConnectionType
    object MobileData : ConnectionType
    object Ethernet : ConnectionType
}