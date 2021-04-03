package keiproductfamily.rtmAddons

import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.ModCommonVar

interface IRTMAReceiver {
    fun onNewSignal(channelKey:String, signalLevel: SignalLevel, rollSignID:Byte): Boolean
    fun markDirtyAndNotify()
}