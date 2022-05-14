package keiproductfamily.rtmAddons.tablet

import jp.kaiz.kaizpatch.util.KeyboardUtil
import jp.ngt.ngtlib.gui.GuiScreenCustom
import keiproductfamily.network.PacketHandler
import keiproductfamily.rtmAddons.GuiTextFieldWithID
import keiproductfamily.rtmAddons.atc2.ATC2Cli
import keiproductfamily.rtmAddons.formationNumber.FormationNumberCore
import keiproductfamily.rtmAddons.formationNumber.FormationNumberKeyPair
import keiproductfamily.rtmAddons.formationNumber.FormationNumberMessage
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.input.Keyboard
import kotlin.properties.Delegates

class RTMTabletGui(val player: EntityPlayer, val formationID: Long) : GuiScreenCustom() {
    private var buttonSignalVarVisible: GuiButton by Delegates.notNull()
    private var tfFormationNumber: Array<GuiTextFieldWithID> by Delegates.notNull()
    private var signalVarVisible: Boolean = false

    override fun initGui() {
        this.signalVarVisible = ATC2Cli.showGuiSel
        super.initGui()
        buttonList.clear()
        buttonList.add(GuiButton(0, width / 2 - 155, height - 28, 150, 20, I18n.format("gui.done", *arrayOfNulls(0))))
        buttonList.add(GuiButton(1, width / 2 + 5, height - 28, 150, 20, I18n.format("gui.cancel", *arrayOfNulls(0))))
        buttonSignalVarVisible = GuiButton(
            2, width / 2 + 20, 50, 100, 20, if (ATC2Cli.showGuiSel) {
                "Visible"
            } else {
                "Invisible"
            }
        )
        buttonList.add(buttonSignalVarVisible)

        val formationNumber = FormationNumberCore.getOrMake(formationID)

        tfFormationNumber = arrayOf(
            setTextFieldWithID(0, width / 2 + 20, 80, 20, 20, 1, formationNumber.name),
            setTextFieldWithID(1, width / 2 + 50, 80, 70, 20, 4, formationNumber.number)
        )
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.id == 0) {
            mc.displayGuiScreen(null as GuiScreen?)
            sendPacket()
        } else if (button.id == 1) {
            mc.displayGuiScreen(null as GuiScreen?)
        } else if (button.id == 2) {
            ATC2Cli.showGuiSel = !ATC2Cli.showGuiSel
            buttonSignalVarVisible.displayString = if (ATC2Cli.showGuiSel) {
                "Visible"
            } else {
                "Invisible"
            }
        }
        super.actionPerformed(button)
    }

    private fun sendPacket() {
        PacketHandler.sendPacketServer(
            FormationNumberMessage(
                formationID,
                FormationNumberKeyPair(tfFormationNumber[0].text, tfFormationNumber[1].text)
            )
        )
    }

    private fun getNextTextField(nowID: Int): GuiTextFieldWithID {
        return if (nowID and 1 == 0) {
            tfFormationNumber[1]
        } else {
            tfFormationNumber[0]
        }
    }

    override fun keyTyped(par1: Char, par2: Int) {
        if (par2 == 1) {
            mc.thePlayer.closeScreen()
        } else if (currentTextField in tfFormationNumber) {
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
                    if (!KeyboardUtil.isIntegerKey(par2) || par2 == 14) {
                        currentTextField.textboxKeyTyped(Character.toUpperCase(par1), par2)
//                        if (currentTextField.text.length > 1) {
//                            currentTextField.text = currentTextField.text.substring(0, 1)
//                        }
                    }
                } else {
                    if (KeyboardUtil.isIntegerKey(par2)) {
                        currentTextField.textboxKeyTyped(par1, par2)
//                        if (currentTextField.text.length > 4) {
//                            currentTextField.text = currentTextField.text.substring(0, 4)
//                        }
                    }
                }
            }
        }
    }

    override fun drawGuiContainerBackgroundLayer(p_146976_1_: Float, p_146976_2_: Int, p_146976_3_: Int) {
        if (FormationNumberCore.isUpdate) {
            this.initGui()
            FormationNumberCore.isUpdate = false
        }

        drawDefaultBackground()
        drawCenteredString(fontRendererObj, "Signal Light (This)", width / 2 - 70, 55, 16777215)
        drawCenteredString(fontRendererObj, " Name     -    Number", width / 2 - 70, 85, 16777215)
        drawCenteredString(fontRendererObj, "-", width / 2 + 45, 85, 16777215)

    }

    protected fun setTextFieldWithID(id: Int, xPos: Int, yPos: Int, w: Int, h: Int, maxStringLength: Int, text: String?): GuiTextFieldWithID {
        val field = GuiTextFieldWithID(id, fontRendererObj, xPos, yPos, w, h, this)
        field.maxStringLength = maxStringLength
        field.isFocused = false
        field.text = text
        textFields.add(field)
        return field
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }
}
