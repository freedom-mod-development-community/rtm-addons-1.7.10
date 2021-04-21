package keiproductfamily.rtmAddons.turnoutChannel

import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.EnumTurnOutSwitch
import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection
import keiproductfamily.rtmAddons.detectorChannel.RTMDetectorChannelData
import keiproductfamily.rtmAddons.detectorChannel.RTMDetectorChannelMaster
import java.util.HashMap
import java.util.LinkedHashMap

object RTMTurnoutChannelMaster {
    private val channelDatas = HashMap<String, RTMTurnoutChannelData>()
    fun getChannelData(channelName: String): RTMTurnoutChannelData? {
        return channelDatas[channelName]
    }

    fun reSet(reveiver: IRTMTurnoutReceiver, from: ChannelKeyPair, to: ChannelKeyPair) {
        val fromSet = setOf(from.keyString)
        val toSet = setOf(to.keyString)
        val remove = HashSet(fromSet)
        remove.removeAll(toSet)
        val add = HashSet(toSet)
        add.removeAll(fromSet)

        for (keyname in remove) {
            channelDatas[keyname]?.irtmaReceivers?.remove(reveiver)
        }

        for (keyname in add) {
            if(channelDatas[keyname] == null){
                channelDatas[keyname] = RTMTurnoutChannelData(keyname)
            }
            channelDatas[keyname]?.irtmaReceivers?.add(reveiver)
        }
    }


    private val forceSelectCalledList = LinkedHashMap<String, EnumTurnOutSyncSelection>()
    private val nowSwitchCalledList = LinkedHashMap<String, EnumTurnOutSwitch>()

    fun putForceSelectCallList(channelKey: String, forceSelect: EnumTurnOutSyncSelection){
        forceSelectCalledList.remove(channelKey)
        forceSelectCalledList[channelKey] = forceSelect
    }
    fun putNowSelectCallList(channelKey: String, turnoutSide: EnumTurnOutSwitch){
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