package keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights

import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.Unpooled
import jp.ngt.rtm.electric.IProvideElectricity
import jp.ngt.rtm.electric.SignalLevel
import jp.ngt.rtm.world.RTMChunkManager
import keiproductfamily.ModCommonVar
import keiproductfamily.ModKEIProductFamily
import keiproductfamily.network.PacketHandler
import keiproductfamily.normal.TileNormal
import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.ForcedTurnoutSelection
import keiproductfamily.rtmAddons.IRTMAReceiver
import keiproductfamily.rtmAddons.RTMAChannelMaster
import keiproductfamily.rtmAddons.scWirelessAdvance.TileEntitySC_WirelessAdvance
import net.minecraft.nbt.NBTTagCompound

class ReceiverTrafficLightTile : TileNormal(), IRTMAReceiver, IProvideElectricity {
    var channelKeys = Array(6) { ChannelKeyPair("", 0) }
    var forcedTurnoutSlection = ForcedTurnoutSelection.Auto
    var isUpdate: Boolean = false

    fun setChunnelKeys(channelKeys: Array<ChannelKeyPair>) {
        if (this.channelKeys.size == channelKeys.size) {
            RTMAChannelMaster.reSet(this, this.channelKeys, channelKeys)
            this.channelKeys = channelKeys
            this.markDirty()
            isUpdate = true
        }
    }

    override fun validate() {
        super.validate()
        if (!this.worldObj.isRemote) {
            RTMAChannelMaster.reCallList(this)
        }
    }

    override fun onNewSignal(channelKey: String, signalLevel: SignalLevel, rollSignID: Byte): Boolean {
        if (signalLevel == ModCommonVar.findTrainLevel) {
            for ((index, pair) in channelKeys.withIndex()) {
                if (channelKey == pair.getKey()) {
                    _electricity = index + 1
                    return true
                }
            }
        }
        return false
    }

    override fun markDirtyAndNotify() {
        if (this.worldObj != null) {
            this.markDirty()
            this.worldObj.markAndNotifyBlock(
                xCoord,
                yCoord,
                zCoord,
                worldObj.getChunkFromChunkCoords(xCoord shr 4, zCoord shr 4),
                ModCommonVar.receiverTrafficLightBlock,
                ModCommonVar.receiverTrafficLightBlock,
                3
            )
        }
    }


    private var _electricity: Int = 0
    override fun getElectricity(): Int {
        return _electricity
    }

    override fun setElectricity(x: Int, y: Int, z: Int, level: Int) {
        //TODO 用いるか判断
        //_electricity = level
        val i = 0
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        val cnt = nbt.getInteger("channelKeyscnt")
        val buf = Unpooled.buffer()
        buf.writeBytes(nbt.getByteArray("channelKeysBuf"))
        val cks = Array<ChannelKeyPair>(cnt) {
            ChannelKeyPair.readFromTyteBuf(buf)
        }
        setChunnelKeys(cks)
        this._electricity = nbt.getInteger("electricity")
    }

    override fun writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)
        nbt.setInteger("channelKeyscnt", channelKeys.size)

        val buf = Unpooled.buffer()
        for (pair in channelKeys) {
            pair.writeToByteBuf(buf)
        }
        nbt.setByteArray("channelKeysBuf", buf.array())
        nbt.setInteger("electricity", _electricity)
    }
}