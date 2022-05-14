package keiproductfamily.rtmAddons.atc2

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import jp.ngt.rtm.entity.train.EntityTrainBase
import jp.ngt.rtm.gui.GuiTrainControlPanel
import keiproductfamily.GLTool
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent

@SideOnly(Side.CLIENT)
object Atc2Gui : GuiScreen() {
    val mc: Minecraft by lazy { Minecraft.getMinecraft() }
    var atsAssignButton: GuiButton? = null

    fun init() {
        buttonList.clear()
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun onRenderGui(e: RenderGameOverlayEvent.Pre) {
        if (ATC2Cli.showGuiSel || ATC2Cli.showGuiForce) {
            if (e.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
                if (mc == null) {
                    mc = Minecraft.getMinecraft()
                    init()
                }
                if (this.mc?.thePlayer?.isRiding != true || this.mc.gameSettings.thirdPersonView != 0) {
                    return
                }

                this.setScale(e.resolution)

                if (this.mc.thePlayer.ridingEntity is EntityTrainBase) {
                    this.renderTrainGui(this.mc.thePlayer.ridingEntity as EntityTrainBase)
                }

                var k: Int
                k = 0
//                while (k < buttonList.size) {
//                    (buttonList[k] as GuiButton).drawButton(this.mc, Mouse.getX(), Mouse.getY())
//                    ++k
//                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun onInitGui(e: GuiScreenEvent.InitGuiEvent.Post) {
        if(e.gui is GuiTrainControlPanel) {
            atsAssignButton = GuiButton(1000, width / 2 + 100, height - 90, 20, 20, "A")
            e.buttonList.add(atsAssignButton)
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun onActionPerformed(e: GuiScreenEvent.ActionPerformedEvent){
        if(atsAssignButton != null && e.button == atsAssignButton){
            val player = e.gui.mc.thePlayer
            val train = if (player.isRiding && player.ridingEntity is EntityTrainBase) player.ridingEntity as EntityTrainBase else null
            if (train != null) {
                val signal: Int = train.signal
                if (signal == 1) {
                    train.setSignal2(-1)
                } else if (signal == -1 && train.notch == -8) {
                    train.setSignal2(0)
                }
            }
        }
    }

    override fun mouseClicked(p_73864_1_: Int, p_73864_2_: Int, p_73864_3_: Int) {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_)
    }

    private fun renderTrainGui(train: EntityTrainBase) {
        val fontrenderer = this.mc.fontRendererObj
        val model = train.modelSet
        if (model != null && !model.config.notDisplayCab) {
            val k = width / 2
            val y = height - 35
            val speedSet = ATC2Core.speedSet
            speedSet.sort()
            val scale = 160 / (speedSet[speedSet.size - 1] - speedSet[0])

            for ((index, speed) in speedSet.withIndex()) {
                val signal = index + 1
                val formationID = ATC2Cli.formationID
                val backLight = formationID > 0 && ATC2Cli.nowSignal == signal
                renderSpeedIcon(fontrenderer, speed, k - 80 + speed * scale, y, backLight)
            }
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
        if (width != par1.scaledWidth || height != par1.scaledHeight) {
            width = par1.scaledWidth
            height = par1.scaledHeight
            init()
        }
    }
}
