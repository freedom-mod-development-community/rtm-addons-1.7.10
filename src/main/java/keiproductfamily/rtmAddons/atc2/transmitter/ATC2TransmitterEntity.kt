package keiproductfamily.rtmAddons.atc2.transmitter

import jp.ngt.rtm.entity.train.EntityBogie
import jp.ngt.rtm.entity.train.EntityTrainBase
import jp.ngt.rtm.entity.train.parts.EntityFloor
import jp.ngt.rtm.item.ItemWire
import keiproductfamily.GuiIDs
import keiproductfamily.ModKEIProductFamily
import keiproductfamily.PermissionList.IParmission
import keiproductfamily.getChannelKeyPair
import keiproductfamily.network.PacketHandler
import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.RequestEntityNBTData
import keiproductfamily.rtmAddons.atc2.ATC2Core
import keiproductfamily.rtmAddons.detectorChannel.EnumDirection
import keiproductfamily.rtmAddons.formationNumber.FormationNumberCore
import keiproductfamily.rtmAddons.signalchannel.RTMSignalChannelMaster
import keiproductfamily.setChannelKeyPair
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.DamageSource
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.min

class ATC2TransmitterEntity(world: World) : Entity(world), IParmission {
    private var findTrain = false
    var signalChannelKeyPairL: ChannelKeyPair = ChannelKeyPair("", "")
    var turnOutChannelKeyPair: ChannelKeyPair = ChannelKeyPair("", "")
    var signalChannelKeyPairR: ChannelKeyPair = ChannelKeyPair("", "")
    var subjectFormationRegex: String = ""

    override fun shouldRenderInPass(pass: Int): Boolean {
        return pass >= 0
    }

    override fun setEntityId(p_145769_1_: Int) {
        super.setEntityId(p_145769_1_)
        if (worldObj.isRemote) {
            PacketHandler.sendPacketServer(RequestEntityNBTData(this))
        }
    }

    override fun entityInit() {
    }

    public override fun writeEntityToNBT(nbt: NBTTagCompound) {
        nbt.setChannelKeyPair("signalChannelKeyPairL", signalChannelKeyPairL)
        nbt.setChannelKeyPair("turnOutChannelKeyPair", turnOutChannelKeyPair)
        nbt.setChannelKeyPair("signalChannelKeyPairR", signalChannelKeyPairR)
        nbt.setString("subjectFormationRegex", subjectFormationRegex)
    }

    public override fun readEntityFromNBT(nbt: NBTTagCompound) {
        signalChannelKeyPairL = nbt.getChannelKeyPair("signalChannelKeyPairL")
        turnOutChannelKeyPair = nbt.getChannelKeyPair("turnOutChannelKeyPair")
        signalChannelKeyPairR = nbt.getChannelKeyPair("signalChannelKeyPairR")
        subjectFormationRegex = nbt.getString("subjectFormationRegex")
    }

    override fun canBePushed(): Boolean {
        return false
    }

    override fun canBeCollidedWith(): Boolean {
        return !isDead
    }

    override fun canTriggerWalking(): Boolean {
        return false
    }

    fun setSignalChannelNameL(channelKey: ChannelKeyPair) {
        val name = channelKey.name.trim { it <= ' ' }
        val number = channelKey.number.trim { it <= ' ' }
        if (signalChannelKeyPairL.name != name || signalChannelKeyPairL.number != number) {
            if (name != "" && number != "") {
                signalChannelKeyPairL = channelKey
                RTMSignalChannelMaster.makeChannel(signalChannelKeyPairL)
            }
        }
    }

    fun setSignalChannelNameR(channelKey: ChannelKeyPair) {
        val name = channelKey.name.trim { it <= ' ' }
        val number = channelKey.number.trim { it <= ' ' }
        if (signalChannelKeyPairR.name != name || signalChannelKeyPairR.number != number) {
            if (name != "" && number != "") {
                signalChannelKeyPairR = channelKey
                RTMSignalChannelMaster.makeChannel(signalChannelKeyPairR)
            }
        }
    }

