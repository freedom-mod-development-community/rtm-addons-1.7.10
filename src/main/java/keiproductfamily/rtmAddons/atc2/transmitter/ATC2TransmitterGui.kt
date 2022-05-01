package keiproductfamily.rtmAddons.atc2.transmitter

import jp.kaiz.kaizpatch.util.KeyboardUtil
import jp.ngt.ngtlib.gui.GuiScreenCustom
import jp.ngt.ngtlib.math.NGTMath
import keiproductfamily.network.PacketHandler
import keiproductfamily.rtmAddons.ChannelKeyPair
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.resources.I18n
import org.lwjgl.input.Keyboard
import kotlin.properties.Delegates

class ATC2TransmitterGui(private val entityATC2: ATC2TransmitterEntity) : GuiScreenCustom() {
    private var tfSignalL: Array<GuiTextField> by Delegates.notNull<Array<GuiTextField>>()
    private var tfTurnOut: Array<GuiTextField> by Delegates.notNull<Array<GuiTextField>>()
    private var tfSignalR: Array<GuiTextField> by Delegates.notNull<Array<GuiTextField>>()
    private var tfFormationRegex: GuiTextField by Delegates.notNull<GuiTextField>()

    override fun initGui() {
        super.initGui()
        buttonList.clear()
        buttonList.add(GuiButton(0, width / 2 - 155, height - 28, 150, 20, I18n.format("gui.done", *arrayOfNulls(0))))
        buttonList.add(GuiButton(1, width / 2 + 5, height - 28, 150, 20, I18n.format("gui.cancel", *arrayOfNulls(0))))
        val i0 = 0
        textFields.clear()
        tfSignalL = arrayOf(
            setTextField(width / 2 - 155, 100, 40, 20, entityATC2.signalChannelKeyPairL.name),
            setTextField(width / 2 - 105, 100, 40, 20, entityATC2.signalChannelKeyPairL.number)
        )
        tfTurnOut = arrayOf(
            setTextField(width / 2 - 45, 100, 40, 20, entityATC2.turnOutChannelKeyPair.name),
            setTextField(width / 2 + 5, 100, 40, 20, entityATC2.turnOutChannelKeyPair.number)
        )
        tfSignalR = arrayOf(
            setTextField(width / 2 + 65, 100, 40, 20, entityATC2.signalChannelKeyPairR.name),
            setTextField(width / 2 + 115, 100, 40, 20, entityATC2.signalChannelKeyPairR.number)
        )
        tfFormationRegex = setTextField(width / 2 - 10, 150, 60, 20, entityATC2.subjectFormationRegex)
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.id == 0) {
            mc.displayGuiScreen(null as GuiScreen?)
            sendPacket()
        } else if (button.id == 1) {
            mc.displayGuiScreen(null as GuiScreen?)
        }
        super.actionPerformed(button)
    }

    private fun sendPacket() {
        formatSignalLevel()
        PacketHandler.sendPacketServer(
            ATC2TransmitterMessage(
                entityATC2.entityId,
                ChannelKeyPair(tfSignalL[0].text, tfSignalL[1].text),
                ChannelKeyPair(tfTurnOut[0].text, tfTurnOut[1].text),
                ChannelKeyPair(tfSignalR[0].text, tfSignalR[1].text),
                tfFormationRegex.text
            )
        )
    }

    private fun formatSignalLevel() {
        val i0 = NGTMath.getIntFromString(tfSignalL[1].text, 0, 999, 0)
        tfSignalL[1].text = i0.toString()
    }

    override fun keyTyped(par1: Char, par2: Int) {
        if (par2 == 1) {
            mc.thePlayer.closeScreen()
        }
        if (currentTextField != null) {
            if (currentTextField === tfSignalL[0]) {
                if (par2 == Keyboard.KEY_TAB) {
                    currentTextField.isFocused = false
                    currentTextField = tfSignalL[1]
                    currentTextField.text = ""
                    currentTextField.isFocused = true
                    currentTextField.cursorPosition = 0
                } else if (!KeyboardUtil.isIntegerKey(par2) || par2 == 14) {
                    currentTextField.textboxKeyTyped(Character.toUpperCase(par1), par2)
                    if (currentTextField.text.length > 3) {
                        currentTextField.text = currentTextField.text.substring(0, 3)
                    }
                }
            } else if (currentTextField === tfSignalL[1] && KeyboardUtil.isIntegerKey(par2)) {
                currentTextField.textboxKeyTyped(par1, par2)
                if (currentTextField.text.length > 3) {
                    currentTextField.text = currentTextField.text.substring(0, 3)
                }
            } else if (currentTextField === tfTurnOut[0]) {
                if (par2 == Keyboard.KEY_TAB) {
                    currentTextField.isFocused = false
                    currentTextField = tfTurnOut[1]
                    currentTextField.text = ""
                    currentTextField.isFocused = true
                    currentTextField.cursorPosition = 0
                } else if (!KeyboardUtil.isIntegerKey(par2) || par2 == 14) {
                    currentTextField.textboxKeyTyped(Character.toUpperCase(par1), par2)
                    if (currentTextField.text.length > 3) {
                        currentTextField.text = currentTextField.text.substring(0, 3)
                    }
                }
            } else if (currentTextField === tfTurnOut[1] && KeyboardUtil.isIntegerKey(par2)) {
                currentTextField.textboxKeyTyped(par1, par2)
                if (currentTextField.text.length > 3) {
                    currentTextField.text = currentTextField.text.substring(0, 3)
                }
            } else if (currentTextField === tfSignalR[0]) {
                if (par2 == Keyboard.KEY_TAB) {
                    currentTextField.isFocused = false
                    currentTextField = tfSignalR[1]
                    currentTextField.text = ""
                    currentTextField.isFocused = true
                    currentTextField.cursorPosition = 0
                } else if (!KeyboardUtil.isIntegerKey(par2) || par2 == 14) {
                    currentTextField.textboxKeyTyped(Character.toUpperCase(par1), par2)
                    if (currentTextField.text.length > 3) {
                        currentTextField.text = currentTextField.text.substring(0, 3)
                    }
                }
            } else if (currentTextField === tfSignalR[1] && KeyboardUtil.isIntegerKey(par2)) {
                currentTextField.textboxKeyTyped(par1, par2)
                if (currentTextField.text.length > 3) {
                    currentTextField.text = currentTextField.text.substring(0, 3)
                }
            } else if (currentTextField === tfFormationRegex){
                currentTextField.textboxKeyTyped(Character.toUpperCase(par1), par2)
            }
        } else if (par2 == mc.gameSettings.keyBindInventory.keyCode) {
            mc.thePlayer.closeScreen()
        }
        if (par2 == 28) {
            formatSignalLevel()
        }
    }

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        drawDefaultBackground()
        super.drawScreen(par1, par2, par3)
        drawCenteredString(fontRendererObj, "Left Signal", width / 2 - 110, 75, 16777215)
        drawCenteredString(fontRendererObj, "Turn Out", width / 2, 75, 16777215)
        drawCenteredString(fontRendererObj, "Right Signal", width / 2 + 110, 75, 16777215)
        drawCenteredString(fontRendererObj, " Name - Number", width / 2 - 110, 85, 16777215)
        drawCenteredString(fontRendererObj, " Name - Number", width / 2, 85, 16777215)
        drawCenteredString(fontRendererObj, " Name - Number", width / 2 + 110, 85, 16777215)
        drawCenteredString(fontRendererObj, "-", width / 2 - 110, 105, 16777215)
        drawCenteredString(fontRendererObj, "-", width / 2, 105, 16777215)
        drawCenteredString(fontRendererObj, "-", width / 2 + 110, 105, 16777215)
        drawCenteredString(fontRendererObj, "Subject Formation Regex", width / 2 - 80, 145, 16777215)
        drawCenteredString(fontRendererObj, "ex( F-[0-9]*  : F-0000 )", width / 2 - 80, 160, 16777215)
        drawCenteredString(fontRendererObj, "ex( A-4[0-9]* : A-4000 )", width / 2 - 80, 170, 16777215)
    }
}