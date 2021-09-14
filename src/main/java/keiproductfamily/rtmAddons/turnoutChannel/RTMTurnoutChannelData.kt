package keiproductfamily.rtmAddons.turnoutChannel

import jp.ngt.rtm.entity.train.util.FormationManager
import keiproductfamily.rtmAddons.EnumTurnOutSwitch
import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection
import keiproductfamily.rtmAddons.atc2.ATC2Core
import keiproductfamily.rtmAddons.receiverBlock.receiverTurnout.ReceiverTurnoutTile
import net.minecraft.tileentity.TileEntity
import java.util.*

class RTMTurnoutChannelData(val channelKey: String) {
    var turnoutNowSide: EnumTurnOutSwitch? = null
    var turnoutForce: EnumTurnOutSyncSelection? = null
    var iRTMTReceivers = HashSet<IRTMTurnoutReceiver>()

    fun setTurnOutNowSwitchData(turnoutSide: EnumTurnOutSwitch) {
        if (this.turnoutNowSide == null || this.turnoutNowSide != turnoutSide) {
            this.turnoutNowSide = turnoutSide
            val removes = HashSet<IRTMTurnoutReceiver>()
            for (receiver in iRTMTReceivers) {
                if (receiver is TileEntity) {
                    if ((receiver as TileEntity).isInvalid) {
                        removes.add(receiver)
                        continue
                    }
                } else if (receiver is ATC2Core.ATCReceiver) {
                    if (FormationManager.getInstance().getFormation(receiver.formationID) == null) {
                        removes.add(receiver)
                        continue
                    }
                }
                receiver.onNewTurnoutNowSwitch(channelKey, turnoutSide)
            }
            iRTMTReceivers.removeAll(removes)
            RTMTurnoutChannelMaster.putNowSelectCallList(channelKey, turnoutSide)
        }
    }

    fun setTurnOutForceData(turnoutForce: EnumTurnOutSyncSelection) {
        if (this.turnoutForce != turnoutForce) {
            this.turnoutForce = turnoutForce
            val removes = HashSet<IRTMTurnoutReceiver>()
            for (receiver in iRTMTReceivers) {
                if (receiver is TileEntity) {
                    if ((receiver as TileEntity).isInvalid) {
                        removes.add(receiver)
                        continue
                    }
                }
                receiver.onNewTurnoutForceSelect(channelKey, turnoutForce)
            }
            iRTMTReceivers.removeAll(removes)

            val turnoutTile = iRTMTReceivers.last { it is ReceiverTurnoutTile }
            (turnoutTile as? ReceiverTurnoutTile)?.syncNowSwitchData()

            RTMTurnoutChannelMaster.putForceSelectCallList(channelKey, turnoutForce)
        }
    }
}