package keiproductfamily.rtmAddons

import net.minecraft.client.gui.GuiButton

class EnumGuiButton<E : Enum<E>>(
    id: Int,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    displayString: String,
    val value: E
) : GuiButton(id, x, y, width, height, displayString) {
}
