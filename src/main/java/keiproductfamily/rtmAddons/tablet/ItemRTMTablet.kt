package keiproductfamily.rtmAddons.tablet

import jp.ngt.rtm.entity.train.EntityBogie
import jp.ngt.rtm.entity.train.EntityTrainBase
import jp.ngt.rtm.entity.train.parts.EntityFloor
import keiproductfamily.GuiIDs
import keiproductfamily.ModKEIProductFamily
import keiproductfamily.PermissionList.IParmission
import keiproductfamily.rtmAddons.atc2.ATC2Cli
import keiproductfamily.rtmAddons.atc2.ATC2Core
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class ItemRTMTablet() : Item(), IParmission {
    init {
        unlocalizedName = "itemRTMTablet"
        setTextureName(ModKEIProductFamily.DOMAIN + ":itemRTMTablet")
        creativeTab = ModKEIProductFamily.keipfCreativeTabs
    }

    override fun onItemRightClick(itemStack: ItemStack, world: World, player: EntityPlayer): ItemStack {
        if (world.isRemote && player.ridingEntity is EntityTrainBase) {
            val formationID = ATC2Cli.formationID
            formationID.toUInt()
            val b0 = formationID shr 42
            val b10 = formationID and 0x3FFFFE00000
            val b11 = b10 shr 21
            val b12 = formationID and 0x3FFFFE00000 shr 21
            val b2 = formationID and 0x1FFFFF

            player.openGui(
                ModKEIProductFamily.instance, GuiIDs.GuiID_RTMTablet, world,
                (formationID shr 42).toInt(), (formationID and 0x3FFFFE00000 shr 21).toInt(), (formationID and 0x1FFFFF).toInt()
            )
        }
        return itemStack
    }

    override fun itemInteractionForEntity(
        itemStack: ItemStack,
        player: EntityPlayer,
        entity: EntityLivingBase
    ): Boolean {
        var train: EntityTrainBase? = null
        if (entity is EntityTrainBase) {
            train = entity
        } else if (entity is EntityBogie) {
            train = entity.train
        } else if (entity is EntityFloor) {
            val vehicle = (entity as EntityFloor).vehicle
            if (vehicle is EntityTrainBase) {
                train = vehicle
            }
        }

        return train != null
    }

    override fun getName(): String {
        return "itemRTMTablet"
    }
}