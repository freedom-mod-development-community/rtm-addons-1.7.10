package keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights

import jp.ngt.rtm.electric.IProvideElectricity
import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.*
import keiproductfamily.normal.TileNormal
import keiproductfamily.rtmAddons.*
import keiproductfamily.rtmAddons.detectorChannel.IRTMDetectorReceiver
import keiproductfamily.rtmAddons.detectorChannel.RTMDetectorChannelMaster
import keiproductfamily.rtmAddons.turnoutChannel.IRTMTurnoutReceiver
import keiproductfamily.rtmAddons.turnoutChannel.RTMTurnoutChannelMaster
import net.minecraft.nbt.NBTTagCompound
import kotlin.properties.Delegates

class ReceiverTrafficLightTile : TileNormal(), IRTMDetectorReceiver, IRTMTurnoutReceiver, IProvideElectricity {
    var detectorChannelKeys = Array(6) { ChannelKeyPair("", "") }
    var forcedSignalSlection = EnumForcedSignalMode.Auto
    var turnOutSyncSelection = EnumTurnOutSyncSelection.OFF
    var turnOutChannelKeyPair = ChannelKeyPair("", "")
    var forceSelectSignal = SignalLevel.STOP

    var nowTurnOutSwitch: EnumTurnOutSwitch by Delegates.notNull()
    var isUpdate: Boolean = false

    fun setDetectorChunnelKeys(channelKeys: Array<ChannelKeyPair>) {
        if (this.detectorChannelKeys.size == channelKeys.size) {
            RTMDetectorChannelMaster.reSet(this, this.detectorChannelKeys, channelKeys)
            this.detectorChannelKeys = channelKeys
            this.markDirtyAndNotify()
            this.markDirty()
            isUpdate = true
        }
    }

    fun setTurnoutChunnelKey(channelKey: ChannelKeyPair) {
        RTMTurnoutChannelMaster.reSet(this, this.turnOutChannelKeyPair, channelKey)
        this.turnOutChannelKeyPair = channelKey
        this.markDirtyAndNotify()
        this.markDirty()
        isUpdate = true
    }

    fun setDatas(
        forcedSignalSlection: EnumForcedSignalMode = EnumForcedSignalMode.Auto,
        turnOutSyncSelection: EnumTurnOutSyncSelection = EnumTurnOutSyncSelection.OFF,
        forceSelectSignal: SignalLevel = SignalLevel.STOP
    ) {
        this.forcedSignalSlection = forcedSignalSlection
        this.turnOutSyncSelection = turnOutSyncSelection
        this.forceSelectSignal = forceSelectSignal
        this.markDirtyAndNotify()
    }

    override fun validate() {
        super.validate()
        if (!this.worldObj.isRemote) {
            RTMDetectorChannelMaster.reCallList(this)
        }
    }

    override fun onNewDetectorSignal(channelKey: String, signalLevel: SignalLevel, rollSignID: Byte): Boolean {
        if (signalLevel == ModCommonVar.findTrainLevel) {
            for ((index, pair) in detectorChannelKeys.withIndex()) {
                if (channelKey == pair.keyString) {
                    _electricityAuto = index + 1
                    return true
                }
            }
        }
        return false
    }

    override fun onNewTurnoutNowSwitch(channelKey: String, nowSide: EnumTurnOutSwitch): Boolean {
        if (channelKey == turnOutChannelKeyPair.keyString) {
            nowTurnOutSwitch = nowSide
            return true
        }
        return false
    }

    override fun onNewTurnoutForceSelect(channelKey: String, turnoutSelect: EnumTurnOutSyncSelection): Boolean {
        TODO("Not yet implemented")
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


    private var _electricityAuto: Int = 0
    override fun getElectricity(): Int {
        return if (forcedSignalSlection.force) {
            forceSelectSignal.level
        } else if (turnOutSyncSelection == EnumTurnOutSyncSelection.OFF) {
            _electricityAuto
        } else {
            if (turnOutSyncSelection == nowTurnOutSwitch) {
                _electricityAuto
            } else {
                SignalLevel.STOP.level
            }
        }
    }

    override fun setElectricity(x: Int, y: Int, z: Int, level: Int) {
        //TODO 用いるか判断
        //_electricity = level
        val i = 0
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        val cks = nbt.getChannelKeyPairArray("detectorChannelKeys")
        setDetectorChunnelKeys(cks)
        this.forcedSignalSlection = EnumForcedSignalMode.getType(nbt.getInteger("forcedSignalSlection"))
        this.turnOutSyncSelection = EnumTurnOutSyncSelection.getType(nbt.getInteger("turnOutSyncSelection"))
        this.turnOutChannelKeyPair = nbt.getChannelKeyPair("turnOutChannnelKeyPair")
        this.forceSelectSignal = SignalLevel.getSignal(nbt.getInteger("forceSelectSignal"))
        this._electricityAuto = nbt.getInteger("electricity")
    }

    override fun writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)
        nbt.setChannelKeyPairArray("detectorChannelKeys", detectorChannelKeys)
        nbt.setInteger("forcedSignalSlection", forcedSignalSlection.id)
        nbt.setInteger("turnOutSyncSelection", turnOutSyncSelection.id)
        nbt.setChannelKeyPair("turnOutChannnelKeyPair", turnOutChannelKeyPair)
        nbt.setInteger("forceSelectSignal", forceSelectSignal.level)
        nbt.setInteger("electricity", _electricityAuto)
    }
}