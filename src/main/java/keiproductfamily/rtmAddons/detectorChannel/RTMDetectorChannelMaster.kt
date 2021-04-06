package keiproductfamily.rtmAddons.detectorChannel

import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.rtmAddons.ChannelKeyPair
import java.util.HashMap
import java.util.LinkedHashMap

object RTMDetectorChannelMaster {
    private val channelDatas = HashMap<String, RTMDetectorChannelData>()
    fun getChannelData(channelName: String): RTMDetectorChannelData? {
        return channelDatas[channelName]
    }

    fun reSet(reveiver: IRTMDetectorReceiver, from: Array<ChannelKeyPair>, to: Array<ChannelKeyPair>) {
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
                channelDatas[keyname] = RTMDetectorChannelData(keyname)
            }
            channelDatas[keyname]?.irtmaReceivers?.add(reveiver)
        }
    }


    private val calledList = LinkedHashMap<String, Pair<SignalLevel, Byte>>()

    fun putCallList(channelKey: String, signalLevel: SignalLevel, rollSignID: Byte){
        calledList.remove(channelKey)
        calledList[channelKey] = Pair(signalLevel, rollSignID)
    }

    fun reCallList(receiver: IRTMDetectorReceiver) {
        for ((key, value) in calledList) {
            receiver.onNewDetectorSignal(key, value.first, value.second)
        }
    }
}