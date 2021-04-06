package keiproductfamily.rtmAddons.detectorChannel

import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.ModCommonVar

interface IRTMDetectorReceiver {
    fun onNewDetectorSignal(channelKey:String, signalLevel: SignalLevel, rollSignID:Byte): Boolean
    fun markDirtyAndNotify()
}