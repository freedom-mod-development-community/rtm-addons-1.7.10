package keiproductfamily.rtmAddons.detectorChannel

import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.ModCommonVar
import net.minecraft.tileentity.TileEntity
import java.util.*

class RTMDetectorChannelData(val channelKey: String) {
    var signalLevel = SignalLevel.PROCEED
    var rollSignID: Byte = -1
    var iRTMDReceivers = HashSet<IRTMDetectorReceiver>()

    fun setTrainData(signalLevel: SignalLevel, rollSignID: Byte) {
        if (this.signalLevel != ModCommonVar.findTrainLevel || this.rollSignID != rollSignID) {
            this.signalLevel = signalLevel
            this.rollSignID = rollSignID
            val removes = HashSet<IRTMDetectorReceiver>()
            for (receiver in iRTMDReceivers) {
                if (receiver is TileEntity) {
                    if ((receiver as TileEntity).isInvalid) {
                        removes.add(receiver)
                        continue
                    }
                }
                receiver.onNewDetectorSignal(channelKey, signalLevel, rollSignID)
            }
            iRTMDReceivers.removeAll(removes)
            RTMDetectorChannelMaster.putCallList(channelKey, signalLevel, rollSignID)
        }
    }
}