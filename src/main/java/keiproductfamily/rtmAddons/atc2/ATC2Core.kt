package keiproductfamily.rtmAddons.atc2

import jp.ngt.rtm.electric.SignalLevel
import jp.ngt.rtm.entity.train.EntityTrainBase
import jp.ngt.rtm.entity.train.util.FormationManager
import keiproductfamily.network.PacketHandler
import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.EnumTurnOutSwitch
import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection
import keiproductfamily.rtmAddons.formationNumber.FormationNumberCore
import keiproductfamily.rtmAddons.formationNumber.FormationNumberKeyPair
import keiproductfamily.rtmAddons.signalchannel.IRTMSignalChannelReceiver
import keiproductfamily.rtmAddons.signalchannel.RTMSignalChannelMaster
import keiproductfamily.rtmAddons.turnoutChannel.IRTMTurnoutReceiver
import keiproductfamily.rtmAddons.turnoutChannel.RTMTurnoutChannelMaster
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import kotlin.math.min

object ATC2Core {
    val formationSignalMap = HashMap<Long, ATCReceiver>()
    var speedSet = arrayListOf(0, 45, 75, 105, 130, 160)

    val playerFormationSendList = HashMap<EntityPlayer, Long>()

    fun tick() {
        val remove = ArrayList<Long>()
        for ((formationID, receiver) in formationSignalMap) {
            val activeTrain = receiver.executionSignalForTrain()
            if (!activeTrain) {
                remove.add(formationID)
            }
        }
        for (formationID in remove) {
            formationSignalMap.remove(formationID)
        }

        val mc = MinecraftServer.getServer()
        for (world in mc.worldServers) {
            for (player in (world.playerEntities as ArrayList<EntityPlayerMP>)) {
                if (player.ridingEntity is EntityTrainBase) {
                    val formationID = (player.ridingEntity as EntityTrainBase).formation.id
                    if (playerFormationSendList[player] != formationID) {
                        playerFormationSendList[player] = formationID
                        val signal = formationSignalMap[formationID]?.calculateLevel ?: -1
                        val formationNumber = FormationNumberCore.getOrMake(formationID)
                        PacketHandler.sendPacketEPM(ATC2SignalSendMessage(formationID, formationNumber, signal), player)
                        continue
                    }
                } else if (playerFormationSendList[player] ?: 0 != -1L) {
                    playerFormationSendList[player] = -1L
                    PacketHandler.sendPacketEPM(ATC2SignalSendMessage(-1, FormationNumberKeyPair(), -1), player)
                }
            }
        }
    }

    fun setATC2ChannelKeyData(
        formationID: Long,
        signalChannelKeyPairL: ChannelKeyPair?,
        turnOutChannelKeyPair: ChannelKeyPair?,
        signalChannelKeyPairR: ChannelKeyPair?,
        isRemote: Boolean
    ) {
        var atcReceiver = formationSignalMap[formationID]
        if (atcReceiver != null) {
            atcReceiver.setATC2ChannelKey(signalChannelKeyPairL, turnOutChannelKeyPair, signalChannelKeyPairR)
        } else {
            atcReceiver = ATCReceiver(formationID, signalChannelKeyPairL, turnOutChannelKeyPair, signalChannelKeyPairR)
            formationSignalMap[formationID] = atcReceiver
        }

        RTMTurnoutChannelMaster.reCallList(atcReceiver)
        RTMSignalChannelMaster.reCallList(atcReceiver)

        atcReceiver.calcAndExeSignalLevel()
        val formation = FormationManager.getInstance().getFormation(formationID)
        val controller = formation.getControlCar().riddenByEntity
        if (controller is EntityPlayerMP) {
            val formationNumber = FormationNumberCore.getOrMake(formationID)
            PacketHandler.sendPacketEPM(
                ATC2SignalSendMessage(formationID, formationNumber, atcReceiver.calculateLevel),
                controller
            )
        }
    }

