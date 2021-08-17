package keiproductfamily.rtmAddons.turnoutSelector

import keiproductfamily.ModCommonVar
import keiproductfamily.getChannelKeyPair
import keiproductfamily.normal.TileNormal
import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection
import keiproductfamily.rtmAddons.turnoutChannel.RTMTurnoutChannelMaster
import keiproductfamily.setChannelKeyPair
import net.minecraft.nbt.NBTTagCompound

class TurnoutSelectorTile : TileNormal() {
    /**
     * 動かす対象の分岐名称
     */
    var turnoutChannelKeyPair = ChannelKeyPair("", "")


    /**
     * 選択している強制分岐選択モード
     */
    var turnOutSelection = EnumTurnOutSyncSelection.OFF

    var isUpdate = false

    fun nextTurnOutSelection() {
        turnOutSelection = turnOutSelection.getNext()
        RTMTurnoutChannelMaster.getChannelData(turnoutChannelKeyPair.keyString)?.setTurnOutForceData(turnOutSelection)
        markDirtyAndNotify()
    }

    fun setTurnoutChunnelKey(channelKey: ChannelKeyPair) {
        this.turnoutChannelKeyPair = channelKey
        markDirtyAndNotify()
        isUpdate = true
    }

    fun markDirtyAndNotify() {
        if (this.worldObj != null) {
            this.markDirty()
            this.worldObj.markAndNotifyBlock(
                xCoord,
                yCoord,
                zCoord,
                worldObj.getChunkFromChunkCoords(xCoord shr 4, zCoord shr 4),
                ModCommonVar.receiverTurnoutBlock,
                ModCommonVar.receiverTurnoutBlock,
                3
            )
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.turnoutChannelKeyPair = nbt.getChannelKeyPair("turnoutChannelKeyPair")
        this.turnOutSelection = EnumTurnOutSyncSelection.getType(nbt.getInteger("turnOutSelection"))
    }

    override fun writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)
        nbt.setChannelKeyPair("turnoutChannelKeyPair", turnoutChannelKeyPair)
        nbt.setInteger("turnOutSelection", turnOutSelection.id)
    }
}