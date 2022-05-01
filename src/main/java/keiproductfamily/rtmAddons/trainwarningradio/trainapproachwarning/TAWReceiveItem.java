package keiproductfamily.rtmAddons.trainwarningradio.trainapproachwarning;

import keiproductfamily.ModKEIProductFamily;
import keiproductfamily.rtmAddons.trainwarningradio.TAWTPRBaseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TAWReceiveItem extends TAWTPRBaseItem {
    public TAWReceiveItem() {
        super();
        setUnlocalizedName("TAWReceiveItem");
        setTextureName(ModKEIProductFamily.DOMAIN + ":TAWReceive");
    }

    public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        NBTTagCompound nbt = itemStack.getTagCompound();
        if (!world.isRemote
                && itemStack.getItem() instanceof TAWReceiveItem
                && itemStack.hasTagCompound() && nbt.getBoolean("enabled")
                && entity instanceof EntityPlayer) {
            long lastTime = nbt.getLong("lastSoundTime");
            long nowTime = System.currentTimeMillis();
            boolean lastSoundIsTA = nbt.getBoolean("lastSoundIsTA");
            if (TAWMaster.checkReceive((EntityPlayer) entity)) {
                if ((nowTime - lastTime) / 1000 >= 5 || !lastSoundIsTA) {
                    world.playSoundEffect(entity.posX, entity.posY, entity.posZ, ModKEIProductFamily.DOMAIN + ":TrainApproach", 2.0f, 1.0f);
                    nbt.setLong("lastSoundTime", nowTime);
                    nbt.setBoolean("lastSoundIsTA", true);
                }
            } else {
                if ((nowTime - lastTime) / 1000 >= 3) {
                    world.playSoundEffect(entity.posX, entity.posY, entity.posZ, ModKEIProductFamily.DOMAIN + ":pee", 1.0f, 1.0f);
                    nbt.setLong("lastSoundTime", nowTime);
                    nbt.setBoolean("lastSoundIsTA", false);
                }
            }
        }
    }
}
