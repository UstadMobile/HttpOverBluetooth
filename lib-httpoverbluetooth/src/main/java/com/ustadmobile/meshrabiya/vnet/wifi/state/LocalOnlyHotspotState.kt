package com.ustadmobile.meshrabiya.vnet.wifi.state

import android.net.wifi.WifiManager.LocalOnlyHotspotCallback
import com.ustadmobile.meshrabiya.vnet.wifi.WifiConnectConfig
import com.ustadmobile.meshrabiya.vnet.wifi.HotspotStatus

data class LocalOnlyHotspotState(
    val status: HotspotStatus = HotspotStatus.STOPPED,
    val config: WifiConnectConfig? = null,
    val errorCode: Int = 0,
) {

    companion object {

        //As per https://developer.android.com/reference/android/net/wifi/WifiManager.LocalOnlyHotspotCallback#onFailed(int)
        fun errorCodeToString(errorCode: Int) : String{
            return when(errorCode) {
                LocalOnlyHotspotCallback.ERROR_TETHERING_DISALLOWED -> "ERROR_TETHERING_DISALLOWED"
                LocalOnlyHotspotCallback.ERROR_NO_CHANNEL -> "ERROR_NO_CHANNEL"
                LocalOnlyHotspotCallback.ERROR_INCOMPATIBLE_MODE -> "ERROR_INCOMPATIBLE_MODE"
                LocalOnlyHotspotCallback.ERROR_GENERIC -> "ERROR_GENERIC"
                else -> "Unknown ERROR: $errorCode"
            }
        }

    }
}
