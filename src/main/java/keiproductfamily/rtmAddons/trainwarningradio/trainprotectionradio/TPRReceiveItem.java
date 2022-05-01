package keiproductfamily.rtmAddons.trainwarningradio.trainprotectionradio;

import keiproductfamily.ModKEIProductFamily;
import keiproductfamily.PermissionList.IParmission;
import keiproductfamily.rtmAddons.trainwarningradio.TAWTPRBaseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TPRReceiveItem extends TAWTPRBaseItem implements IParmission {
    public TPRReceiveItem() {
        super();
        setUnlocalizedName("TPRReceiveItem");
        setTextureName(ModKEIProductFamily.DOMAIN + ":TPRReceive");
    }

    public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        if (!world.isRemote
                && itemStack.getItem() instanceof TPRReceiveItem
                && itemStack.hasTagCompound() && itemStack.getTagCompound().getBoolean("enabled")
                && entity instanceof EntityPlayer) {
            long lastTime = itemStack.getTagCompound().getLong("lastSoundTime");
            long nowTime = System.currentTimeMillis();
            if (TPRMaster.checkReceive((EntityPlayer) entity)) {
                if ((nowTime - lastTime)  >= 315) {
                    world.playSoundEffect(entity.posX, entity.posY, entity.posZ,ModKEIProductFamily.DOMAIN + ":pee_shot", 2.0f, 1.0f);
                    itemStack.getTagCompound().setLong("lastSoundTime", nowTime);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "TPRReceiveItem";
    }
}
