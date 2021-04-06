package keiproductfamily.rtmAddons.detectorChannel

import jp.ngt.rtm.electric.SignalLevel
import net.minecraft.tileentity.TileEntity
import java.util.HashSet

class RTMDetectorChannelData(val channelKey: String) {
    var signalLevel = SignalLevel.PROCEED
    var rollSignID: Byte = -1
//    var forcedTurnoutSelection = EnumForcedSignalMode.Auto
    var irtmaReceivers = HashSet<IRTMDetectorReceiver>()

    fun setTrainData(signalLevel: SignalLevel, rollSignID: Byte) {
        if (this.signalLevel != signalLevel || this.rollSignID != rollSignID) {
            this.signalLevel = signalLevel
            this.rollSignID = rollSignID
            val removes = HashSet<IRTMDetectorReceiver>()
            for (receiver in irtmaReceivers) {
                if (receiver is TileEntity) {
                    if ((receiver as TileEntity).isInvalid) {
                        removes.add(receiver)
                    }
                }
                receiver.onNewDetectorSignal(channelKey, signalLevel, rollSignID)
                receiver.markDirtyAndNotify()
            }
            irtmaReceivers.removeAll(removes)
            RTMDetectorChannelMaster.putCallList(channelKey, signalLevel, rollSignID)
        }
    }

//    fun setForceSelect(selection: EnumForcedSignalMode) {
//        if (forcedTurnoutSelection != selection) {
//            forcedTurnoutSelection = selection
//            val removes = HashSet<IRTMDetectorReceiver>()
//            for (receiver in irtmaReceivers) {
//                if (receiver is TileEntity) {
//                    if ((receiver as TileEntity).isInvalid) {
//                        removes.add(receiver)
//                    }
//                }
//                receiver.onNewSignal(channelKey, signalLevel, rollSignID)
//                receiver.markDirtyAndNotify()
//            }
//            irtmaReceivers.removeAll(removes)
//        }
//    }
}