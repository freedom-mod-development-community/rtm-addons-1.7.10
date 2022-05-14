package keiproductfamily.rtmAddons.scWirelessAdvance;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.electric.IBlockConnective;
import jp.ngt.rtm.electric.SignalConverterType;
import jp.ngt.rtm.electric.TileEntitySignalConverter;
import keiproductfamily.ModKEIProductFamily;
import keiproductfamily.PermissionList.IParmission;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;


public class BlockSCWirelessAdvance extends BlockContainer implements IBlockConnective, IParmission {
    @SideOnly(Side.CLIENT)
    private IIcon icon;

    public BlockSCWirelessAdvance() {
        super(Material.rock);
        setUnlocalizedName("SCWirelessAdvance");
        setCreativeTab(ModKEIProductFamily.keipfCreativeTabs);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World par1, int par2) {
        return new TileEntitySC_WirelessAdvance();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemstack) {
        world.setBlock(x, y, z, this, SignalConverterType.Wireless.id, 3);
    }

    @Override
    public boolean onBlockActivated(World world, int par2, int par3, int par4, EntityPlayer player, int par6, float par7, float par8, float par9) {
        int meta = world.getBlockMetadata(par2, par3, par4);
        if (meta != SignalConverterType.Increment.id && meta != SignalConverterType.Decrement.id) {
            if (world.isRemote) {
                player.openGui(RTMCore.instance, RTMCore.guiIdSignalConverter, player.worldObj, par2, par3, par4);
            }
        }
        return true;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int direction) {
        return this.isProvidingStrongPower(world, x, y, z, direction);
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int direction) {
        TileEntitySignalConverter tile = (TileEntitySignalConverter) world.getTileEntity(x, y, z);
        return tile.getRSOutput();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1, CreativeTabs tab, List list) {
        list.add(new ItemStack(par1, 1, SignalConverterType.Wireless.id));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2) {
        return this.icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.icon = register.registerIcon(ModKEIProductFamily.DOMAIN + ":SCWirelessAdvance");//wireless
    }

    @Override
    public boolean canConnect(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public String getName() {
        return "SCWirelessAdvance";
    }
}
