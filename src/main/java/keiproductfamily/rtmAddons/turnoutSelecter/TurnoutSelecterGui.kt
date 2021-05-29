package keiproductfamily.rtmAddons.turnoutSelecter

import jp.ngt.ngtlib.gui.GuiScreenCustom
import jp.ngt.ngtlib.math.NGTMath
import jp.ngt.ngtlib.util.KeyboardUtil
import keiproductfamily.Util
import keiproductfamily.network.PacketHandler
import keiproductfamily.rtmAddons.*
import keiproductfamily.rtmAddons.receiverBlock.receiverTurnout.ReceiverTurnoutMessage
import keiproductfamily.rtmAddons.receiverBlock.receiverTurnout.ReceiverTurnoutTile
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.resources.I18n
import org.lwjgl.input.Keyboard
import java.lang.IllegalArgumentException
import kotlin.properties.Delegates

class TurnoutSelecterGui(private val tile: TurnoutSelecterTile) : GuiScreenCustom() {
    var turnoutChannelKeyPair: ChannelKeyPair by Delegates.notNull()

    private var thisTurnOutChannelKeyTFs: Array<GuiTextFieldWithID> by Delegates.notNull()

    override fun initGui() {
        this.turnoutChannelKeyPair = tile.turnoutChannelKeyPair

        super.initGui()
        buttonList.clear()
        buttonList.add(GuiButton(0, width / 2 - 155, height - 28, 150, 20, I18n.format("gui.done", *arrayOfNulls(0))))
        buttonList.add(GuiButton(1, width / 2 + 5, height - 28, 150, 20, I18n.format("gui.cancel", *arrayOfNulls(0))))

        textFields.clear()
        thisTurnOutChannelKeyTFs = arrayOf(
            setTextFieldWithID(0, width / 2 - 120, 40, 40, 15, this.turnoutChannelKeyPair.name),
            setTextFieldWithID(1, width / 2 - 60, 40, 40, 15, this.turnoutChannelKeyPair.number)
        )
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
        this.turnoutChannelKeyPair = ChannelKeyPair(
            thisTurnOutChannelKeyTFs[0].text,
            thisTurnOutChannelKeyTFs[1].text
        )

        PacketHandler.sendPacketServer(
            TurnoutSelecterMessage(
                tile,
                this.turnoutChannelKeyPair
            )
        )
    }

    private fun formatSignalLevel() {
        var num = NGTMath.getIntFromString(thisTurnOutChannelKeyTFs[1].text, 0, 999, 0)
        thisTurnOutChannelKeyTFs[1].text = num.toString()
    }

    private fun getNextTextField(nowID: Int): GuiTextFieldWithID {
        val cat = nowID / 10
        var num = nowID and 1
        num = (num + 1) and 1
        when (cat) {
            0 -> return thisTurnOutChannelKeyTFs[num]
        }
        throw IllegalArgumentException("ReceiverTurnoutGui getNextTextField. nowID:$nowID")
    }

    override fun keyTyped(par1: Char, par2: Int) {
        if (par2 == 1 || par2 == mc.gameSettings.keyBindInventory.keyCode) {
            mc.thePlayer.closeScreen()
        } else if (currentTextField is GuiTextFieldWithID) {
            val id = (currentTextField as GuiTextFieldWithID).id
            val side = id and 1
            if (par2 == Keyboard.KEY_TAB) {
                currentTextField.isFocused = false
                currentTextField = getNextTextField(id)
                currentTextField.text = ""
                currentTextField.isFocused = true
                currentTextField.cursorPosition = 0
            } else {
                if (side == 0) {
                    if (!KeyboardUtil.isIntegerKey(par2) || par2 == Keyboard.KEY_BACK) {
                        currentTextField.textboxKeyTyped(Character.toUpperCase(par1), par2)
                        if (currentTextField.text.length > 3) {
                            currentTextField.text = currentTextField.text.substring(0, 3)
                        }
                    }
                } else {
                    if (KeyboardUtil.isIntegerKey(par2)) {
                        currentTextField.textboxKeyTyped(par1, par2)
                        if (currentTextField.text.length > 3) {
                            currentTextField.text = currentTextField.text.substring(0, 3)
                        }
                    }
                }
            }
        } else if (par2 == 28) {
            formatSignalLevel()
        }
    }

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        if (tile.isUpdate) {
            this.initGui()
            tile.isUpdate = false
        }
        drawDefaultBackground()
        super.drawScreen(par1, par2, par3)
        drawCenteredString(fontRendererObj, "TurnOut Name", width / 2 - 70, 15, 16777215)
        drawCenteredString(fontRendererObj, " Name     -    Number", width / 2 - 70, 25, 16777215)
        drawCenteredString(fontRendererObj, "-", width / 2 - 70, 42, -1)
    }

    companion object{
        val ENABLED_COLLAR = Util.intCollar(255, 90, 90, 255)
        val OFF_COLLAR = Util.intCollar(110, 110, 110, 255)
    }
}