package keiproductfamily.rtmAddons.turnoutChannel

import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.ModCommonVar
import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection

interface IRTMTurnoutReceiver {
    fun onNewTurnoutSignal(channelKey:String, turnoutSide: EnumTurnOutSyncSelection): Boolean
    fun markDirtyAndNotify()
}