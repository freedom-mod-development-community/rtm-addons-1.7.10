package keiproductfamily.rtmAddons.detectorChannel

import jp.ngt.rtm.electric.SignalLevel

interface IRTMDetectorReceiver {
    fun onNewDetectorSignal(channelKey: String, signalLevel: SignalLevel, rollSignID: Byte, formationID: Long, direction: EnumDirection): Boolean
    fun markDirtyAndNotify()
    fun isRemote(): Boolean
}