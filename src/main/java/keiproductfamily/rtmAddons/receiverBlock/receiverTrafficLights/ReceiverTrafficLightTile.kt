package keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights

import jp.ngt.rtm.electric.IProvideElectricity
import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.*
import keiproductfamily.normal.TileNormal
import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.EnumForcedSignalMode
import keiproductfamily.rtmAddons.EnumTurnOutSwitch
import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection
import keiproductfamily.rtmAddons.detectorChannel.EnumDirection
import keiproductfamily.rtmAddons.detectorChannel.IRTMDetectorReceiver
import keiproductfamily.rtmAddons.detectorChannel.RTMDetectorChannelMaster
import keiproductfamily.rtmAddons.turnoutChannel.IRTMTurnoutReceiver
import keiproductfamily.rtmAddons.turnoutChannel.RTMTurnoutChannelMaster
import net.minecraft.nbt.NBTTagCompound

open class ReceiverTrafficLightTile : TileNormal(), IRTMDetectorReceiver, IRTMTurnoutReceiver, IProvideElectricity {
    open var detectorChannelKeys = Array(6) { ChannelKeyPair("", "") }
        set(channelKeys: Array<ChannelKeyPair>) {
            if (this.detectorChannelKeys.size == channelKeys.size) {
                RTMDetectorChannelMaster.reSet(this, this.detectorChannelKeys, channelKeys)
                field = channelKeys
                isUpdate = true
            }
        }

    var forcedSignalSelection = EnumForcedSignalMode.Auto
    var turnOutSyncSelection = EnumTurnOutSyncSelection.OFF
    var turnOutChannelKeyPair = ChannelKeyPair("", "")
        set(channelKey: ChannelKeyPair) {
            RTMTurnoutChannelMaster.reSet(this, this.turnOutChannelKeyPair, channelKey)
            field = channelKey
            this.markDirtyAndNotify()
            isUpdate = true
        }

    var forceSelectSignal = SignalLevel.STOP

    var nowTurnOutSwitch = EnumTurnOutSyncSelection.OFF
    var isUpdate: Boolean = false


    fun setDatas(
        forcedSignalSlection: EnumForcedSignalMode = EnumForcedSignalMode.Auto,
        turnOutSyncSelection: EnumTurnOutSyncSelection = EnumTurnOutSyncSelection.OFF,
        forceSelectSignal: SignalLevel = SignalLevel.STOP
    ) {
        this.forcedSignalSelection = forcedSignalSlection
        this.turnOutSyncSelection = turnOutSyncSelection
        this.forceSelectSignal = forceSelectSignal
        this.markDirtyAndNotify()
        isUpdate = true
    }

    private var initialized = false
    override fun updateEntity() {
        super.updateEntity()
        if(!initialized){
            if (!this.worldObj.isRemote) {
                RTMDetectorChannelMaster.reCallList(this)
            }
            initialized = true
        }
    }

    override fun onNewDetectorSignal(
        channelKey: String,
        signalLevel: SignalLevel,
        rollSignID: Byte,
        formationID: Long,
        direction: EnumDirection
    ): Boolean {
        if (signalLevel == ModCommonVar.findTrainLevel) {
            for ((index, pair) in detectorChannelKeys.withIndex()) {
                if (channelKey == pair.keyString) {
                    electricityAuto = index + 1
                    return true
                }
            }
        }
        return false
    }

    override fun onNewTurnoutNowSwitch(channelKey: String, nowSide: EnumTurnOutSwitch): Boolean {
        if (channelKey == turnOutChannelKeyPair.keyString) {
            val newValue = nowSide.toEnumTurnOutSyncSelection()
            if (nowTurnOutSwitch != newValue) {
                nowTurnOutSwitch = newValue
                changeNotify()
            }
            return true
        }
        return false
    }

    override fun onNewTurnoutForceSelect(channelKey: String, turnoutSelect: EnumTurnOutSyncSelection): Boolean {
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

    override fun isRemote(): Boolean {
        return worldObj?.isRemote ?: false
    }

    fun changeNotify() {
        this.worldObj.notifyBlockOfNeighborChange(xCoord, yCoord, zCoord, this.blockType)
    }


    protected var electricityAuto: Int = 0
    override fun getElectricity(): Int {
        return if (forcedSignalSelection.force) {
            forceSelectSignal.level
        } else if (turnOutSyncSelection == EnumTurnOutSyncSelection.OFF) {
            electricityAuto
        } else {
            if (nowTurnOutSwitch == EnumTurnOutSyncSelection.OFF || turnOutSyncSelection == nowTurnOutSwitch) {
                electricityAuto
            } else {
                SignalLevel.STOP.level
            }
        }
    }

    override fun setElectricity(x: Int, y: Int, z: Int, level: Int) {}

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        detectorChannelKeys = nbt.getChannelKeyPairArray("detectorChannelKeys")
        this.forcedSignalSelection = EnumForcedSignalMode.getType(nbt.getInteger("forcedSignalSlection"))
        this.turnOutSyncSelection = EnumTurnOutSyncSelection.getType(nbt.getInteger("turnOutSyncSelection"))
        turnOutChannelKeyPair = nbt.getChannelKeyPair("turnOutChannnelKeyPair")
        this.forceSelectSignal = SignalLevel.getSignal(nbt.getInteger("forceSelectSignal"))
        this.electricityAuto = nbt.getInteger("electricity")
    }

    override fun writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)
        nbt.setChannelKeyPairArray("detectorChannelKeys", detectorChannelKeys)
        nbt.setInteger("forcedSignalSlection", forcedSignalSelection.id)
        nbt.setInteger("turnOutSyncSelection", turnOutSyncSelection.id)
        nbt.setChannelKeyPair("turnOutChannnelKeyPair", turnOutChannelKeyPair)
        nbt.setInteger("forceSelectSignal", forceSelectSignal.level)
        nbt.setInteger("electricity", electricityAuto)
    }
}