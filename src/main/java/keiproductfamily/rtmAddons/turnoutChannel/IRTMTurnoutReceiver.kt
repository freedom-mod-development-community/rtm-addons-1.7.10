package keiproductfamily.rtmAddons.turnoutChannel

import keiproductfamily.rtmAddons.EnumTurnOutSwitch
import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection

interface IRTMTurnoutReceiver {
    fun onNewTurnoutNowSwitch(channelKey:String, nowSide: EnumTurnOutSwitch): Boolean
    fun onNewTurnoutForceSelect(channelKey:String, turnoutSelect: EnumTurnOutSyncSelection): Boolean
    fun markDirtyAndNotify()
    fun isRemote(): Boolean
}