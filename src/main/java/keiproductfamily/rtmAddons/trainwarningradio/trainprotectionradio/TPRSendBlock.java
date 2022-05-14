package keiproductfamily.rtmAddons.trainwarningradio.trainprotectionradio;

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
import net.minecraft.world.World;

public class TPRSendBlock extends BlockContainer implements IParmission {
    public TPRSendBlock() {
        super(Material.iron);
        setUnlocalizedName("TPRSendBlock");
        setTextureName(ModKEIProductFamily.DOMAIN + ":TPRSendBlock");
        setCreativeTab(ModKEIProductFamily.keipfCreativeTabs);
    }

    protected IIcon blockIconSend;
    protected IIcon blockIconOn;
    protected IIcon blockIconOff;

    @Override
    public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        TileEntity tile = p_149727_1_.getTileEntity(p_149727_2_, p_149727_3_, p_149727_4_);
        if (tile instanceof TPRSendBlockTile) {
            if (!p_149727_1_.isRemote) {
                TPRSendBlockTile tawReceiveTile = (TPRSendBlockTile) tile;
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
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister p_149651_1_) {
        this.blockIconOn = p_149651_1_.registerIcon(ModKEIProductFamily.DOMAIN + ":TPRSendBlock_On");
        this.blockIconOff = p_149651_1_.registerIcon(ModKEIProductFamily.DOMAIN + ":TPRSendBlock_Off");
        this.blockIconSend = p_149651_1_.registerIcon(ModKEIProductFamily.DOMAIN + ":TPRSendBlock_Send");
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
            return blockIconSend;
        }
        return blockIconOff;
    }

    @SideOnly(Side.CLIENT)
    public String getItemIconName() {
        return getTextureName();
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TPRSendBlockTile();
    }

    @Override
    public String getName() {
        return "TPRSendBlock";
    }
}
