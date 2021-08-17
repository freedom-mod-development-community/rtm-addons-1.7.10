package keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights

import jp.ngt.rtm.electric.IBlockConnective
import keiproductfamily.GuiIDs
import keiproductfamily.ModKEIProductFamily
import keiproductfamily.PermissionList.IParmission
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

class ReceiverTrafficLightBlock() : BlockContainer(Material.rock) , IBlockConnective, IParmission {
    init {
        setBlockName("ReceiverTrafficLight")
        setBlockTextureName(ModKEIProductFamily.DOMAIN + ":receivertrafficlight")
        setCreativeTab(ModKEIProductFamily.keipfCreativeTabs)
    }

    override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val tile = world.getTileEntity(x, y, z)
        if (tile is ReceiverTrafficLightTile) {
            if(world.isRemote) {
                player.openGui(ModKEIProductFamily.MOD_ID, GuiIDs.GuiID_ReceiverTrafficLightSetting, world, x, y, z)
            }
            return true
        }
        return false
    }

    override fun createNewTileEntity(p_149915_1_: World?, p_149915_2_: Int): TileEntity {
        return ReceiverTrafficLightTile()
    }

    override fun canConnect(p0: World?, p1: Int, p2: Int, p3: Int): Boolean {
        return true
    }

    override fun getName(): String {
        return "ReceiverTrafficLight"
    }
}