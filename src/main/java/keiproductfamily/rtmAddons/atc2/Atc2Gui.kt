package keiproductfamily.rtmAddons.atc2

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import jp.ngt.rtm.entity.train.EntityTrainBase
import jp.ngt.rtm.gui.GuiIngameCustom
import jp.ngt.rtm.gui.GuiTrainControlPanel
import jp.ngt.rtm.modelpack.cfg.TrainConfig
import jp.ngt.rtm.modelpack.modelset.ModelSetVehicleBase
import keiproductfamily.GLTool
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent

@SideOnly(Side.CLIENT)
object Atc2Gui : GuiScreen() {
    @SubscribeEvent
    fun onOpenGui(e: GuiOpenEvent) {
        if (e.gui is GuiTrainControlPanel) {

        }
    }

    val mc: Minecraft by lazy { Minecraft.getMinecraft() }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun onRenderGui(e: RenderGameOverlayEvent.Pre) {
        if (e.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
            if (mc == null) {
                mc = Minecraft.getMinecraft()
            }
            if (this.mc?.thePlayer?.isRiding != true || this.mc.gameSettings.thirdPersonView != 0) {
                return
            }

            this.setScale(e.resolution)

            if (this.mc.thePlayer.ridingEntity is EntityTrainBase) {
                this.renderTrainGui(this.mc.thePlayer.ridingEntity as EntityTrainBase)
            }
        }
    }


    private fun renderTrainGui(train: EntityTrainBase) {
        val fontrenderer = this.mc.fontRenderer
        val model = train.modelSet
        if (model != null && !model.config.notDisplayCab) {
            //this.mc.textureManager.bindTexture(GuiIngameCustom.tex_cab)
            val k = width / 2
            //drawTexturedModalRect(k - 208, height - 48, 0, 0, 416, 48)

            val y = height - 35

            renderSpeedIcon(fontrenderer, 0, k - 80, y, false)
            renderSpeedIcon(fontrenderer, 45, k - 35, y, false)
            renderSpeedIcon(fontrenderer, 75, k - 5, y, false)
            renderSpeedIcon(fontrenderer, 105, k + 25, y, false)
            renderSpeedIcon(fontrenderer, 130, k + 50, y, true)
            renderSpeedIcon(fontrenderer, 160, k + 80, y, false)
        }
    }

    private fun renderSpeedIcon(
        fontRenderer: FontRenderer,
        spd: Int,
        centerX: Int,
        centerY: Int,
        backLight: Boolean
    ) {
        if (backLight) {
            GLTool.drawCircle(centerX.toDouble(), centerY.toDouble(), 8.0, 255f, 176f, 129f, 255f, true)
        } else {
            GLTool.drawCircle(centerX.toDouble(), centerY.toDouble(), 7.0, 150f, 150f, 150f, 255f, true)
        }
        val color = if (spd == 0) {
            0xFF0000
        } else {
            0x505050
        }
        val str = if (spd == 0) {
            "☓"
        } else {
            spd.toString()
        }
        GLTool.drawString(
            fontRenderer,
            str,
            centerX - fontRenderer.getStringWidth(str) / 2.0,
            centerY - fontRenderer.FONT_HEIGHT / 2.0,
            color,
            false
        )
    }

    private fun setScale(par1: ScaledResolution) {
        width = par1.scaledWidth
        height = par1.scaledHeight
    }
}