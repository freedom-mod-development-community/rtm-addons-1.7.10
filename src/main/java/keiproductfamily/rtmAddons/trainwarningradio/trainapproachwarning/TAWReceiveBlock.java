package keiproductfamily.rtmAddons.trainwarningradio.trainapproachwarning;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import keiproductfamily.ModKEIProductFamily;
import keiproductfamily.PermissionList.IParmission;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TAWReceiveBlock extends BlockContainer implements IParmission {
    public TAWReceiveBlock() {
        super(Material.iron);
        setBlockName("TAWReceiveBlock");
        setBlockTextureName(ModKEIProductFamily.DOMAIN + ":TAWReceiveBlock");
        setCreativeTab(ModKEIProductFamily.keipfCreativeTabs);
    }

    protected IIcon blockIconReceive;
    protected IIcon blockIconOn;
    protected IIcon blockIconOff;

    @Override
    public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        TileEntity tile = p_149727_1_.getTileEntity(p_149727_2_, p_149727_3_, p_149727_4_);
        if (tile instanceof TAWReceiveBlockTile) {
            if (!p_149727_1_.isRemote) {
                TAWReceiveBlockTile tawReceiveTile = (TAWReceiveBlockTile) tile;
                if (tawReceiveTile.clickCoolTime == 0) {
                    p_149727_5_.addChatMessage(new ChatComponentText("Now Power : " + tawReceiveTile.getPower()));
                } else {
                    boolean power = !tawReceiveTile.getPower();
                    tawReceiveTile.setPower(power);
                    p_149727_5_.addChatMessage(new ChatComponentText("Set to Power : " + tawReceiveTile.getPower()));
                }

                tawReceiveTile.clickCoolTime = 60;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TAWReceiveBlockTile) {
            if (((TAWReceiveBlockTile) tileEntity).isReceived()) {
                return ((TAWReceiveBlockTile) tileEntity).getReceiveLevel();
            } else {
                return 0;
            }
        }
        return 0;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        this.blockIconOn = p_149651_1_.registerIcon(ModKEIProductFamily.DOMAIN + ":TAWReceiveBlock_On");
        this.blockIconOff = p_149651_1_.registerIcon(ModKEIProductFamily.DOMAIN + ":TAWReceiveBlock_Off");
        this.blockIconReceive = p_149651_1_.registerIcon(ModKEIProductFamily.DOMAIN + ":TAWReceiveBlock_Receive");
        this.blockIcon = this.blockIconOff;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) {
            return blockIconOff;
        } else if (meta == 1) {
            return blockIconOn;
        } else if (meta == 2) {
            return blockIconReceive;
        }
        return blockIconOff;
    }

    @SideOnly(Side.CLIENT)
    public String getItemIconName() {
        return getTextureName();
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TAWReceiveBlockTile();
    }

    @Override
    public String getName() {
        return "TAWReceiveBlock";
    }
}
