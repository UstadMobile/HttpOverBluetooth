package com.ustadmobile.meshrabiya.vnet.wifi

import android.net.MacAddress
import android.net.wifi.SoftApConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.LocalOnlyHotspotCallback
import androidx.annotation.RequiresApi
import java.util.concurrent.Executor
import kotlin.random.Random


@RequiresApi(28)
fun generateRandomMacAddress(): MacAddress {
    val byteArray = ByteArray(6)
    Random.nextBytes(byteArray)
    return MacAddress.fromBytes(byteArray)
}

/**
 * Start a LocalOnlyHotspot using a configuration to set the band, ssid, bssid, etc. This
 * a hidden API and works only with Android 13+. The API requires one of three permissions:
 * NETWORK_SETTINGS, NETWORK_SETUP_WIZARD, or NEARBY_WIFI_DEVICES. The first two are reserved for
 * system apps. NEARBY_WIFI_DEVICES permission is a runtime permission that is available from
 * Android 12 and up.
 *
 * This calls the hidden function startLocalOnlyHotspot(SoftApConfig, Executor, callback):
 * https://cs.android.com/android/platform/superproject/+/android-13.0.0_r1:packages/modules/Wifi/framework/java/android/net/wifi/WifiManager.java;l=4764
 *
 * Unfortunately Android 12 does not accept NEARBY_WIFI_DEVICES permission - if the platform or target
 * is less than SDK 33, it will fail as per the implementation of WifiServiceImpl on Android 12:
 * https://cs.android.com/android/platform/superproject/+/android-12.0.0_r1:packages/modules/Wifi/service/java/com/android/server/wifi/WifiServiceImpl.java;l=2045;bpv=1;bpt=0
 *
 * From Android 13 the check is changed to accept NEARBY_WIFI_DEVICES permission (provided that the
 * device and target SDK is Android 13 or higher) when a configuration is provided:
 * https://cs.android.com/android/platform/superproject/+/android-13.0.0_r1:packages/modules/Wifi/service/java/com/android/server/wifi/WifiServiceImpl.java;l=2472
 *
 * ===It is NOT possible to workaround the random MAC problem on Android 11/12.===
 *
 * Avenues explored:
 *
 * Option 1: use WifiManager.setWifiApConfiguration to set the default tethering ap configuration,
 * and set mac randomization to NONE. This is blocked by a requirement for OVERRIDE_WIFI_CONFIG
 * permission (which is not available to third party app). See WifiServiceImpl#setSoftApConfiguration
 * https://cs.android.com/android/platform/superproject/+/android-13.0.0_r54:packages/modules/Wifi/service/java/com/android/server/wifi/WifiServiceImpl.java;l=2727
 *
 * Option 2: try to disable Wifi Mac randomization so that the generateLocalOnlyHotspotConfig will
 * set randomization to NONE. Won't work: AP config util uses a system resource
 * R.bool.config_wifi_ap_mac_randomization_supported as per:
 * https://cs.android.com/android/platform/superproject/+/android-12.0.0_r1:packages/modules/Wifi/service/java/com/android/server/wifi/util/ApConfigUtil.java;l=699
 * The only way to override that would be a "Runtime Resourc Overlay" RRO, which can only be installed
 * with system level permission.
 *
 * Option 3: Try using global settings as per
 * https://stackoverflow.com/questions/61748195/android-10-how-to-disable-randomized-mac-address-in-source-code
 * Won't work on Android 11/12 - Mac Randomization is now a per-network setting, not a global setting.
 * Might work on Android 9 as per
 *  https://github.com/elastic/AndroidSDKMirror-28/blob/master/com/android/server/wifi/WifiStateMachine.java
 *
 * Option 4: Use adb command / network settings app to change the default tethering, but the
 * settings app itself does not change this:
 * https://cs.android.com/android/platform/superproject/+/master:packages/apps/Settings/src/com/android/settings/wifi/tether/WifiTetherSettings.java;l=231
 * adb shell cmd wifi also does not support changing this.
 */
@RequiresApi(33)
fun WifiManager.startLocalOnlyHotspotWithConfig(
    config: SoftApConfiguration,
    executor: Executor?,
    callback: LocalOnlyHotspotCallback
) {
    WifiManager::class.java.getMethod(
        "startLocalOnlyHotspot", SoftApConfiguration::class.java, Executor::class.java,
        LocalOnlyHotspotCallback::class.java,
    ).invoke(this, config, executor, callback)
}


/**
 * It is NOT possible to workaround the random MAC problem on Android 11/12.
 *
 * Option 1: use WifiManager.setWifiApConfiguration to set the default tethering ap configuration,
 * and set mac randomization to NONE. This is blocked by a requirement for OVERRIDE_WIFI_CONFIG
 * permission (which is not available to third party app). See WifiServiceImpl#setSoftApConfiguration
  https://cs.android.com/android/platform/superproject/+/android-13.0.0_r54:packages/modules/Wifi/service/java/com/android/server/wifi/WifiServiceImpl.java;l=2727
 *
 * Option 2: try to disable Wifi Mac randomization so that the generateLocalOnlyHotspotConfig will
 * set randomization to NONE. Won't work: AP config util uses a system resource
 * R.bool.config_wifi_ap_mac_randomization_supported as per:
 * https://cs.android.com/android/platform/superproject/+/android-12.0.0_r1:packages/modules/Wifi/service/java/com/android/server/wifi/util/ApConfigUtil.java;l=699
 * The only way to override that would be a "Runtime Resourc Overlay" RRO, which can only be installed
 * with system level permission.
 *
 * Option 3: Try using global settings as per
 * https://stackoverflow.com/questions/61748195/android-10-how-to-disable-randomized-mac-address-in-source-code
 * Won't work on Android 11/12 - Mac Randomization is now a per-network setting, not a global setting.
 * Might work on Android 9 as per
 *  https://github.com/elastic/AndroidSDKMirror-28/blob/master/com/android/server/wifi/WifiStateMachine.java
 *
 * Option 4: Use adb command / network settings app to change the default tethering, but the
 * settings app itself does not change this:
 * https://cs.android.com/android/platform/superproject/+/master:packages/apps/Settings/src/com/android/settings/wifi/tether/WifiTetherSettings.java;l=231
 *
 */
