package keiproductfamily.rtmAddons.receiverBlock.receiverTraffficLightsType2

import jp.kaiz.kaizpatch.util.KeyboardUtil
import jp.ngt.ngtlib.gui.GuiScreenCustom
import jp.ngt.ngtlib.math.NGTMath
import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.network.PacketHandler
import keiproductfamily.rtmAddons.*
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.resources.I18n
import org.lwjgl.input.Keyboard
import kotlin.properties.Delegates

class ReceiverTrafficLightGuiType2(private val tile: ReceiverTrafficLightTileType2) : GuiScreenCustom() {
    var detectorChannelKeys: Array<ChannelKeyPair> by Delegates.notNull()
    var forcedSignalSelection: EnumForcedSignalMode by Delegates.notNull()
    var turnOutSyncSelection: EnumTurnOutSyncSelection by Delegates.notNull()
    var turnOutSyncSelection2: EnumTurnOutSyncSelection by Delegates.notNull()
    var turnOutChannelKeyPair: ChannelKeyPair by Delegates.notNull()
    var turnOutChannelKeyPair2: ChannelKeyPair by Delegates.notNull()
    var forceSelectSignal: SignalLevel by Delegates.notNull()

    private var signalChannelKeys: Array<Array<GuiTextFieldWithID>> by Delegates.notNull()
    private var turnoutChannelKeys: Array<GuiTextFieldWithID> by Delegates.notNull()
    private var turnoutChannelKeys2: Array<GuiTextFieldWithID> by Delegates.notNull()
    private var forceModeButtons: Array<EnumGuiButton<EnumForcedSignalMode>> by Delegates.notNull()
    private var turnOutSyncButtons: Array<EnumGuiButton<EnumTurnOutSyncSelection>> by Delegates.notNull()
    private var turnOutSyncButtonsType2: Array<EnumGuiButton<EnumTurnOutSyncSelection>> by Delegates.notNull()
    private var forceSelectSignalButtons: Array<EnumGuiButton<SignalLevel>> by Delegates.notNull()

    override fun initGui() {
        this.detectorChannelKeys = tile.detectorChannelKeys
        this.forcedSignalSelection = tile.forcedSignalSelection
        this.turnOutSyncSelection = tile.turnOutSyncSelection
        this.turnOutSyncSelection2 = tile.turnOutSyncSelection2
        this.turnOutChannelKeyPair = tile.turnOutChannelKeyPair
        this.turnOutChannelKeyPair2 = tile.turnOutChannelKeyPair2
        this.forceSelectSignal = tile.forceSelectSignal

        super.initGui()
        buttonList.clear()
        buttonList.add(GuiButton(0, width / 2 - 155, height - 28, 150, 20, I18n.format("gui.done", *arrayOfNulls(0))))
        buttonList.add(GuiButton(1, width / 2 + 5, height - 28, 150, 20, I18n.format("gui.cancel", *arrayOfNulls(0))))

        forceModeButtons = arrayOf(
            EnumGuiButton(10, width / 2 - 50, height - 70, 50, 20, "Auto", EnumForcedSignalMode.Auto),
            EnumGuiButton(10, width / 2 + 115, height - 70, 50, 20, "ForceSelect", EnumForcedSignalMode.ForceSelect)
        )
        buttonList.addAll(forceModeButtons)

        turnOutSyncButtons = arrayOf(
            EnumGuiButton(20, width / 2 + 10, 55, 20, 20, "L", EnumTurnOutSyncSelection.Left),
            EnumGuiButton(20, width / 2 + 50, 55, 20, 20, "R", EnumTurnOutSyncSelection.Right),
            EnumGuiButton(20, width / 2 + 20, 80, 40, 20, "OFF", EnumTurnOutSyncSelection.OFF)
        )
        buttonList.addAll(turnOutSyncButtons)

        turnOutSyncButtonsType2 = arrayOf(
            EnumGuiButton(25, width / 2 + 10, 130, 20, 20, "L", EnumTurnOutSyncSelection.Left),
            EnumGuiButton(25, width / 2 + 50, 130, 20, 20, "R", EnumTurnOutSyncSelection.Right),
            EnumGuiButton(25, width / 2 + 20, 155, 40, 20, "OFF", EnumTurnOutSyncSelection.OFF)
        )
        buttonList.addAll(turnOutSyncButtonsType2)

        forceSelectSignalButtons = Array<EnumGuiButton<SignalLevel>>(SignalLevel.values().size) { i ->
            val signal = SignalLevel.values()[i]
            val id = signal.level - 1
            EnumGuiButton(
                30,
                width / 2 + 110,
                34 + id * 20,
                60,
                20,
                (signal.level).toString() + ":" + I18n.format(signal.name),
                signal
            )
        }
        buttonList.addAll(forceSelectSignalButtons)


        selectForcedMode(this.forcedSignalSelection)
        selectTurnOutSync(this.turnOutSyncSelection)
        selectTurnOutSync2(this.turnOutSyncSelection2)
        turnEnabledForceSelect(this.forcedSignalSelection.force)


        textFields.clear()
        signalChannelKeys = Array<Array<GuiTextFieldWithID>>(6) { i ->
            val (first, second) = this.detectorChannelKeys[i]
            arrayOf(
                setTextFieldWithID(i * 10, width / 2 - 120, 30 + 20 * i, 40, 15, first),
                setTextFieldWithID(i * 10 + 1, width / 2 - 60, 30 + 20 * i, 40, 15, second)
            )
        }
        turnoutChannelKeys = arrayOf(
            setTextFieldWithID(100, width / 2, 35, 40, 15, this.turnOutChannelKeyPair.name),
            setTextFieldWithID(101, width / 2 + 50, 35, 40, 15, this.turnOutChannelKeyPair.number),
        )
        turnoutChannelKeys2 = arrayOf(
            setTextFieldWithID(102, width / 2, 110, 40, 15, this.turnOutChannelKeyPair2.name),
            setTextFieldWithID(103, width / 2 + 50, 110, 40, 15, this.turnOutChannelKeyPair2.number),
        )
    }

