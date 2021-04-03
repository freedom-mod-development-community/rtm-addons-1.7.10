package keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights

import jp.ngt.ngtlib.gui.GuiScreenCustom
import jp.ngt.ngtlib.math.NGTMath
import jp.ngt.ngtlib.util.KeyboardUtil
import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.network.PacketHandler
import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.GuiTextFieldWithID
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.resources.I18n
import org.lwjgl.input.Keyboard
import kotlin.properties.Delegates

class ReceiverTrafficLightGui(private val tile: ReceiverTrafficLightTile) : GuiScreenCustom() {
    private var signalValues: Array<Array<GuiTextField>> by Delegates.notNull()

    override fun initGui() {
        super.initGui()
        buttonList.clear()
        buttonList.add(GuiButton(0, width / 2 - 155, height - 28, 150, 20, I18n.format("gui.done", *arrayOfNulls(0))))
        buttonList.add(GuiButton(1, width / 2 + 5, height - 28, 150, 20, I18n.format("gui.cancel", *arrayOfNulls(0))))
        val i0 = 0
        textFields.clear()
        signalValues = Array<Array<GuiTextField>>(6) { i ->
            val (first, second) = tile.channelKeys[i]
            arrayOf(
                setTextFieldWithID(i * 10, width / 2 - 120, 30 + 20 * i, 40, 15, first),
                setTextFieldWithID(i * 10 + 1, width / 2 - 60 + i0, 30 + 20 * i, 40, 15, second.toString())
            )
        }
    }

    override fun onTextFieldClicked(field: GuiTextField) {}
    protected fun setTextFieldWithID(id: Int, xPos: Int, yPos: Int, w: Int, h: Int, text: String?): GuiTextFieldWithID {
        val field = GuiTextFieldWithID(id, fontRendererObj, xPos, yPos, w, h)
        field.maxStringLength = 32767
        field.isFocused = false
        field.text = text
        textFields.add(field)
        return field
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
        val channelKeys = Array<ChannelKeyPair>(6) { i ->
            ChannelKeyPair(
                signalValues[i][0].text,
                Integer.valueOf(signalValues[i][1].text)
            )
        }
        PacketHandler.sendPacketServer(ReceiverTrafficLightMessage(tile, channelKeys))
    }

    private fun formatSignalLevel() {
        for (i in 0 until 6) {
            val i0 = NGTMath.getIntFromString(signalValues[i][1].text, 0, 999, 0)
            signalValues[i][1].text = i0.toString()
        }
    }

    override fun keyTyped(par1: Char, par2: Int) {
        if (par2 == 1 || par2 == mc.gameSettings.keyBindInventory.keyCode) {
            mc.thePlayer.closeScreen()
        }
        if (currentTextField is GuiTextFieldWithID) {
            val id = (currentTextField as GuiTextFieldWithID).id
            var i = id / 10
            val side = id and 1
            if (side == 0) {
                if (par2 == Keyboard.KEY_TAB) {
                    currentTextField.isFocused = false
                    currentTextField = signalValues[i][1]
                    currentTextField.text = ""
                    currentTextField.isFocused = true
                    currentTextField.cursorPosition = 0
                } else {
                    currentTextField.textboxKeyTyped(Character.toUpperCase(par1), par2)
                    if (currentTextField.text.length > 3) {
                        currentTextField.text = currentTextField.text.substring(0, 3)
                    }
                }
            } else {
                if (par2 == Keyboard.KEY_TAB) {
                    i++
                    if (i >= signalValues.size) {
                        i = 0
                    }
                    currentTextField.isFocused = false
                    currentTextField = signalValues[i][0]
                    currentTextField.text = ""
                    currentTextField.isFocused = true
                    currentTextField.cursorPosition = 0
                } else if (KeyboardUtil.isIntegerKey(par2)) {
                    currentTextField.textboxKeyTyped(par1, par2)
                    if (currentTextField.text.length > 3) {
                        currentTextField.text = currentTextField.text.substring(0, 3)
                    }
                }
            }
        }
        if (par2 == 28) {
            formatSignalLevel()
        }
    }

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        drawDefaultBackground()
        super.drawScreen(par1, par2, par3)
        drawCenteredString(fontRendererObj, " Channel Name  -  Channel Number", width / 2 - 70, 15, 16777215)
        for (i in 0 until 6) {
            drawString(
                fontRendererObj,
                (i + 1).toString() + ":" + I18n.format(SignalLevel.getSignal(i + 1).name),
                width / 2 - 200,
                34 + i * 20,
                16777215
            )
        }
        for (i in 0 until 6) {
            drawCenteredString(fontRendererObj, "-", width / 2 - 70, 34 + i * 20, 16777215)
        }
    }
}