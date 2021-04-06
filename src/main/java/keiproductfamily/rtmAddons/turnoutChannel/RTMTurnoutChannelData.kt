package keiproductfamily.rtmAddons.turnoutChannel

import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection
import net.minecraft.tileentity.TileEntity
import java.util.HashSet

class RTMTurnoutChannelData(val channelKey: String) {
    var turnoutSide = EnumTurnOutSyncSelection.OFF
    var irtmaReceivers = HashSet<IRTMTurnoutReceiver>()

    fun setTurnoutData(turnoutSide: EnumTurnOutSyncSelection) {
        if (this.turnoutSide != turnoutSide) {
            this.turnoutSide = turnoutSide
            val removes = HashSet<IRTMTurnoutReceiver>()
            for (receiver in irtmaReceivers) {
                if (receiver is TileEntity) {
                    if ((receiver as TileEntity).isInvalid) {
                        removes.add(receiver)
                    }
                }
                receiver.onNewTurnoutSignal(channelKey, turnoutSide)
                receiver.markDirtyAndNotify()
            }
            irtmaReceivers.removeAll(removes)
            RTMTurnoutChannelMaster.putCallList(channelKey, turnoutSide)
        }
    }
}