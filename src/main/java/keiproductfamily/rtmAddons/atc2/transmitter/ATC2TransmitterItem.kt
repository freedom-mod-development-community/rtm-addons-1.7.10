package keiproductfamily.rtmAddons.atc2.transmitter

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import jp.ngt.rtm.rail.TileEntityLargeRailBase
import keiproductfamily.ModKEIProductFamily
import keiproductfamily.PermissionList.IParmission
import net.minecraft.block.Block
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import kotlin.math.abs

class ATC2TransmitterItem : Item(), IParmission {
    @SideOnly(Side.CLIENT)
    private var icon: IIcon? = null

    init {
        setHasSubtypes(true)
        unlocalizedName = "atc2TransmitterItem"
        setTextureName(ModKEIProductFamily.DOMAIN + ":itemATC2")
        creativeTab = ModKEIProductFamily.keipfCreativeTabs
    }

    override fun onItemUse(
        itemStack: ItemStack,
        player: EntityPlayer,
        world: World,
        par4: Int,
        par5: Int,
        par6: Int,
        par7: Int,
        par8: Float,
        par9: Float,
        par10: Float
    ): Boolean {
        var x = par4
        var y = par5
        var z = par6
        if (!world.isRemote) {
            var block: Block? = null
            if (par7 == 0) {
                --y
            } else if (par7 == 1) {
                ++y
            } else if (par7 == 2) {
                --z
            } else if (par7 == 3) {
                ++z
            } else if (par7 == 4) {
                --x
            } else if (par7 == 5) {
                ++x
            }
            if (!world.isAirBlock(x, y, z)) {
                return true
            }
            if (par7 == 1 && setEntityOnRail(
                    world,
                    ATC2TransmitterEntity(world),
                    x,
                    y - 1,
                    z,
                    player,
                    itemStack
                )
            ) {
                block = Blocks.stone
            }
            if (block != null) {
                world.playSoundEffect(
                    x.toDouble() + 0.5,
                    y.toDouble() + 0.5,
                    z.toDouble() + 0.5,
                    block.stepSound.placeSound,
                    (block.stepSound.getVolume() + 1.0f) / 2.0f,
                    block.stepSound.frequency * 0.8f
                )
                --itemStack.stackSize
            }
        }
        return true
    }

    fun setEntityOnRail(
        world: World,
        entity: ATC2TransmitterEntity,
        x: Int,
        y: Int,
        z: Int,
        player: EntityPlayer,
        stack: ItemStack?
    ): Boolean {
        val rm0 =
            TileEntityLargeRailBase.getRailMapFromCoordinates(world, null, x.toDouble(), y.toDouble(), z.toDouble())
        return if (rm0 == null) {
            false
        } else {
            val split = 128
            val i0 = rm0.getNearlestPoint(split, x.toDouble() + 0.5, z.toDouble() + 0.5)
            val posX = rm0.getRailPos(split, i0)[1]
            val posY = rm0.getRailHeight(split, i0) + 0.0625
            val posZ = rm0.getRailPos(split, i0)[0]
            var yaw = rm0.getRailRotation(split, i0)
            val yaw2 = -player.rotationYaw + 180.0f
            val dif = MathHelper.wrapAngleTo180_float(yaw - yaw2)
            if (abs(dif) > 90.0f) {
                yaw += 180.0f
            }
            entity.setPosition(posX, posY, posZ)
            entity.rotationYaw = yaw
            entity.rotationPitch = 0.0f
            world.spawnEntityInWorld(entity)
            true
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getIconFromDamage(par1: Int): IIcon? {
        return icon
    }

    @SideOnly(Side.CLIENT)
    override fun registerIcons(register: IIconRegister) {
        icon = register.registerIcon(ModKEIProductFamily.DOMAIN + ":itemATC2")
    }

    override fun getName(): String {
        return "atc2TransmitterItem"
    }
}
