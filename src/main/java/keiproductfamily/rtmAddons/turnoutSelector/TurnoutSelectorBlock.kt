package keiproductfamily.rtmAddons.turnoutSelector

import jp.ngt.rtm.RTMItem
import keiproductfamily.GuiIDs
import keiproductfamily.ModCommonVar
import keiproductfamily.ModKEIProductFamily
import keiproductfamily.PermissionList.IParmission
import keiproductfamily.getDirectional
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

class TurnoutSelectorBlock() : BlockContainer(Material.rock), IParmission {
    init {
        unlocalizedName = "TurnoutSelector"
        setTextureName(ModKEIProductFamily.DOMAIN + ":turnoutselector")
        setCreativeTab(ModKEIProductFamily.keipfCreativeTabs)
    }

    val itemBlockTurnoutSelector by lazy { Item.getItemFromBlock(ModCommonVar.turnoutSelectorBlock) }
    override fun onBlockActivated(
        world: World,
        x: Int,
        y: Int,
        z: Int,
        player: EntityPlayer,
        meta: Int,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        if (player.heldItem != null && (player.heldItem.item == RTMItem.crowbar || player.heldItem.item == itemBlockTurnoutSelector)) {
            if (world.isRemote) {
                player.openGui(ModKEIProductFamily.instance, GuiIDs.GuiID_TurnoutSelecterGui, world, x, y, z)
            }
            return true
        } else {
            val tile = world.getTileEntity(x, y, z)
            if (tile is TurnoutSelectorTile) {
                if(!tile.world.isRemote) {
                    tile.nextTurnOutSelection()
                }
                return true
            }
            return false
        }
    }

    override fun onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, entityliving: EntityLivingBase, itemstack: ItemStack) {
        val l = entityliving.getDirectional()
        world.setBlockMetadataWithNotify(x, y, z, l, 2)
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity {
        return TurnoutSelectorTile()
    }

    override fun renderAsNormalBlock(): Boolean {
        return false
    }

    override fun isOpaqueCube(): Boolean {
        return false
    }

    override fun getRenderType(): Int {
        return -1
    }

    override fun getName(): String {
        return "TurnoutSelector"
    }
}
