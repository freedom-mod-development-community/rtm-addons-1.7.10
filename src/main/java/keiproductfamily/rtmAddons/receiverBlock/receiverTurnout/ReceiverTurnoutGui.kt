package keiproductfamily.rtmAddons.receiverBlock.receiverTurnout

import jp.kaiz.kaizpatch.util.KeyboardUtil
import jp.ngt.ngtlib.gui.GuiScreenCustom
import jp.ngt.ngtlib.math.NGTMath
import jp.ngt.ngtlib.util.KeyboardUtil
import keiproductfamily.Util
import keiproductfamily.network.PacketHandler
import keiproductfamily.rtmAddons.*
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.resources.I18n
import org.lwjgl.input.Keyboard
import java.util.*
import kotlin.properties.Delegates

class ReceiverTurnoutGui(private val tile: ReceiverTurnoutTile) : GuiScreenCustom() {
    var thisTurnOutChannelKeyPair: ChannelKeyPair by Delegates.notNull()
    var detectorChannelKey: ChannelKeyPair by Delegates.notNull()
    var defaultTurnOutSelection: EnumTurnOutSwitch by Delegates.notNull()
    var turnOutLeftSelectRollIDs: BitSet by Delegates.notNull()
    var keepTurnOutSelectTime: Int by Delegates.notNull()

    var turnOutOperation: EnumTurnOutSyncSelection by Delegates.notNull()
    var turnOutSyncSelection: EnumTurnOutSwitch by Delegates.notNull()

    private var thisTurnOutChannelKeyTFs: Array<GuiTextFieldWithID> by Delegates.notNull()
    private var detectorChannelKeyTFs: Array<GuiTextFieldWithID> by Delegates.notNull()
    private var defaultTurnOutSelectButtons: Array<EnumGuiButton<EnumTurnOutSwitch>> by Delegates.notNull()
    private var turnOutSelectRollIDButtons: EnumMap<EnumTurnOutSwitch, Array<GuiButton>> by Delegates.notNull()
    private var keepTurnOutSelectTimeTFs: Array<GuiTextFieldWithID> by Delegates.notNull()