    fun setTurnOutChannelName(channelKey: ChannelKeyPair) {
        val name = channelKey.name.trim { it <= ' ' }
        val number = channelKey.number.trim { it <= ' ' }
        if (turnOutChannelKeyPair.name != name || turnOutChannelKeyPair.number != number) {
            if (name != "" && number != "") {
                turnOutChannelKeyPair = channelKey
                RTMSignalChannelMaster.makeChannel(turnOutChannelKeyPair)
            }
        }
    }



    var lastCollidedTrainFormationID: Long = -1
    var lastCollidedTrainFormationIDKeepCnt: Int = -1
    override fun onUpdate() {
        if (!worldObj.isRemote) {
            if (signalChannelKeyPairL.hasData() || signalChannelKeyPairR.hasData()) {
                val (train, direction) = getCollidedTrainData()
                var subjectFlag = false
                if (train != null) {
                    subjectFlag = if (subjectFormationRegex == "") {
                        true
                    } else {
                        val formationNumber = FormationNumberCore.getOrMake(train.formation.id)
                        formationNumber.keyString.matches(Regex(subjectFormationRegex))
                    }
                }

                if (train != null && subjectFlag) {
                    if (lastCollidedTrainFormationID != train.formation.id) {
                        lastCollidedTrainFormationID = train.formation.id
                        ATC2Core.setATC2ChannelKeyData(
                            lastCollidedTrainFormationID,
                            signalChannelKeyPairL,
                            turnOutChannelKeyPair,
                            signalChannelKeyPairR,
                            this.worldObj.isRemote
                        )
                        lastCollidedTrainFormationIDKeepCnt = 200
                    }
                } else {
                    if (lastCollidedTrainFormationIDKeepCnt > 0) {
                        lastCollidedTrainFormationIDKeepCnt--
                    } else if (lastCollidedTrainFormationIDKeepCnt == 0) {
                        lastCollidedTrainFormationID = -1
                        lastCollidedTrainFormationIDKeepCnt--
                    }
                }
            }
        }
        super.onUpdate()
    }

    override fun getBoundingBox(): AxisAlignedBB {
        return boundingBox
    }

    private fun getCollidedTrainData(): Pair<EntityTrainBase?, EnumDirection> {
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

    override fun attackEntityFrom(par1: DamageSource, par2: Float): Boolean {
        return if (this.isEntityInvulnerable || isDead) {
            false
        } else {
            if (par1.entity is EntityPlayer) {
                if (!worldObj.isRemote) {
                    setBeenAttacked()
                    val entityplayer = par1.entity as EntityPlayer
                    if (!entityplayer.capabilities.isCreativeMode) {
                        dropItems()
                    }
                    val block = Blocks.stone
                    worldObj.playSoundEffect(
                        posX,
                        posY,
                        posZ,
                        block.stepSound.func_150496_b(),
                        (block.stepSound.getVolume() + 1.0f) / 2.0f,
                        block.stepSound.pitch * 0.8f
                    )
                    setDead()
                }
                return true
            }
            false
        }
    }

    fun dropItems() {
        entityDropItem(ItemStack(ModKEIProductFamily.itemATC2Transmitter), 0.0f)
    }

    override fun moveEntity(par1: Double, par3: Double, par5: Double) {}

    override fun addVelocity(par1: Double, par3: Double, par5: Double) {}

    override fun interactFirst(player: EntityPlayer): Boolean {
        if (player.heldItem == null || player.heldItem.item !is ItemWire) {
            if (worldObj.isRemote) {
                player.openGui(
                    ModKEIProductFamily.instance, GuiIDs.GuiID_ATC2TransmitterEntitySetting, worldObj,
                    entityId, 0, 0
                )
            }
            return true
        }
        return super.interactFirst(player)
    }

    override fun getPickedResult(target: MovingObjectPosition?): ItemStack? {
        return getItem()
    }

    private fun getItem(): ItemStack {
        return ItemStack(ModKEIProductFamily.itemATC2Transmitter)
    }

    init {
        setSize(1.0f, 0.0625f)
        ignoreFrustumCheck = true
    }

    override fun getName(): String {
        return "ATC2TransmitterEntity"
    }
}