    fun turnEnabledForceSelect(enabled: Boolean) {
        if (enabled) {
            selectForceSelectSignal(this.forceSelectSignal)
        } else {
            for (button in forceSelectSignalButtons) {
                button.enabled = false
            }
        }
    }

    fun selectForcedMode(mode: EnumForcedSignalMode) {
        this.forcedSignalSelection = mode
        for (enumButton in forceModeButtons) {
            enumButton.enabled = enumButton.value != this.forcedSignalSelection
        }
        turnEnabledForceSelect(this.forcedSignalSelection.force)
    }

    fun selectTurnOutSync(mode: EnumTurnOutSyncSelection) {
        this.turnOutSyncSelection = mode
        for (enumButton in turnOutSyncButtons) {
            enumButton.enabled = enumButton.value != this.turnOutSyncSelection
        }
    }

    fun selectTurnOutSync2(mode: EnumTurnOutSyncSelection) {
        this.turnOutSyncSelection2 = mode
        for (enumButton in turnOutSyncButtonsType2) {
            enumButton.enabled = enumButton.value != this.turnOutSyncSelection2
        }
    }

    fun selectForceSelectSignal(signal: SignalLevel) {
        this.forceSelectSignal = signal
        for (enumButton in forceSelectSignalButtons) {
            enumButton.enabled = enumButton.value != this.forceSelectSignal
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
        } else if (button.id == 10) {
            selectForcedMode((button as EnumGuiButton<*>).value as EnumForcedSignalMode)
        } else if (button.id == 20) {
            selectTurnOutSync((button as EnumGuiButton<*>).value as EnumTurnOutSyncSelection)
        } else if (button.id == 25) {
            selectTurnOutSync2((button as EnumGuiButton<*>).value as EnumTurnOutSyncSelection)
        } else if (button.id == 30) {
            selectForceSelectSignal((button as EnumGuiButton<*>).value as SignalLevel)
        }
        super.actionPerformed(button)
    }

