package keiproductfamily.rtmAddons

import jp.ngt.rtm.electric.SignalLevel
import java.util.HashMap
import java.util.LinkedHashMap

object RTMAChannelMaster {
    private val channelDatas = HashMap<String, RTMAChannelData>()
    fun getChannelData(channelName: String): RTMAChannelData? {
        return channelDatas[channelName]
    }

    fun reSet(reveiver: IRTMAReceiver, from: Array<ChannelKeyPair>, to: Array<ChannelKeyPair>) {
        val fromSet = Array<String>(from.size) { i -> from[i].getKey() }.toSet()
        val toSet = Array<String>(to.size) { i -> to[i].getKey() }.toSet()
        val remove = HashSet(fromSet)
        remove.removeAll(toSet)
        val add = HashSet(toSet)
        add.removeAll(fromSet)

        for (keyname in remove) {
            channelDatas[keyname]?.irtmaReceivers?.remove(reveiver)
        }

        for (keyname in add) {
            if(channelDatas[keyname] == null){
                channelDatas[keyname] = RTMAChannelData(keyname)
            }
            channelDatas[keyname]?.irtmaReceivers?.add(reveiver)
        }
    }


    private val calledList = LinkedHashMap<String, Pair<SignalLevel, Byte>>()

    fun putCallList(channelKey: String, signalLevel: SignalLevel, rollSignID: Byte){
        calledList.remove(channelKey)
        calledList[channelKey] = Pair(signalLevel, rollSignID)
    }

    fun reCallList(receiver: IRTMAReceiver) {
        for ((key, value) in calledList) {
            receiver.onNewSignal(key, value.first, value.second)
        }
    }
}