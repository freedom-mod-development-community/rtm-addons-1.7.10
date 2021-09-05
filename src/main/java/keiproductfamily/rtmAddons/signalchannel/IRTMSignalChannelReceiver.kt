package keiproductfamily.rtmAddons.signalchannel

import jp.ngt.rtm.electric.SignalLevel

interface IRTMSignalChannelReceiver  {
    fun onNewLevelSignal(channelKey: String, signalLevel: SignalLevel): Boolean
    fun isRemote(): Boolean
}