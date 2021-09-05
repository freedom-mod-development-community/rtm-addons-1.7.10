package keiproductfamily.rtmAddons.signalchannel

import jp.ngt.rtm.electric.SignalLevel
import net.minecraft.tileentity.TileEntity

class RTMSignalChannelData(val channelKey: String) {
    var signalLevel: SignalLevel = SignalLevel.STOP
    var iRTMSReceivers = HashSet<IRTMSignalChannelReceiver>()

    fun setSignalLevelNowData(signalLevel: SignalLevel) {
        if (this.signalLevel != signalLevel) {
            this.signalLevel = signalLevel
            val removes = HashSet<IRTMSignalChannelReceiver>()
            for (receiver in iRTMSReceivers) {
                if((receiver as? TileEntity)?.isInvalid == true){
                    removes.add(receiver)
                    continue
                }
                receiver.onNewLevelSignal(channelKey, signalLevel)
            }
            iRTMSReceivers.removeAll(removes)
            RTMSignalChannelMaster.putCallList(channelKey, signalLevel)
        }
    }
}