    class ATCReceiver(
        val formationID: Long,
        var signalChannelKeyPairL: ChannelKeyPair?,
        var turnOutChannelKeyPair: ChannelKeyPair?,
        var signalChannelKeyPairR: ChannelKeyPair?
    ) : IRTMSignalChannelReceiver, IRTMTurnoutReceiver {
        var signalLevelL: Int = 0
        var nowSide: EnumTurnOutSyncSelection = EnumTurnOutSyncSelection.OFF
        var signalLevelR: Int = 0
        var calculateLevel: Int = 0
        var atcControl: Boolean = false
        var targetNotch: Int = 0
        var beforeNotch: Int = 0

        init {
            RTMSignalChannelMaster.reSet(
                this,
                setOf("", ""),
                setOf(signalChannelKeyPairL?.keyString ?: "", signalChannelKeyPairR?.keyString ?: "")
            )
            RTMTurnoutChannelMaster.reSet(this, null, turnOutChannelKeyPair)
        }

        fun setATC2ChannelKey(
            signalChannelKeyPairL: ChannelKeyPair?,
            turnOutChannelKeyPair: ChannelKeyPair?,
            signalChannelKeyPairR: ChannelKeyPair?
        ) {
            RTMSignalChannelMaster.reSet(
                this,
                setOf(this.signalChannelKeyPairL?.keyString ?: "", this.signalChannelKeyPairR?.keyString ?: ""),
                setOf(signalChannelKeyPairL?.keyString ?: "", signalChannelKeyPairR?.keyString ?: "")
            )
            RTMTurnoutChannelMaster.reSet(this, this.turnOutChannelKeyPair, turnOutChannelKeyPair)
            calcAndExeSignalLevel()
        }

        fun calcAndExeSignalLevel() {
            val turnOutChannel = turnOutChannelKeyPair?.keyString?.let { RTMTurnoutChannelMaster.getChannelData(it) }
            val signalLChannelL =
                signalChannelKeyPairL?.keyString?.let { RTMSignalChannelMaster.getOrMakeChannelData(it) }
            val signalLChannelR =
                signalChannelKeyPairR?.keyString?.let { RTMSignalChannelMaster.getOrMakeChannelData(it) }

            var ret = 0
            when (nowSide) {
                EnumTurnOutSyncSelection.OFF ->
                    if (signalLChannelL != null) {
                        ret = signalLevelL
                    } else if (signalLChannelR != null) {
                        ret = signalLevelR
                    }
                EnumTurnOutSyncSelection.Left -> {
                    if (signalLChannelL != null) {
                        ret = signalLevelL
                    } else if (signalLChannelR != null) {
                        ret = signalLevelR
                    }
                }
                EnumTurnOutSyncSelection.Right -> {
                    if (signalLChannelR != null) {
                        ret = signalLevelR
                    } else if (signalLChannelL != null) {
                        ret = signalLevelL
                    }
                }
            }

            if (calculateLevel != ret) {
                calculateLevel = ret
                val formation = FormationManager.getInstance().getFormation(formationID)
                val rider = formation.getControlCar().riddenByEntity
                if (rider is EntityPlayerMP) {
                    val formationNumber = FormationNumberCore.getOrMake(formationID)
                    PacketHandler.sendPacketEPM(
                        ATC2SignalSendMessage(formationID, formationNumber, calculateLevel),
                        rider
                    )
                }
                executionSignalForTrain()
            }
        }

        /**
         * @return Active Train
         */
        fun executionSignalForTrain(): Boolean {
            val formation = FormationManager.getInstance().getFormation(formationID)
            if (formation != null) {
                val index = calculateLevel - 1
                if (index in 0 until speedSet.size) {
                    val targetSpeed = speedSet[index]
                    val brakeSpeed = if (targetSpeed > 0) {
                        targetSpeed + 5
                    } else {
                        0
                    }
                    val train = formation.getControlCar()
                    val nowSpeed = (((train.speed) * 72.0f) + 0.5f).toInt()

                    if (brakeSpeed < nowSpeed) {
//                        val trainConfig = train.modelSet.config
//                        val dec = trainConfig.deccelerations[trainConfig.deccelerations.size - 2]
//                        val requiredTime = (nowSpeed - targetSpeed) / dec
//                        val requiredLong = nowSpeed * requiredTime + 0.5 * dec * requiredTime * requiredTime

                        val trainConfig = train.modelSet.config
                        val targetNotch = -(trainConfig.deccelerations.size - 2)
                        if (train.notch > targetNotch && train.notch > beforeNotch) {
                            beforeNotch = train.notch
                        }
                        this.targetNotch = targetNotch
                        train.notch = targetNotch

                        atcControl = true
                    } else if (nowSpeed < targetSpeed - 2) {
                        if (atcControl) {
                            val trainConfig = train.modelSet.config
                            var speedNotch = 0
                            if (calculateLevel > 2) {
                                for (_index in trainConfig.maxSpeed.size - 1 downTo 0) {
                                    if (((trainConfig.maxSpeed[_index] * 72.0f) + 0.5f) < targetSpeed - 2) {
                                        speedNotch = _index + 1
                                        break
                                    }
                                }
                                train.notch = min(speedNotch, beforeNotch)
                                atcControl = false
                            }
                        }
                    }
                }
                return true
            }
            return false
        }

        override fun onNewLevelSignal(channelKey: String, signalLevel: SignalLevel): Boolean {
            if (channelKey == this.signalChannelKeyPairL?.keyString) {
                this.signalLevelL = signalLevel.level
                calcAndExeSignalLevel()
                return true
            } else if (channelKey == this.signalChannelKeyPairR?.keyString) {
                this.signalLevelR = signalLevel.level
                calcAndExeSignalLevel()
                return true
            }
            return false
        }

        override fun onNewTurnoutNowSwitch(channelKey: String, nowSide: EnumTurnOutSwitch): Boolean {
            if (channelKey == this.turnOutChannelKeyPair?.keyString) {
                this.nowSide = nowSide.toEnumTurnOutSyncSelection()
                calcAndExeSignalLevel()
                return true
            }
            return false
        }

        override fun onNewTurnoutForceSelect(channelKey: String, turnoutSelect: EnumTurnOutSyncSelection): Boolean {
            return false
        }

        override fun markDirtyAndNotify() {
        }

        override fun isRemote(): Boolean {
            return false
        }
    }
}