package keiproductfamily.rtmAddons.receiverBlock.receiverTurnout

import jp.ngt.rtm.electric.IProvideElectricity
import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.*
import keiproductfamily.normal.TileNormal
import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.EnumTurnOutSwitch
import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection
import keiproductfamily.rtmAddons.detectorChannel.IRTMDetectorReceiver
import keiproductfamily.rtmAddons.detectorChannel.RTMDetectorChannelMaster
import keiproductfamily.rtmAddons.turnoutChannel.IRTMTurnoutReceiver
import keiproductfamily.rtmAddons.turnoutChannel.RTMTurnoutChannelMaster
import net.minecraft.nbt.NBTTagCompound
import java.lang.IllegalArgumentException
import java.util.*

class ReceiverTurnoutTile : TileNormal(), IRTMDetectorReceiver, IRTMTurnoutReceiver, IProvideElectricity {
    /**
     * 自身の名称
     */
    var thisTurnOutChannelKeyPair = ChannelKeyPair("", "")

    /**
     * 左側を指定する方向幕のID
     */
    var turnOutLeftSelectRollIDs = BitSet(16)

    /**
     * 方向幕などの情報を参照する検知器の名称
     */
    var detectorChannelKey = ChannelKeyPair("", "")

    /**
     * デフォルト（RS出力なし）での分岐の方向
     */
    var defaultTurnOutSelection = EnumTurnOutSwitch.Right

    /**
     * detectorChannelKeyの列車検知器が車両を検知しているか
     */
    var isFindTrain = false

    /**
     * 方向幕などから判断した分岐方向 方向幕情報から自動判断される
     */
    var turnOutSyncSelection = EnumTurnOutSwitch.Right

    /**
     * 介入があるか、介入で支持された分岐方向　介入の情報を受信
     */
    var turnOutOperation = EnumTurnOutSyncSelection.OFF

    /**
     * 様々な処理の最終的な結果
     */
    fun getTurnOutSelection(): EnumTurnOutSwitch {
        return if (turnOutOperation == EnumTurnOutSyncSelection.OFF) {
            turnOutSyncSelection
        } else {
            turnOutOperation.toEnumTurnOutSwitch()
        }
    }


    var isUpdate: Boolean = false

    fun setDetectorChunnelKey(channelKey: ChannelKeyPair) {
        RTMDetectorChannelMaster.reSet(this, this.detectorChannelKey, channelKey)
        this.detectorChannelKey = channelKey
        this.markDirtyAndNotify()
        this.markDirty()
        isUpdate = true
    }

    fun setThisTurnoutChunnelKey(channelKey: ChannelKeyPair) {
        RTMTurnoutChannelMaster.reSet(this, this.thisTurnOutChannelKeyPair, channelKey)
        RTMTurnoutChannelMaster.getChannelData(channelKey.keyString)
            ?.setTurnOutNowSwitchData(this.getTurnOutSelection())
        this.thisTurnOutChannelKeyPair = channelKey
        this.markDirtyAndNotify()
        this.markDirty()
        isUpdate = true
    }

    fun setDatas(
        defaultTurnOutSelection: EnumTurnOutSwitch,
        turnOutLeftSelectRollIDs: BitSet
    ) {
        this.defaultTurnOutSelection = defaultTurnOutSelection
        this.turnOutLeftSelectRollIDs = turnOutLeftSelectRollIDs
        RTMDetectorChannelMaster.reCallList(this)
        this.markDirtyAndNotify()
    }

    override fun validate() {
        super.validate()
        if (!this.worldObj.isRemote) {
            RTMDetectorChannelMaster.reCallList(this)
            RTMTurnoutChannelMaster.reCallList(this)
        }
    }

    fun syncNowSwitchData() {
        RTMTurnoutChannelMaster.getChannelData(thisTurnOutChannelKeyPair.keyString)
            ?.setTurnOutNowSwitchData(this.getTurnOutSelection())
    }

