package keiproductfamily.rtmAddons.trainwarningradio.trainprotectionradio;

import keiproductfamily.ModKEIProductFamily;
import keiproductfamily.PermissionList.IParmission;
import keiproductfamily.rtmAddons.trainwarningradio.TAWTPRBaseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TPRSendItem extends TAWTPRBaseItem implements IParmission {
    public TPRSendItem() {
        super();
        setUnlocalizedName("TPRSendItem");
        setTextureName(ModKEIProductFamily.DOMAIN + ":TPRSend");
    }

    public void onUpdate(ItemStack itemStack, World p_77663_2_, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        if (itemStack.getItem() instanceof TPRSendItem && itemStack.hasTagCompound() && itemStack.getTagCompound().getBoolean("enabled") && entity instanceof EntityPlayer) {
            TPRMaster.resetLostCnt(TPRMaster.getID(itemStack), (EntityPlayer) entity);
        }
    }

    @Override
    public String getName() {
        return "TPRSendItem";
    }
}
