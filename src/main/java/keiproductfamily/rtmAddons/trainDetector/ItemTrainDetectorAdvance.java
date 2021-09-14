package keiproductfamily.rtmAddons.trainDetector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jp.ngt.rtm.electric.MachineType;
import jp.ngt.rtm.entity.EntityInstalledObject;
import jp.ngt.rtm.item.ItemWithModel;
import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import jp.ngt.rtm.rail.util.RailMap;
import keiproductfamily.ModKEIProductFamily;
import keiproductfamily.PermissionList.IParmission;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;


public class ItemTrainDetectorAdvance extends Item implements IParmission {
    @SideOnly(Side.CLIENT)
    private IIcon icon;

    public ItemTrainDetectorAdvance() {
        this.setHasSubtypes(true);
        setUnlocalizedName("itemTrainDetectorAdvance");
        setTextureName(ModKEIProductFamily.DOMAIN + ":itemTrainDetectorAdvance");
        setCreativeTab(ModKEIProductFamily.keipfCreativeTabs);
    }

    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
        if (!world.isRemote) {
            Block block = null;
            if (par7 == 0) {
                --par5;
            } else if (par7 == 1) {
                ++par5;
            } else if (par7 == 2) {
                --par6;
            } else if (par7 == 3) {
                ++par6;
            } else if (par7 == 4) {
                --par4;
            } else if (par7 == 5) {
                ++par4;
            }

            if (!world.isAirBlock(par4, par5, par6)) {
                return true;
            }

            if (par7 == 1 && this.setEntityOnRail(world, new EntityTrainDetectorAdvance(world), par4, par5 - 1, par6, player, itemStack)) {
                block = Blocks.stone;
            }

            if (block != null) {
                world.playSoundEffect((double)par4 + 0.5D, (double)par5 + 0.5D, (double)par6 + 0.5D, block.stepSound.func_150496_b(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                --itemStack.stackSize;
            }
        }

        return true;
    }

    public boolean setEntityOnRail(World world, EntityTrainDetectorAdvance entity, int x, int y, int z, EntityPlayer player, ItemStack stack) {
        RailMap rm0 = TileEntityLargeRailBase.getRailMapFromCoordinates(world, null, x, y, z);
        if (rm0 == null) {
            return false;
        } else {
            int split = 128;
            int i0 = rm0.getNearlestPoint(split, (double)x + 0.5D, (double)z + 0.5D);
            double posX = rm0.getRailPos(split, i0)[1];
            double posY = rm0.getRailHeight(split, i0) + 0.0625D;
            double posZ = rm0.getRailPos(split, i0)[0];
            float yaw = rm0.getRailRotation(split, i0);
            float yaw2 = -player.rotationYaw + 180.0F;
            float dif = MathHelper.wrapAngleTo180_float(yaw - yaw2);
            if (Math.abs(dif) > 90.0F) {
                yaw += 180.0F;
            }

            entity.setPosition(posX, posY, posZ);
            entity.rotationYaw = yaw;
            entity.rotationPitch = 0.0F;
            world.spawnEntityInWorld(entity);
//            entity.setModelName(this.getModelName(stack));
//            entity.getResourceState().readFromNBT(this.getModelState(stack).writeToNBT());
            return true;
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1) {
        return this.icon;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.icon = register.registerIcon(ModKEIProductFamily.DOMAIN + ":itemTrainDetectorAdvance");
    }

    @Override
    public String getName() {
        return "TrainDetectorAdvance";
    }
}
