package keiproductfamily.rtmAddons.turnoutChannel

import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection
import java.util.HashMap
import java.util.LinkedHashMap

object RTMTurnoutChannelMaster {
    private val channelDatas = HashMap<String, RTMTurnoutChannelData>()
    fun getChannelData(channelName: String): RTMTurnoutChannelData? {
        return channelDatas[channelName]
    }

    fun reSet(reveiver: IRTMTurnoutReceiver, from: ChannelKeyPair, to: ChannelKeyPair) {
        val fromSet = setOf(from.getKey())
        val toSet = setOf(to.getKey())
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


    private val calledList = LinkedHashMap<String, EnumTurnOutSyncSelection>()

    fun putCallList(channelKey: String, turnoutSide: EnumTurnOutSyncSelection){
        calledList.remove(channelKey)
        calledList[channelKey] = turnoutSide
    }

    fun reCallList(receiver: IRTMTurnoutReceiver) {
        for ((key, value) in calledList) {
            receiver.onNewTurnoutSignal(key, value)
        }
    }
}