    override fun initGui() {
        this.thisTurnOutChannelKeyPair = tile.thisTurnOutChannelKeyPair
        this.detectorChannelKey = tile.detectorChannelKey
        this.defaultTurnOutSelection = tile.defaultTurnOutSelection
        this.turnOutLeftSelectRollIDs = tile.turnOutLeftSelectRollIDs

        this.turnOutOperation = tile.turnOutOperation
        this.turnOutSyncSelection = tile.turnOutSyncSelection
        this.keepTurnOutSelectTime = tile.keepTurnOutSelectTime

        super.initGui()
        buttonList.clear()
        buttonList.add(GuiButton(0, width / 2 - 155, height - 28, 150, 20, I18n.format("gui.done", *arrayOfNulls(0))))
        buttonList.add(GuiButton(1, width / 2 + 5, height - 28, 150, 20, I18n.format("gui.cancel", *arrayOfNulls(0))))

        defaultTurnOutSelectButtons = arrayOf(
            EnumGuiButton(10, width / 2 - 100, 90, 30, 20, "Left", EnumTurnOutSwitch.Left),
            EnumGuiButton(11, width / 2 - 50, 90, 30, 20, "Right", EnumTurnOutSwitch.Right)
        )
        buttonList.addAll(defaultTurnOutSelectButtons)
        setDefaultTurn(defaultTurnOutSelection)

        turnOutSelectRollIDButtons = EnumMap<EnumTurnOutSwitch, Array<GuiButton>>(
            mapOf(
                Pair(EnumTurnOutSwitch.Left, Array<GuiButton>(16) { i ->
                    GuiButton(20 + i, width / 2 - 140 + 20 * i, 145, 20, 20, i.toString())
                }),
                Pair(EnumTurnOutSwitch.Right, Array<GuiButton>(16) { i ->
                    GuiButton(40 + i, width / 2 - 140 + 20 * i, 170, 20, 20, i.toString())
                })
            )
        )
        buttonList.addAll(turnOutSelectRollIDButtons[EnumTurnOutSwitch.Left]!!)
        buttonList.addAll(turnOutSelectRollIDButtons[EnumTurnOutSwitch.Right]!!)
        syncRollIDButtons()

        textFields.clear()
        thisTurnOutChannelKeyTFs = arrayOf(
            setTextFieldWithID(0, width / 2 - 190, 40, 40, 15, this.thisTurnOutChannelKeyPair.name),
            setTextFieldWithID(1, width / 2 - 130, 40, 40, 15, this.thisTurnOutChannelKeyPair.number)
        )

        detectorChannelKeyTFs = arrayOf(
            setTextFieldWithID(10, width / 2 - 50, 40, 40, 15, this.detectorChannelKey.name),
            setTextFieldWithID(11, width / 2 + 10, 40, 40, 15, this.detectorChannelKey.number)
        )

        keepTurnOutSelectTimeTFs = arrayOf(
            setTextFieldWithID(20, width / 2 + 120, 40, 40, 15, this.keepTurnOutSelectTime.toString())
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
        } else if (defaultTurnOutSelectButtons.contains(button)) {
            setDefaultTurn((button as EnumGuiButton<EnumTurnOutSwitch>).value)
        } else if (button.id / 10 in 2..5) {
            val side = button.id / 10
            val id = button.id % 20
            if (side in 2..3) {
                //Left
                turnOutLeftSelectRollIDs[id] = true
                //Left
                button.enabled = false
                turnOutSelectRollIDButtons[EnumTurnOutSwitch.Right]?.get(id)?.enabled = true
            } else {
                //Right
                turnOutLeftSelectRollIDs[id] = false
                button.enabled = false
                turnOutSelectRollIDButtons[EnumTurnOutSwitch.Left]?.get(id)?.enabled = true
            }
        } else if (button.id == 10 || button.id == 11) {
            setDefaultTurn((button as EnumGuiButton<EnumTurnOutSwitch>).value)
        }
        super.actionPerformed(button)
    }

    fun setDefaultTurn(defaultSide: EnumTurnOutSwitch) {
        this.defaultTurnOutSelection = defaultSide
        for (button in defaultTurnOutSelectButtons) {
            button.enabled = button.value != this.defaultTurnOutSelection
        }
    }

    fun syncRollIDButtons() {
        for (id in 0..15) {
            val leftON = turnOutLeftSelectRollIDs[id]
            turnOutSelectRollIDButtons[EnumTurnOutSwitch.Left]?.get(id)?.enabled = !leftON
            turnOutSelectRollIDButtons[EnumTurnOutSwitch.Right]?.get(id)?.enabled = leftON
        }
    }

    private fun sendPacket() {
        this.thisTurnOutChannelKeyPair = ChannelKeyPair(
            thisTurnOutChannelKeyTFs[0].text,
            thisTurnOutChannelKeyTFs[1].text
        )
        this.detectorChannelKey = ChannelKeyPair(
            detectorChannelKeyTFs[0].text,
            detectorChannelKeyTFs[1].text
        )
        this.keepTurnOutSelectTime = keepTurnOutSelectTimeTFs[0].text.toInt()

        PacketHandler.sendPacketServer(
            ReceiverTurnoutMessage(
                tile,
                this.thisTurnOutChannelKeyPair,
                this.detectorChannelKey,
                this.defaultTurnOutSelection,
                this.turnOutLeftSelectRollIDs,
                this.keepTurnOutSelectTime
            )
        )
    }

    private fun formatSignalLevel() {
        var num = NGTMath.getIntFromString(thisTurnOutChannelKeyTFs[1].text, 0, 999, 0)
        thisTurnOutChannelKeyTFs[1].text = num.toString()
        num = NGTMath.getIntFromString(detectorChannelKeyTFs[1].text, 0, 999, 0)
        detectorChannelKeyTFs[1].text = num.toString()
    }