    private fun sendPacket() {
        formatSignalLevel()
        val detectorChannelKeys = Array<ChannelKeyPair>(6) { i ->
            ChannelKeyPair(
                signalChannelKeys[i][0].text,
                signalChannelKeys[i][1].text
            )
        }
        val turnoutChannelKey = ChannelKeyPair(
            turnoutChannelKeys[0].text,
            turnoutChannelKeys[1].text
        )

        val turnoutChannelKey2 = ChannelKeyPair(
            turnoutChannelKeys2[0].text,
            turnoutChannelKeys2[1].text
        )

        PacketHandler.sendPacketServer(
            ReceiverTrafficLightMessageType2(
                tile,
                detectorChannelKeys,
                this.forcedSignalSelection,
                this.turnOutSyncSelection,
                this.turnOutSyncSelection2,
                turnoutChannelKey,
                turnoutChannelKey2,
                this.forceSelectSignal
            )
        )
    }

    private fun formatSignalLevel() {
        for (i in 0 until 6) {
            val num = NGTMath.getIntFromString(signalChannelKeys[i][1].text, 0, 999, 0)
            signalChannelKeys[i][1].text = num.toString()
        }
    }

    private fun getNextTextField(nowID: Int): GuiTextFieldWithID {
        if (nowID < 100) {
            var i = nowID / 10
            return if (nowID and 1 == 0) {
                signalChannelKeys[i][1]
            } else {
                i++
                if (i >= signalChannelKeys.size) {
                    i = 0
                }
                signalChannelKeys[i][0]
            }
        } else if(nowID < 102){
            return if (nowID and 1 == 0) {
                turnoutChannelKeys[1]
            } else {
                turnoutChannelKeys[0]
            }
        } else {
            return if (nowID and 1 == 0) {
                turnoutChannelKeys2[1]
            } else {
                turnoutChannelKeys2[0]
            }
        }
    }

    override fun keyTyped(par1: Char, par2: Int) {
        if (par2 == 1) {
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
                    if (!KeyboardUtil.isIntegerKey(par2) || par2 == 14) {
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
        } else if (currentTextField in turnoutChannelKeys) {
            val id = (currentTextField as GuiTextFieldWithID).id
            val side = id and 1
            if (side == 0) {
                if (par2 == Keyboard.KEY_TAB) {
                    currentTextField.isFocused = false
                    currentTextField = turnoutChannelKeys[1]
                    currentTextField.text = ""
                    currentTextField.isFocused = true
                    currentTextField.cursorPosition = 0
                } else if (!KeyboardUtil.isIntegerKey(par2) || par2 == 14) {
                    currentTextField.textboxKeyTyped(Character.toUpperCase(par1), par2)
                    if (currentTextField.text.length > 3) {
                        currentTextField.text = currentTextField.text.substring(0, 3)
                    }
                }
            } else {
                if (par2 == Keyboard.KEY_TAB) {
                    currentTextField.isFocused = false
                    currentTextField = turnoutChannelKeys[0]
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
        } else if (currentTextField in turnoutChannelKeys2) {
            val id = (currentTextField as GuiTextFieldWithID).id
            val side = id and 1
            if (side == 0) {
                if (par2 == Keyboard.KEY_TAB) {
                    currentTextField.isFocused = false
                    currentTextField = turnoutChannelKeys2[1]
                    currentTextField.text = ""
                    currentTextField.isFocused = true
                    currentTextField.cursorPosition = 0
                } else if (!KeyboardUtil.isIntegerKey(par2) || par2 == 14) {
                    currentTextField.textboxKeyTyped(Character.toUpperCase(par1), par2)
                    if (currentTextField.text.length > 3) {
                        currentTextField.text = currentTextField.text.substring(0, 3)
                    }
                }
            } else {
                if (par2 == Keyboard.KEY_TAB) {
                    currentTextField.isFocused = false
                    currentTextField = turnoutChannelKeys2[0]
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
        drawCenteredString(fontRendererObj, "Detector", width / 2 - 70, 5, 16777215)
        drawCenteredString(fontRendererObj, " Name     -    Number", width / 2 - 70, 15, 16777215)
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
        drawCenteredString(fontRendererObj, "TurnOut", width / 2 + 45, 15, -1)
        drawCenteredString(fontRendererObj, " Name   -  Number", width / 2 + 45, 25, -1)
        drawCenteredString(fontRendererObj, "-", width / 2 + 45, 38, -1)
    }
}