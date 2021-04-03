package keiproductfamily.rtmAddons.trainDetector

import jp.ngt.rtm.electric.EntityElectricalWiring
import jp.ngt.rtm.electric.SignalLevel
import jp.ngt.rtm.entity.train.EntityBogie
import jp.ngt.rtm.entity.train.EntityTrainBase
import jp.ngt.rtm.item.ItemWire
import keiproductfamily.GuiIDs
import keiproductfamily.ModCommonVar
import keiproductfamily.ModKEIProductFamily
import keiproductfamily.network.PacketHandler
import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.RTMAChannelMaster.getChannelData
import keiproductfamily.rtmAddons.RequestEntityNBTData
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.World
import java.nio.channels.Channel

class EntityTrainDetectorAdvance(world: World?) : EntityElectricalWiring(world) {
    private var findTrain = false
    var channelKeyPair: ChannelKeyPair = ChannelKeyPair("", 0)

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
        nbt.setString("channelName", channelKeyPair.name)
        nbt.setInteger("channelNumber", channelKeyPair.number)
    }

    public override fun readEntityFromNBT(nbt: NBTTagCompound) {
        channelKeyPair = ChannelKeyPair(nbt.getString("channelName"), nbt.getInteger("channelNumber"))
        super.readEntityFromNBT(nbt)
    }

    val channelKey: String
        get() = channelKeyPair.getKey()

    fun setChunnelName(chunnelName: String, channelNumber: Int) {
        var chunnelName = chunnelName
        chunnelName = chunnelName.trim { it <= ' ' }
        if (channelKeyPair.name != chunnelName || channelKeyPair.number != channelNumber) {
            if (chunnelName != "") {
                channelKeyPair = ChannelKeyPair(chunnelName, channelNumber)
            }
        }
    }

    override fun onUpdate() {
        if (!worldObj.isRemote) {
            if (channelKeyPair.number > 0) {
                val channelData = getChannelData(channelKey)
                if (channelData != null) {
                    val train = collidedTrain
                    if (train != null) {
                        val rollSignID = train.getTrainStateData(8)
                        channelData.setTrainData(ModCommonVar.findTrainLevel, rollSignID)
                    } else {
                        channelData.setTrainData(ModCommonVar.notfindTrainLevel, (-1).toByte())
                    }
                }
            }
        }
        super.onUpdate()
    }

    override fun getBoundingBox(): AxisAlignedBB {
        return boundingBox
    }

    val collidedTrain: EntityTrainBase?
        get() {
            if(worldObj == null){
                return null
            }
            val collideEntityList =
                worldObj.getEntitiesWithinAABBExcludingEntity(this, getBoundingBox().expand(0.0, 2.0, 0.0))
            if (collideEntityList != null) {
                for (entity in collideEntityList) {
                    if (entity is EntityTrainBase) {
                        findTrain = true
                        return entity.formation[0]?.train
                    }
                    if (entity is EntityBogie) {
                        findTrain = true
                        return entity.train?.formation?.get(0)?.train
                    }
                }
            }
            findTrain = false
            return null
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
}