    private fun getNextTextField(nowID: Int): GuiTextFieldWithID {
        val cat = nowID / 10
        var num = nowID and 1
        num = (num + 1) and 1
        when (cat) {
            0 -> return thisTurnOutChannelKeyTFs[num]
            1 -> return detectorChannelKeyTFs[num]
        }
        throw IllegalArgumentException("ReceiverTurnoutGui getNextTextField. nowID:$nowID")
    }

    override fun keyTyped(par1: Char, par2: Int) {
        if (par2 == 1) {
            mc.thePlayer.closeScreen()
        } else if (currentTextField is GuiTextFieldWithID) {
            val id = (currentTextField as GuiTextFieldWithID).id
            val mode = id / 10
            val side = id and 1
            if (mode == 0 || mode == 1) {
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
            } else if (mode == 2) {
                if (KeyboardUtil.isIntegerKey(par2)) {
                    currentTextField.textboxKeyTyped(par1, par2)
                    if (currentTextField.text.length > 3) {
                        currentTextField.text = currentTextField.text.substring(0, 3)
                    }
                }
            }
        } else if (par2 == 28) {
            formatSignalLevel()
        } else if (par2 == mc.gameSettings.keyBindInventory.keyCode) {
            mc.thePlayer.closeScreen()
        }
    }

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        if (tile.isUpdate) {
            this.initGui()
            tile.isUpdate = false
        }
        drawDefaultBackground()
        super.drawScreen(par1, par2, par3)
        drawCenteredString(fontRendererObj, "This TurnOut Name", width / 2 - 140, 15, 16777215)
        drawCenteredString(fontRendererObj, " Name     -    Number", width / 2 - 140, 25, 16777215)
        drawCenteredString(fontRendererObj, "-", width / 2 - 140, 42, -1)

        drawCenteredString(fontRendererObj, "Roll ID Target Detector", width / 2 + 0, 15, -1)
        drawCenteredString(fontRendererObj, " Name     -    Number", width / 2 + 0, 25, -1)
        drawCenteredString(fontRendererObj, "-", width / 2 + 0, 42, -1)

        drawCenteredString(fontRendererObj, "keepTurnOutSelectTime[s]", width / 2 + 140, 25, -1)

        drawCenteredString(fontRendererObj, "ForceSelect", width / 2 + 60, 70, -1)
        drawCenteredString(
            fontRendererObj,
            "(Auto)",
            width / 2 + 60,
            85,
            if (this.turnOutOperation == EnumTurnOutSyncSelection.OFF) {
                -1
            } else {
                OFF_COLLAR
            }
        )
        drawCenteredString(
            fontRendererObj,
            "(Left)",
            width / 2 + 30,
            95,
            if (this.turnOutOperation == EnumTurnOutSyncSelection.Left) {
                -1
            } else {
                OFF_COLLAR
            }
        )
        drawCenteredString(
            fontRendererObj,
            "(Right)",
            width / 2 + 90,
            95,
            if (this.turnOutOperation == EnumTurnOutSyncSelection.Right) {
                -1
            } else {
                OFF_COLLAR
            }
        )

        drawCenteredString(fontRendererObj, "Default", width / 2 - 60, 70, -1)
        drawCenteredString(fontRendererObj, "(RedStone OFF)", width / 2 - 60, 80, -1)

        drawCenteredString(fontRendererObj, "Turn Out Roll ID", width / 2 - 140, 130, -1)
        drawCenteredString(fontRendererObj, "L", width / 2 - 150, 150, -1)
        drawCenteredString(fontRendererObj, "R", width / 2 - 150, 175, -1)
        val height = if (defaultTurnOutSelection == EnumTurnOutSwitch.Left) {
            150
        } else {
            175
        }
        drawCenteredString(fontRendererObj, "(Default)", width / 2 - 170, height, -1)
    }

    companion object {
        val ENABLED_COLLAR = Util.intCollar(255, 90, 90, 255)
        val OFF_COLLAR = Util.intCollar(110, 110, 110, 255)
    }
}