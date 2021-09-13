package keiproductfamily.rtmAddons.turnoutChannel

import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.EnumTurnOutSwitch
import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection
import java.util.*
import kotlin.collections.HashSet
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

object RTMTurnoutChannelMaster {
    private val channelDATAs = HashMap<String, RTMTurnoutChannelData>()
    fun getChannelData(channelName: String): RTMTurnoutChannelData? {
        return channelDATAs[channelName]
    }

    fun reSet(receiver: IRTMTurnoutReceiver, from: ChannelKeyPair?, to: ChannelKeyPair?) {
        if (!receiver.isRemote()) {
            val fromSet = if (from != null) {
                setOf(from.keyString)
            } else {
                HashSet()
            }
            val toSet = if (to != null) {
                setOf(to.keyString)
            } else {
                HashSet()
            }
            val remove = HashSet(fromSet)
            remove.removeAll(toSet)
            val add = HashSet(toSet)
            add.removeAll(fromSet)

            for (keyName in remove) {
                channelDATAs[keyName]?.iRTMTReceivers?.remove(receiver)
            }

            for (keyName in add) {
                if (channelDATAs[keyName] == null) {
                    channelDATAs[keyName] = RTMTurnoutChannelData(keyName)
                }
                channelDATAs[keyName]?.iRTMTReceivers?.add(receiver)
            }
        }
    }


    private val forceSelectCalledList = LinkedHashMap<String, EnumTurnOutSyncSelection>()
    private val nowSwitchCalledList = LinkedHashMap<String, EnumTurnOutSwitch>()

    fun putForceSelectCallList(channelKey: String, forceSelect: EnumTurnOutSyncSelection) {
        forceSelectCalledList.remove(channelKey)
        forceSelectCalledList[channelKey] = forceSelect
    }

    fun putNowSelectCallList(channelKey: String, turnoutSide: EnumTurnOutSwitch) {
        nowSwitchCalledList.remove(channelKey)
        nowSwitchCalledList[channelKey] = turnoutSide
    }

    fun reCallList(receiver: IRTMTurnoutReceiver) {
        for ((key, value) in forceSelectCalledList) {
            receiver.onNewTurnoutForceSelect(key, value)
        }
        for ((key, value) in nowSwitchCalledList) {
            receiver.onNewTurnoutNowSwitch(key, value)
        }
    }
}