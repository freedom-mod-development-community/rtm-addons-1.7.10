package keiproductfamily.rtmAddons

import jp.ngt.rtm.electric.SignalLevel
import net.minecraft.tileentity.TileEntity
import java.util.HashSet

class RTMAChannelData(val channelKey: String) {
    var signalLevel = SignalLevel.PROCEED
    var rollSignID: Byte = -1
    var forcedTurnoutSelection = ForcedTurnoutSelection.Auto
    var irtmaReceivers = HashSet<IRTMAReceiver>()
    fun setTrainData(signalLevel: SignalLevel, rollSignID: Byte) {
        if (this.signalLevel != signalLevel || this.rollSignID != rollSignID) {
            this.signalLevel = signalLevel
            this.rollSignID = rollSignID
            val removes = HashSet<IRTMAReceiver>()
            for (receiver in irtmaReceivers) {
                if (receiver is TileEntity) {
                    if ((receiver as TileEntity).isInvalid) {
                        removes.add(receiver)
                    }
                }
                receiver.onNewSignal(channelKey, signalLevel, rollSignID)
                receiver.markDirtyAndNotify()
            }
            irtmaReceivers.removeAll(removes)
            RTMAChannelMaster.putCallList(channelKey, signalLevel, rollSignID)
        }
    }

    fun setForceSelect(selection: ForcedTurnoutSelection) {
        if (forcedTurnoutSelection != selection) {
            forcedTurnoutSelection = selection
            val removes = HashSet<IRTMAReceiver>()
            for (receiver in irtmaReceivers) {
                if (receiver is TileEntity) {
                    if ((receiver as TileEntity).isInvalid) {
                        removes.add(receiver)
                    }
                }
                receiver.onNewSignal(channelKey, signalLevel, rollSignID)
                receiver.markDirtyAndNotify()
            }
            irtmaReceivers.removeAll(removes)
        }
    }
}