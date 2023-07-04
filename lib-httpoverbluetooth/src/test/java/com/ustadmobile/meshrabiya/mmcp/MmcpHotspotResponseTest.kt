package com.ustadmobile.meshrabiya.mmcp

import com.ustadmobile.meshrabiya.vnet.randomApipaAddr
import com.ustadmobile.meshrabiya.vnet.wifi.WifiConnectConfig
import com.ustadmobile.meshrabiya.vnet.wifi.HotspotType
import com.ustadmobile.meshrabiya.vnet.wifi.LocalHotspotResponse
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class MmcpHotspotResponseTest {

    @Test
    fun givenHotspotResponse_whenConvertedToFromBytes_thenShouldBeEqual() {
        val responseMessage = MmcpHotspotResponse(
            messageId = 42,
            result = LocalHotspotResponse(
                responseToMessageId = Random.nextInt(),
                errorCode = 0,
                config = WifiConnectConfig(
                    nodeVirtualAddr = randomApipaAddr(),
                    ssid = "test",
                    passphrase = "secret",
                    port = 8042,
                    hotspotType = HotspotType.LOCALONLY_HOTSPOT,
                ),
                redirectAddr = 0
            )
        )

        val responseBytes = responseMessage.toBytes()
        val responseDeserialized = MmcpHotspotResponse.fromBytes(responseBytes) as MmcpHotspotResponse
        Assert.assertEquals(responseMessage.messageId, responseDeserialized.messageId)
        Assert.assertEquals(responseMessage.result, responseDeserialized.result)
    }

}