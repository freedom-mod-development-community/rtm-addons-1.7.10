package keiproductfamily.rtmAddons.trainDetector

import jp.ngt.rtm.electric.EntityElectricalWiring
import jp.ngt.rtm.electric.SignalLevel
import jp.ngt.rtm.entity.train.EntityBogie
import jp.ngt.rtm.entity.train.EntityTrainBase
import jp.ngt.rtm.entity.train.parts.EntityFloor
import jp.ngt.rtm.item.ItemWire
import keiproductfamily.*
import keiproductfamily.PermissionList.IParmission
import keiproductfamily.network.PacketHandler
import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.RequestEntityNBTData
import keiproductfamily.rtmAddons.detectorChannel.EnumDirection
import keiproductfamily.rtmAddons.detectorChannel.RTMDetectorChannelMaster
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.World
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.min

class EntityTrainDetectorAdvance(world: World?) : EntityElectricalWiring(world), IParmission {
    private var findTrain = false
    var channelKeyPair: ChannelKeyPair = ChannelKeyPair("", "")

    override fun entityInit() {
        super.entityInit()
    }

    override fun setEntityId(p_145769_1_: Int) {
        super.setEntityId(p_145769_1_)
        if (worldObj.isRemote) {
            PacketHandler.sendPacketServer(RequestEntityNBTData(this))
        }
    }

    public override fun writeEntityToNBT(nbt: NBTTagCompound) {
        super.writeEntityToNBT(nbt)
        nbt.setChannelKeyPair("channelKeyPair", channelKeyPair)
    }

    public override fun readEntityFromNBT(nbt: NBTTagCompound) {
        channelKeyPair = nbt.getChannelKeyPair("channelKeyPair")
    }

    val channelKey: String
        get() = channelKeyPair.keyString

    fun setChannelName(channelName: String, channelNumber: String) {
        val name = channelName.trim { it <= ' ' }
        val number = channelNumber.trim { it <= ' ' }
        if (channelKeyPair.name != name || channelKeyPair.number != number) {
            if (name != "" && number != "") {
                channelKeyPair = ChannelKeyPair(name, number)
                RTMDetectorChannelMaster.makeChannel(channelKeyPair)
            }
        }
    }

    var lastCollidedTrainFormationID: Long = -1
    var lastCollidedTrainFormationIDKeepCnt: Int = -1
    override fun onUpdate() {
        if (!worldObj.isRemote) {
            if (channelKeyPair.number != "") {
                val channelData = RTMDetectorChannelMaster.getChannelData(channelKey)
                if (channelData != null) {
                    val (train, direction) = getCollidedTrainData()
                    if (train != null) {
                        if (lastCollidedTrainFormationID != train.formation.id) {
                            val rollSignID = train.getTrainStateData(8)
                            lastCollidedTrainFormationID = train.formation.id
                            channelData.setTrainData(
                                ModCommonVar.findTrainLevel,
                                rollSignID,
                                lastCollidedTrainFormationID,
                                direction
                            )
                        }
                        lastCollidedTrainFormationIDKeepCnt = 200
                    } else {
                        channelData.setTrainData(ModCommonVar.notfindTrainLevel, (-1).toByte(), -1L, direction)
                        if (lastCollidedTrainFormationIDKeepCnt > 0) {
                            lastCollidedTrainFormationIDKeepCnt--
                        } else if (lastCollidedTrainFormationIDKeepCnt == 0) {
                            lastCollidedTrainFormationID = -1
                            lastCollidedTrainFormationIDKeepCnt = -1
                        }
                    }
                }
            }
        }
        super.onUpdate()
    }

    override fun getBoundingBox(): AxisAlignedBB {
        return boundingBox
    }

    fun getCollidedTrainData(): Pair<EntityTrainBase?, EnumDirection> {
        if (worldObj == null) {
            return Pair(null, EnumDirection.Null)
        }
        val collideEntityList =
            worldObj.getEntitiesWithinAABBExcludingEntity(this, getBoundingBox().expand(0.0, 2.0, 0.0))
        if (collideEntityList != null) {
            for (entity in collideEntityList) {
                if (entity is EntityTrainBase) {
                    findTrain = true
                    val angYaw = Math.toDegrees(atan2(entity.posX - entity.prevPosX, entity.posZ - entity.prevPosZ))
                    val diff0 = (angYaw - this.rotationYaw) % 360
                    val diff1 = (angYaw - this.rotationYaw + 360) % 360
                    val direction = if (min(abs(diff0), abs(diff1)) < 90) {
                        EnumDirection.Elimination
                    } else {
                        EnumDirection.Access
                    }
                    return Pair(entity.formation[0]?.train, direction)
                }
                if (entity is EntityBogie) {
                    findTrain = true
                    val diff0 = (entity.movingYaw - this.rotationYaw) % 360
                    val diff1 = (entity.movingYaw - this.rotationYaw + 360) % 360
                    val direction = if (min(abs(diff0), abs(diff1)) < 90) {
                        EnumDirection.Elimination
                    } else {
                        EnumDirection.Access
                    }
                    return Pair(entity.train?.formation?.get(0)?.train, direction)
                }
                if (entity is EntityFloor) {
                    val vehicle = entity.vehicle
                    if (vehicle is EntityTrainBase) {
                        findTrain = true
                        val angYaw = Math.toDegrees(atan2(entity.posX - entity.prevPosX, entity.posZ - entity.prevPosZ))
                        val diff0 = (angYaw - this.rotationYaw) % 360
                        val diff1 = (angYaw - this.rotationYaw + 360) % 360
                        val direction = if (min(abs(diff0), abs(diff1)) < 90) {
                            EnumDirection.Elimination
                        } else {
                            EnumDirection.Access
                        }
                        return Pair(vehicle.formation[0]?.train, direction)
                    }
                }
            }
        }
        findTrain = false
        return Pair(null, EnumDirection.Null)
    }

    override fun interactFirst(player: EntityPlayer): Boolean {
        if (player.heldItem == null || player.heldItem.item !is ItemWire) {
            if (worldObj.isRemote) {
                player.openGui(
                    ModKEIProductFamily.instance, GuiIDs.GuiID_EntityTrainDetectorSetting, worldObj,
                    entityId, 0, 0
                )
            }
            return true
        }
        return super.interactFirst(player)
    }

    override fun getElectricity(): Int {
        return if (findTrain) ModCommonVar.findTrainLevel.level else ModCommonVar.notfindTrainLevel.level
    }

    private val signalLevel: SignalLevel
        get() = if (findTrain) ModCommonVar.findTrainLevel else ModCommonVar.notfindTrainLevel

    override fun setElectricity(par1: Int) {}
    override fun dropItems() {
        entityDropItem(ItemStack(ModKEIProductFamily.itemTrainDetectorAdvance), 0.0f)
    }

    override fun getSubType(): String {
        return "Antenna_Receive"
    }

    override fun getDefaultName(): String {
        return "TrainDetector_01"
    }

    override fun getItem(): ItemStack {
        return ItemStack(ModKEIProductFamily.itemTrainDetectorAdvance)
    }

    init {
        setSize(1.0f, 0.0625f)
        ignoreFrustumCheck = true
    }

    override fun getName(): String {
        return "EntityTrainDetectorAdvance"
    }
}