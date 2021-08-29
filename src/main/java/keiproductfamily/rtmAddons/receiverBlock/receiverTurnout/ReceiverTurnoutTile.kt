package keiproductfamily.rtmAddons.receiverBlock.receiverTurnout

import jp.kaiz.kaizpatch.fixrtm.threadFactoryWithPrefix
import jp.ngt.rtm.electric.IProvideElectricity
import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.ModCommonVar
import keiproductfamily.getChannelKeyPair
import keiproductfamily.normal.TileNormal
import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.EnumTurnOutSwitch
import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection
import keiproductfamily.rtmAddons.detectorChannel.EnumDirection
import keiproductfamily.rtmAddons.detectorChannel.IRTMDetectorReceiver
import keiproductfamily.rtmAddons.detectorChannel.RTMDetectorChannelMaster
import keiproductfamily.rtmAddons.turnoutChannel.IRTMTurnoutReceiver
import keiproductfamily.rtmAddons.turnoutChannel.RTMTurnoutChannelMaster
import keiproductfamily.setChannelKeyPair
import net.minecraft.nbt.NBTTagCompound
import java.util.*

class ReceiverTurnoutTile : TileNormal(), IRTMDetectorReceiver, IRTMTurnoutReceiver, IProvideElectricity {
    /**
     * 自身の名称
     */
    var thisTurnOutChannelKeyPair = ChannelKeyPair("", "")
        set(channelKey: ChannelKeyPair) {
            RTMTurnoutChannelMaster.reSet(this, this.thisTurnOutChannelKeyPair, channelKey)
            RTMTurnoutChannelMaster.getChannelData(channelKey.keyString)
                ?.setTurnOutNowSwitchData(this.getTurnOutSelection())
            if (this.thisTurnOutChannelKeyPair != channelKey) {
                field = channelKey
                this.markDirtyAndNotify()
                isUpdate = true
            }
        }

    /**
     * 左側を指定する方向幕のID
     */
    var turnOutLeftSelectRollIDs = BitSet(16)

    /**
     * 方向幕などの情報を参照する検知器の名称
     */
    var detectorChannelKey = ChannelKeyPair("", "")
        set(channelKey: ChannelKeyPair) {
            RTMDetectorChannelMaster.reSet(this, this.detectorChannelKey, channelKey)
            if (detectorChannelKey != channelKey) {
                field = channelKey
                this.markDirtyAndNotify()
                isUpdate = true
            }
        }

    /**
     * デフォルト（RS出力なし）での分岐の方向
     */
    var rsOFFTurnOutSelection = EnumTurnOutSwitch.Right

    /**
     * 通常時の分岐開通の方向
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
     * 方向幕による制御を維持する時間
     * 0 のとき、次の列車が来るまで維持
     * 1～ 列車を検知してから方向幕による制御を維持する時間 単位:秒
     */
    var keepTurnOutSelectTime = 0

    var receiveCnt = 0

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

    fun setDatas(
        rsOFFTurnOutSelection: EnumTurnOutSwitch,
        defaultTurnOutSelection: EnumTurnOutSwitch,
        turnOutLeftSelectRollIDs: BitSet,
        keepTurnOutSelectTime: Int
    ) {
        this.rsOFFTurnOutSelection = rsOFFTurnOutSelection
        this.defaultTurnOutSelection = defaultTurnOutSelection
        this.turnOutSyncSelection = defaultTurnOutSelection
        this.turnOutLeftSelectRollIDs = turnOutLeftSelectRollIDs
        this.keepTurnOutSelectTime = keepTurnOutSelectTime
        RTMDetectorChannelMaster.reCallList(this)
        this.markDirtyAndNotify()
        isUpdate = true
    }

    fun syncNowSwitchData() {
        RTMTurnoutChannelMaster.getChannelData(thisTurnOutChannelKeyPair.keyString)
            ?.setTurnOutNowSwitchData(this.getTurnOutSelection())
    }

