package keiproductfamily;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabKEIPF extends CreativeTabs {
    public CreativeTabKEIPF() {
        super("Kuma Electric Industry Product Family");
    }

    @Override
    public Item getTabIconItem() {
        return ModKEIProductFamily.creativeTabIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel() {
        return "Kuma Electric Industry Product Family";
    }
}
