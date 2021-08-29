package keiproductfamily.rtmAddons.detectorChannel

import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.rtmAddons.ChannelKeyPair
import scala.util.control.Exception
import java.util.*
import kotlin.collections.HashSet
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

object RTMDetectorChannelMaster {
    private val channelDatas = HashMap<String, RTMDetectorChannelData>()
    fun getChannelData(channelName: String): RTMDetectorChannelData? {
        return channelDatas[channelName]
    }

    fun makeChannel(channelKey: ChannelKeyPair) {
        if (channelDatas[channelKey.keyString] == null) {
            channelDatas[channelKey.keyString] = RTMDetectorChannelData(channelKey.keyString)
        }
    }

    private fun reSet(receiver: IRTMDetectorReceiver, fromSet: Set<String>, toSet: Set<String>) {
        val remove = HashSet(fromSet)
        remove.removeAll(toSet)
        val add = HashSet(toSet)
        add.removeAll(fromSet)

        for (keyname in remove) {
            channelDatas[keyname]?.iRTMDReceivers?.remove(receiver)
        }

        for (keyname in add) {
            if (channelDatas[keyname] == null) {
                channelDatas[keyname] = RTMDetectorChannelData(keyname)
            }
            channelDatas[keyname]?.iRTMDReceivers?.add(receiver)
        }
    }

    fun reSet(receiver: IRTMDetectorReceiver, from: Array<ChannelKeyPair>, to: Array<ChannelKeyPair>) {
        if (!receiver.isRemote()) {
            val fromSet = Array<String>(from.size) { i -> from[i].keyString }.toSet()
            val toSet = Array<String>(to.size) { i -> to[i].keyString }.toSet()
            reSet(receiver, fromSet, toSet)
        }
    }

    fun reSet(receiver: IRTMDetectorReceiver, from: ChannelKeyPair, to: ChannelKeyPair) {
        if (!receiver.isRemote()) {
            val fromSet = setOf(from.keyString)
            val toSet = setOf(to.keyString)
            reSet(receiver, fromSet, toSet)
        }
    }


    private val calledList = LinkedHashMap<String, DataSet>()

    fun putCallList(channelKey: String, signalLevel: SignalLevel, rollSignID: Byte, formationID: Long, direction: EnumDirection) {
        calledList.remove(channelKey)
        calledList[channelKey] = DataSet(signalLevel, rollSignID, formationID, direction)
    }

    fun reCallList(receiver: IRTMDetectorReceiver) {
        for ((key, value) in calledList) {
            receiver.onNewDetectorSignal(key, value.signalLevel, value.rollSignID, value.formationID, value.direction)
        }
    }

    data class DataSet(val signalLevel: SignalLevel, val rollSignID: Byte, val formationID: Long, val direction: EnumDirection)
}