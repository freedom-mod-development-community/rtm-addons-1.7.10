package keiproductfamily.rtmAddons.trainwarningradio;

import keiproductfamily.ModKEIProductFamily;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.world.World;

public abstract class TAWTPRBaseItem extends Item {
    public TAWTPRBaseItem() {
        setCreativeTab(ModKEIProductFamily.keipfCreativeTabs);
    }

    public ItemStack onItemRightClick(ItemStack itemStack, World p_77659_2_, EntityPlayer p_77659_3_) {
        boolean enabled = false;
        if (itemStack.hasTagCompound()) {
            enabled = itemStack.getTagCompound().getBoolean("enabled");
        }
        enabled = !enabled;
        itemStack.setTagInfo("enabled", new NBTTagByte((byte) (enabled ? 1 : 0)));
        if (enabled) {
            itemStack.addEnchantment(Enchantment.protection, 0);
        } else {
            itemStack.getTagCompound().removeTag("ench");
        }
        return itemStack;
    }

    public boolean onDroppedByPlayer(ItemStack itemStack, EntityPlayer player) {
        boolean enabled = false;
        itemStack.setTagInfo("enabled", new NBTTagByte((byte) (enabled ? 1 : 0)));
        if (enabled) {
            itemStack.addEnchantment(Enchantment.protection, 0);
        } else {
            itemStack.getTagCompound().removeTag("ench");
        }
        return true;
    }

    abstract public void onUpdate(ItemStack itemStack, World p_77663_2_, Entity entity, int p_77663_4_, boolean p_77663_5_);
}
