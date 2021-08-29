package keiproductfamily.rtmAddons.receiverBlock.receiverTraffficLightsType2

import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.ModCommonVar
import keiproductfamily.getChannelKeyPair
import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.EnumForcedSignalMode
import keiproductfamily.rtmAddons.EnumTurnOutSwitch
import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection
import keiproductfamily.rtmAddons.detectorChannel.EnumDirection
import keiproductfamily.rtmAddons.detectorChannel.RTMDetectorChannelMaster
import keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights.ReceiverTrafficLightTile
import keiproductfamily.rtmAddons.turnoutChannel.RTMTurnoutChannelMaster
import keiproductfamily.setChannelKeyPair
import net.minecraft.nbt.NBTTagCompound

class ReceiverTrafficLightTileType2 : ReceiverTrafficLightTile() {
    var turnOutSyncSelection2 = EnumTurnOutSyncSelection.OFF
    var turnOutChannelKeyPair2 = ChannelKeyPair("", "")
        set(channelKey: ChannelKeyPair) {
            RTMTurnoutChannelMaster.reSet(this, this.turnOutChannelKeyPair2, channelKey)
            field = channelKey
            this.markDirtyAndNotify()
            isUpdate = true
        }

    override var detectorChannelKeys = Array(6) { ChannelKeyPair("", "") }
        set(channelKeys: Array<ChannelKeyPair>) {
            if (this.detectorChannelKeys.size == channelKeys.size) {
                RTMDetectorChannelMaster.reSet(this, this.detectorChannelKeys, channelKeys)
                field = channelKeys
                initObstructions()
                isUpdate = true
            }
        }

    var nowTurnOutSwitch2 = EnumTurnOutSyncSelection.OFF

    fun setDatas(
        forcedSignalSlection: EnumForcedSignalMode = EnumForcedSignalMode.Auto,
        turnOutSyncSelection: EnumTurnOutSyncSelection = EnumTurnOutSyncSelection.OFF,
        turnOutSyncSelection2: EnumTurnOutSyncSelection = EnumTurnOutSyncSelection.OFF,
        forceSelectSignal: SignalLevel = SignalLevel.STOP
    ) {
        this.forcedSignalSelection = forcedSignalSlection
        this.turnOutSyncSelection = turnOutSyncSelection
        this.turnOutSyncSelection2 = turnOutSyncSelection2
        this.forceSelectSignal = forceSelectSignal
        RTMDetectorChannelMaster.reCallList(this)
        RTMTurnoutChannelMaster.reCallList(this)
        this.markDirtyAndNotify()
        isUpdate = true
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
                    val obIndex = obstructionSignalIndexMap[SignalLevel.getSignal(index + 1)] ?: return false
                    val obSignal = obstructionIndexSignalMap[obIndex]
                    val mbIndex = obIndex - 1
                    val mbSignal = if (mbIndex >= 0) {
                        obstructionIndexSignalMap[mbIndex]
                    } else {
                        null
                    }

                    var targetOBS: SignalLevel? = null

                    //駅から出発
                    if (direction == EnumDirection.Elimination) {
                        if (obIndex == 0) {
                            targetOBS = obSignal
                            obstructionOnRailMap[obSignal] = formationID
                        } else if (mbSignal != null) {
                            //obIndexが1以上なら[mbSignal != null]は必ずTrueのはず
                            if (obIndex < obstructionOnRailMap.size - 1) {
                                obstructionOnRailMap[obSignal] = formationID
                                targetOBS = obSignal
                                obstructionOnRailMap[mbSignal] = -1
                            } else {
                                obstructionOnRailMap[mbSignal] = -1
                            }
                        }

                    } else if (direction == EnumDirection.Access) {
                        //駅に入ってくる
                        if (obIndex == obstructionOnRailMap.size - 1) {
                            if (mbSignal != null) {
                                obstructionOnRailMap[mbSignal] = formationID
                                targetOBS = mbSignal
                            }
                        } else if (obIndex > 0 && mbSignal != null) {
                            obstructionOnRailMap[mbSignal] = formationID
                            targetOBS = mbSignal
                            obstructionOnRailMap[obSignal] = -1
                        } else { //obIndex == 0
                            obstructionOnRailMap[obSignal] = -1
                        }
                    }

                    for ((signal, obstruct) in obstructionOnRailMap) {
                        if (obstruct == formationID && signal != targetOBS) {
                            obstructionOnRailMap[signal] = -1
                        }
                    }

                    for (element in obstructionIndexSignalMap) {
                        if (obstructionOnRailMap[element]!! > 0) {
                            electricityAuto = element.level
                            return true
                        }
                    }
                    electricityAuto = obstructionIndexSignalMap[obstructionIndexSignalMap.size - 1].level
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
        } else if (channelKey == turnOutChannelKeyPair2.keyString) {
            val newValue = nowSide.toEnumTurnOutSyncSelection()
            if (nowTurnOutSwitch2 != newValue) {
                nowTurnOutSwitch2 = newValue
                changeNotify()
            }
            return true
        }
        return false
    }


    var obstructionOnRailMap: HashMap<SignalLevel, Long> = HashMap<SignalLevel, Long>()
    var obstructionIndexSignalMap: Array<SignalLevel> = Array<SignalLevel>(6) { SignalLevel.getSignal(it) }
    var obstructionSignalIndexMap: HashMap<SignalLevel, Int> = HashMap<SignalLevel, Int>()

    private fun initObstructions() {
        val newOnRailMap = HashMap<SignalLevel, Long>()
        val newIndexSignalMap = ArrayList<SignalLevel>()
        val newSignalIndexMap = HashMap<SignalLevel, Int>()
        for ((index, pair) in detectorChannelKeys.withIndex()) {
            if (pair.hasData()) {
                val signalLevel = SignalLevel.getSignal(index + 1)
                newOnRailMap[signalLevel] = obstructionOnRailMap[signalLevel] ?: -1
                val index = newIndexSignalMap.size
                newIndexSignalMap.add(signalLevel)
                newSignalIndexMap[signalLevel] = index
            }
        }
        obstructionOnRailMap = newOnRailMap
        obstructionIndexSignalMap =
            newIndexSignalMap.toArray(Array<SignalLevel>(newIndexSignalMap.size) { SignalLevel.getSignal(it) })
        obstructionSignalIndexMap = newSignalIndexMap
    }

    override fun getElectricity(): Int {
        return if (forcedSignalSelection.force) {
            forceSelectSignal.level
        } else if (turnOutSyncSelection == EnumTurnOutSyncSelection.OFF && turnOutSyncSelection2 == EnumTurnOutSyncSelection.OFF) {
            electricityAuto
        } else {
            if (
                (nowTurnOutSwitch == EnumTurnOutSyncSelection.OFF || turnOutSyncSelection == nowTurnOutSwitch) &&
                (nowTurnOutSwitch2 == EnumTurnOutSyncSelection.OFF || turnOutSyncSelection2 == nowTurnOutSwitch2)
            ) {
                electricityAuto
            } else {
                SignalLevel.STOP.level
            }
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.turnOutSyncSelection2 = EnumTurnOutSyncSelection.getType(nbt.getInteger("turnOutSyncSelection2"))
        turnOutChannelKeyPair2 = nbt.getChannelKeyPair("turnOutChannelKeyPair2")
    }

    override fun writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)
        nbt.setInteger("turnOutSyncSelection2", turnOutSyncSelection2.id)
        nbt.setChannelKeyPair("turnOutChannelKeyPair2", turnOutChannelKeyPair2)
    }
}