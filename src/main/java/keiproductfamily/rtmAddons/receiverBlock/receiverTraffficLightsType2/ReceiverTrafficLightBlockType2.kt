package keiproductfamily.rtmAddons.receiverBlock.receiverTraffficLightsType2

import jp.ngt.rtm.electric.IBlockConnective
import keiproductfamily.GuiIDs
import keiproductfamily.ModCommonVar
import keiproductfamily.ModKEIProductFamily
import keiproductfamily.PermissionList.IParmission
import keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights.ReceiverTrafficLightBlock
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

class ReceiverTrafficLightBlockType2 : BlockContainer(Material.rock) , IBlockConnective, IParmission {
    init {
        setBlockName("ReceiverTrafficLightType2")
        setBlockTextureName(ModKEIProductFamily.DOMAIN + ":receivertrafficlighttype2")
        setCreativeTab(ModKEIProductFamily.keipfCreativeTabs)
    }

    override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val tile = world.getTileEntity(x, y, z)
        if (tile is ReceiverTrafficLightTileType2) {
            val item = player.heldItem?.item as? ItemBlock
            if(item != null && item.field_150939_a is ReceiverTrafficLightBlock){
                val nbt = NBTTagCompound()
                world.getTileEntity(x, y, z).writeToNBT(nbt)
                world.setBlock(x, y, z, ModCommonVar.receiverTrafficLightBlock)
                val tileEntity = ModCommonVar.receiverTrafficLightBlock.createTileEntity(world, 0)
                world.setTileEntity(x, y, z, tileEntity)
                tileEntity.readFromNBT(nbt)
            } else {
                if (world.isRemote) {
                    player.openGui(ModKEIProductFamily.MOD_ID, GuiIDs.GuiID_ReceiverTrafficLightSettingType2, world, x, y, z)
                }
            }
            return true
        }
        return false
    }

    override fun createNewTileEntity(p_149915_1_: World?, p_149915_2_: Int): TileEntity {
        return ReceiverTrafficLightTileType2()
    }

    override fun canConnect(p0: World?, p1: Int, p2: Int, p3: Int): Boolean {
        return true
    }

    override fun getName(): String {
        return "ReceiverTrafficLightType2"
    }
}