    override fun onNewDetectorSignal(channelKey: String, signalLevel: SignalLevel, rollSignID: Byte): Boolean {
        if (this.detectorChannelKey.keyString == channelKey) {
            if (signalLevel == ModCommonVar.notfindTrainLevel || rollSignID < 0 || 15 < rollSignID) {
                this.turnOutSyncSelection = this.defaultTurnOutSelection
                this.isFindTrain = false
            } else {
                if (turnOutLeftSelectRollIDs.get(rollSignID.toInt())) {
                    this.turnOutSyncSelection = EnumTurnOutSwitch.Left
                } else {
                    this.turnOutSyncSelection = EnumTurnOutSwitch.Right
                }
                this.isFindTrain = true
            }
            syncNowSwitchData()
            return true
        }
        return false
    }

    override fun onNewTurnoutNowSwitch(channelKey: String, nowSide: EnumTurnOutSwitch): Boolean {
        //自分自身なので無視
        return false
    }

    override fun onNewTurnoutForceSelect(channelKey: String, turnoutSelect: EnumTurnOutSyncSelection): Boolean {
        if (channelKey == thisTurnOutChannelKeyPair.keyString) {
            turnOutOperation = turnoutSelect
            syncNowSwitchData()
            return true
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
                ModCommonVar.receiverTurnoutBlock,
                ModCommonVar.receiverTurnoutBlock,
                3
            )
        }
    }


    private var _electricityAuto: Int = 0
    override fun getElectricity(): Int {
        return SignalLevel.STOP.level
    }

    override fun setElectricity(x: Int, y: Int, z: Int, level: Int) {
        //TODO 用いるか判断
        //_electricity = level
        val i = 0
    }

    fun getRSPower(outDirection: Int): Int {
        var retTurnout = false
        var retDetector = false

        if (outDirection == 0) {
            retTurnout = true
        }

        val meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord)
        val baseDirection = meta and 3

        when (baseDirection) {
            0 -> {
                if (outDirection == 4) {
                    retTurnout = true
                } else if (outDirection == 5) {
                    retDetector = true
                }
            }
            1 -> {
                if (outDirection == 2) {
                    retTurnout = true
                } else if (outDirection == 3) {
                    retDetector = true
                }
            }
            2 -> {
                if (outDirection == 5) {
                    retTurnout = true
                } else if (outDirection == 4) {
                    retDetector = true
                }
            }
            3 -> {
                if (outDirection == 3) {
                    retTurnout = true
                } else if (outDirection == 2) {
                    retDetector = true
                }
            }
        }

        if (retTurnout) {
            return if (getTurnOutSelection() != defaultTurnOutSelection) {
                15
            } else {
                0
            }
        }

        if (retDetector) {
            return if (isFindTrain) {
                15
            } else {
                0
            }
        }
        return 0
    }


    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.setThisTurnoutChunnelKey(nbt.getChannelKeyPair("thisTurnOutChannelKeyPair"))
        this.turnOutLeftSelectRollIDs = BitSet.valueOf(nbt.getByteArray("turnOutLeftSelectRollIDs"))
        this.setDetectorChunnelKey(nbt.getChannelKeyPair("detectorChannelKey"))
        this.turnOutSyncSelection = EnumTurnOutSwitch.getType(nbt.getInteger("turnOutSyncSelection"))
        this.defaultTurnOutSelection = EnumTurnOutSwitch.getType(nbt.getInteger("defaultTurnOutSelection"))
        this.turnOutOperation = EnumTurnOutSyncSelection.getType(nbt.getInteger("turnOutOperation"))
        this._electricityAuto = nbt.getInteger("electricity")
    }

    override fun writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)
        nbt.setChannelKeyPair("thisTurnOutChannelKeyPair", thisTurnOutChannelKeyPair)
        nbt.setByteArray("turnOutLeftSelectRollIDs", turnOutLeftSelectRollIDs.toByteArray())
        nbt.setChannelKeyPair("detectorChannelKey", detectorChannelKey)
        nbt.setInteger("turnOutSyncSelection", turnOutSyncSelection.id)
        nbt.setInteger("defaultTurnOutSelection", defaultTurnOutSelection.id)
        nbt.setInteger("turnOutOperation", turnOutOperation.id)
        nbt.setInteger("electricity", _electricityAuto)
    }
}