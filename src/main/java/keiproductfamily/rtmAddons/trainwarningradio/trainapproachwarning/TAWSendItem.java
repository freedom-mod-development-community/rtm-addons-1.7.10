package keiproductfamily.rtmAddons.trainwarningradio.trainapproachwarning;

import keiproductfamily.ModKEIProductFamily;
import keiproductfamily.PermissionList.IParmission;
import keiproductfamily.rtmAddons.trainwarningradio.TAWTPRBaseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TAWSendItem extends TAWTPRBaseItem implements IParmission {
    public TAWSendItem() {
        super();
        setUnlocalizedName("TAWSendItem");
        setTextureName(ModKEIProductFamily.DOMAIN + ":TAWSend");
    }

    public void onUpdate(ItemStack itemStack, World p_77663_2_, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        if (itemStack.getItem() instanceof TAWSendItem && itemStack.hasTagCompound() && itemStack.getTagCompound().getBoolean("enabled") && entity instanceof EntityPlayer) {
            TAWMaster.resetLostCnt(TAWMaster.getID(itemStack), (EntityPlayer) entity);
        }
    }

    @Override
    public String getName() {
        return "TAWSendItem";
    }
}
