package keiproductfamily.rtmAddons.signalchannel

import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.rtmAddons.ChannelKeyPair

object RTMSignalChannelMaster {
    private val channelDatas = HashMap<String, RTMSignalChannelData>()
    fun getOrMakeChannelData(channelName: String): RTMSignalChannelData? {
        if (channelDatas[channelName] == null) {
            channelDatas[channelName] = RTMSignalChannelData(channelName)
        }
        return channelDatas[channelName]
    }

    fun makeChannel(channelKey: ChannelKeyPair) {
        if (channelDatas[channelKey.keyString] == null) {
            channelDatas[channelKey.keyString] = RTMSignalChannelData(channelKey.keyString)
        }
    }

    fun reSet(receiver: IRTMSignalChannelReceiver, fromSet: Set<String>, toSet: Set<String>) {
        if (!receiver.isRemote()) {
            val remove = HashSet(fromSet)
            remove.removeAll(toSet)
            val add = HashSet(toSet)
            add.removeAll(fromSet)

            for (keyName in remove) {
                channelDatas[keyName]?.iRTMSReceivers?.remove(receiver)
            }

            for (keyName in add) {
                if (channelDatas[keyName] == null) {
                    channelDatas[keyName] = RTMSignalChannelData(keyName)
                }
                channelDatas[keyName]?.iRTMSReceivers?.add(receiver)
            }
        }
    }

    private val calledList = LinkedHashMap<String, SignalLevel>()

    fun putCallList(channelName: String, signalLevel: SignalLevel) {
        if (channelName != "" && channelName != "-") {
            calledList.remove(channelName)
            calledList[channelName] = signalLevel
        }
    }

    fun reCallList(receiver: IRTMSignalChannelReceiver) {
        for ((channelName, signalLevel) in calledList) {
            receiver.onNewLevelSignal(channelName, signalLevel)
        }
    }
}