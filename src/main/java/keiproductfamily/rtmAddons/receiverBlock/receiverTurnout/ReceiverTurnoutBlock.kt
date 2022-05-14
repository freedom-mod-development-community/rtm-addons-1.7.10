package keiproductfamily.rtmAddons.receiverBlock.receiverTurnout

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import jp.ngt.rtm.electric.IBlockConnective
import keiproductfamily.GuiIDs
import keiproductfamily.ModKEIProductFamily
import keiproductfamily.PermissionList.IParmission
import keiproductfamily.getDirectional
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.IIcon
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import kotlin.properties.Delegates

class ReceiverTurnoutBlock : BlockContainer(Material.rock) , IBlockConnective, IParmission {
    init {
        unlocalizedName = "ReceiverTurnout"
        setTextureName(ModKEIProductFamily.DOMAIN + ":receiverturnout")
        setCreativeTab(ModKEIProductFamily.keipfCreativeTabs)
    }

    override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val tile = world.getTileEntity(x, y, z)
        if (tile is ReceiverTurnoutTile) {
            if(world.isRemote) {
                player.openGui(ModKEIProductFamily.MOD_ID, GuiIDs.GuiID_ReceiverTurnoutSetting, world, x, y, z)
            }
            return true
        }
        return false
    }

    override fun onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, entityliving: EntityLivingBase, itemstack: ItemStack) {
        val l = entityliving.getDirectional()
        world.setBlockMetadataWithNotify(x, y, z, l, 2)
    }

    override fun canProvidePower(): Boolean {
        return true
    }

    override fun isProvidingWeakPower(world: IBlockAccess, x: Int, y: Int, z: Int, direction: Int): Int {
        return isProvidingStrongPower(world, x, y, z, direction)
    }

    override fun isProvidingStrongPower(world: IBlockAccess, x: Int, y: Int, z: Int, direction: Int): Int {
        val tile = world.getTileEntity(x, y, z) as? ReceiverTurnoutTile ?: return 0
        return tile.getRSPower(direction)
    }

    override fun createNewTileEntity(p_149915_1_: World?, p_149915_2_: Int): TileEntity {
        return ReceiverTurnoutTile()
    }

    override fun canConnect(p0: World?, p1: Int, p2: Int, p3: Int): Boolean {
        return true
    }

    protected var blockIconFront: IIcon by Delegates.notNull()

    @SideOnly(Side.CLIENT)
    override fun registerIcons(p_149651_1_: IIconRegister) {
        blockIcon = p_149651_1_.registerIcon(ModKEIProductFamily.DOMAIN + ":receiverturnout")
        blockIconFront = p_149651_1_.registerIcon(ModKEIProductFamily.DOMAIN + ":receiverturnoutfront")
    }

    @SideOnly(Side.CLIENT)
    override fun getIcon(side: Int, meta: Int): IIcon {
        if(meta == 0 && side == 3){
            return blockIconFront
        }else if(meta == 1 && side == 4){
            return blockIconFront
        }else if(meta == 2 && side == 2){
            return blockIconFront
        }else if(meta == 3 && side == 5){
            return blockIconFront
        }
        return blockIcon
    }

    override fun getName(): String {
        return "ReceiverTurnout"
    }
}
