package keiproductfamily.rtmAddons.turnoutSelecter

import cpw.mods.fml.common.registry.GameRegistry
import jp.ngt.rtm.RTMItem
import keiproductfamily.GuiIDs
import keiproductfamily.ModCommonVar
import keiproductfamily.ModKEIProductFamily
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

class TurnoutSelecterBlock() : BlockContainer(Material.rock) {
    init {
        setBlockName("TurnoutSelecter")
        setBlockTextureName(ModKEIProductFamily.DOMAIN + ":turnoutselecter")
        setCreativeTab(ModKEIProductFamily.keipfCreativeTabs)
    }
    val itemBlockTurnoutSelecter by lazy { Item.getItemFromBlock(ModCommonVar.receiverTurnoutBlock) }
    override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, meta: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if(player.heldItem!=null && (player.heldItem.item == RTMItem.crowbar || player.heldItem.item == itemBlockTurnoutSelecter)){
            if(world.isRemote) {
                player.openGui(ModKEIProductFamily.instance, GuiIDs.GuiID_TurnoutSelecterGui, world, x, y, z)
            }
            return true
        }else{
            val tile = world.getTileEntity(x, y, z)
            if(tile is TurnoutSelecterTile) {
                tile.nextTurnOutSelection()
                return true
            }
            return false
        }
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity {
        return TurnoutSelecterTile()
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
}