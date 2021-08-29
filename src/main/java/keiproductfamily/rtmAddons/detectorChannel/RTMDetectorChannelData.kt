package keiproductfamily.rtmAddons.detectorChannel

import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.ModCommonVar
import net.minecraft.tileentity.TileEntity
import java.util.*

class RTMDetectorChannelData(val channelKey: String) {
    var signalLevel = SignalLevel.PROCEED
    var rollSignID: Byte = -1
    var formationID: Long = -1
    var direction: EnumDirection = EnumDirection.Null

    var iRTMDReceivers = HashSet<IRTMDetectorReceiver>()

    fun setTrainData(signalLevel: SignalLevel, rollSignID: Byte, formationID: Long, direction: EnumDirection) {
        if (this.signalLevel != signalLevel || this.rollSignID != rollSignID || this.formationID != formationID || this.direction != direction) {
            this.signalLevel = signalLevel
            this.rollSignID = rollSignID
            this.formationID = formationID
            this.direction = direction
            val removes = HashSet<IRTMDetectorReceiver>()
            for (receiver in iRTMDReceivers) {
                if (receiver is TileEntity) {
                    if ((receiver as TileEntity).isInvalid) {
                        removes.add(receiver)
                        continue
                    }
                }
                receiver.onNewDetectorSignal(channelKey, signalLevel, rollSignID, formationID, direction)
            }
            iRTMDReceivers.removeAll(removes)
            RTMDetectorChannelMaster.putCallList(channelKey, signalLevel, rollSignID, formationID, direction)
        }
    }
}