    override fun onNewDetectorSignal(
        channelKey: String,
        signalLevel: SignalLevel,
        rollSignID: Byte,
        formationID: Long,
        direction: EnumDirection
    ): Boolean {
        if (this.detectorChannelKey.keyString == channelKey) {
            //error or 時間経過で方向を自動復帰し、列車が見つかっていない経過時間が設定よりたっている
            if (signalLevel == ModCommonVar.notfindTrainLevel || (rollSignID < 0 || 15 < rollSignID)) {
                if (keepTurnOutSelectTime in 1..receiveCnt / 20) {
                    this.turnOutSyncSelection = this.defaultTurnOutSelection
                }
                this.isFindTrain = false
            } else if (signalLevel == ModCommonVar.findTrainLevel) {
                if (turnOutLeftSelectRollIDs.get(rollSignID.toInt())) {
                    this.turnOutSyncSelection = EnumTurnOutSwitch.Left
                } else {
                    this.turnOutSyncSelection = EnumTurnOutSwitch.Right
                }
                this.isFindTrain = true
                receiveCnt = 0
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
            markDirtyAndNotify()
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

    fun changeNotify() {
        this.worldObj.notifyBlockOfNeighborChange(xCoord, yCoord, zCoord, this.blockType)
    }


    private var _electricityAuto: Int = 0
    override fun getElectricity(): Int {
        return SignalLevel.STOP.level
    }

    override fun setElectricity(x: Int, y: Int, z: Int, level: Int) {}

    var rsPowerBuff = 0
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

        var ret = 0

        if (retTurnout) {
            ret = if (getTurnOutSelection() != rsOFFTurnOutSelection) {
                15
            } else {
                0
            }
        }

        if (retDetector) {
            ret = if (isFindTrain) {
                15
            } else {
                0
            }
        }
        if (ret != rsPowerBuff) {
            changeNotify()
            rsPowerBuff = ret
        }
        return ret
    }

    private var initialized = false
    override fun updateEntity() {
        super.updateEntity()
        if (!initialized) {
            if (!this.worldObj.isRemote) {
                RTMDetectorChannelMaster.reCallList(this)
                RTMTurnoutChannelMaster.reCallList(this)
            }
            initialized = true
        }

        if (receiveCnt < keepTurnOutSelectTime * 20 + 20) {
            receiveCnt++
        }
    }

    override fun isRemote(): Boolean {
        return worldObj?.isRemote ?: false
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        thisTurnOutChannelKeyPair = nbt.getChannelKeyPair("thisTurnOutChannelKeyPair")
        this.turnOutLeftSelectRollIDs = BitSet.valueOf(nbt.getByteArray("turnOutLeftSelectRollIDs"))
        detectorChannelKey = (nbt.getChannelKeyPair("detectorChannelKey"))
        this.turnOutSyncSelection = EnumTurnOutSwitch.getType(nbt.getInteger("turnOutSyncSelection"))
        var rsOFFSelectionID = nbt.getInteger("rsOFFTurnOutSelection")
        val defaultSelectionID = nbt.getInteger("defaultTurnOutSelection")
        if (rsOFFSelectionID == 0) {
            rsOFFSelectionID = defaultSelectionID
        }
        this.rsOFFTurnOutSelection = EnumTurnOutSwitch.getType(rsOFFSelectionID)
        this.defaultTurnOutSelection = EnumTurnOutSwitch.getType(defaultSelectionID)
        this.turnOutOperation = EnumTurnOutSyncSelection.getType(nbt.getInteger("turnOutOperation"))
        this._electricityAuto = nbt.getInteger("electricity")
        this.keepTurnOutSelectTime = nbt.getInteger("keepTurnOutSelectTime")
    }

    override fun writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)
        nbt.setChannelKeyPair("thisTurnOutChannelKeyPair", thisTurnOutChannelKeyPair)
        nbt.setByteArray("turnOutLeftSelectRollIDs", turnOutLeftSelectRollIDs.toByteArray())
        nbt.setChannelKeyPair("detectorChannelKey", detectorChannelKey)
        nbt.setInteger("turnOutSyncSelection", turnOutSyncSelection.id)
        nbt.setInteger("rsOFFTurnOutSelection", rsOFFTurnOutSelection.id)
        nbt.setInteger("defaultTurnOutSelection", defaultTurnOutSelection.id)
        nbt.setInteger("turnOutOperation", turnOutOperation.id)
        nbt.setInteger("electricity", _electricityAuto)
        nbt.setInteger("keepTurnOutSelectTime", keepTurnOutSelectTime)
    }
}