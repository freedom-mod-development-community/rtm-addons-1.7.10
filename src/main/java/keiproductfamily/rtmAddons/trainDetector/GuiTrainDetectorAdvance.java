package keiproductfamily.rtmAddons.trainDetector;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.util.KeyboardUtil;
import keiproductfamily.network.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiTrainDetectorAdvance extends GuiScreenCustom {
    private final EntityTrainDetectorAdvance entityTDAdvance;
    private GuiTextField[] signalValues;

    public GuiTrainDetectorAdvance(EntityTrainDetectorAdvance par1) {
        this.entityTDAdvance = par1;
    }

    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done", new Object[0])));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel", new Object[0])));
        int i0 = 0;

        this.textFields.clear();
        this.signalValues = new GuiTextField[2];
        this.signalValues[0] = this.setTextField(this.width / 2 - 60 + i0, 100, 40, 20, entityTDAdvance.getChannelName());
        this.signalValues[1] = this.setTextField(this.width / 2 + 20 + i0, 100, 40, 20, String.valueOf(entityTDAdvance.getChannelNumber()));

    }

    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen((GuiScreen) null);
            this.sendPacket();
        } else if (button.id == 1) {
            this.mc.displayGuiScreen((GuiScreen) null);
        }

        super.actionPerformed(button);
    }

    private void sendPacket() {
        this.formatSignalLevel();
        PacketHandler.sendPacketServer(new MessageTrainDetectorAdvance(this.entityTDAdvance.getEntityId(), signalValues[0].getText(), Integer.parseInt(signalValues[1].getText())));
    }

    private void formatSignalLevel() {
        int i0 = NGTMath.getIntFromString(this.signalValues[1].getText(), 0, 999, 0);
        this.signalValues[1].setText(String.valueOf(i0));
    }

    protected void keyTyped(char par1, int par2) {
        if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.thePlayer.closeScreen();
        }

        if (this.currentTextField != null) {
            if (this.currentTextField == signalValues[0]) {
                if (par2 == Keyboard.KEY_TAB) {
                    currentTextField = signalValues[1];
                    currentTextField.setText("");
                    currentTextField.setFocused(true);
                } else {
                    this.currentTextField.textboxKeyTyped(Character.toUpperCase(par1), par2);
                    if (currentTextField.getText().length() > 3) {
                        currentTextField.setText(currentTextField.getText().substring(0, 3));
                    }
                }
            } else if (this.currentTextField == signalValues[1] && KeyboardUtil.isIntegerKey(par2)) {
                this.currentTextField.textboxKeyTyped(par1, par2);
                if (currentTextField.getText().length() > 3) {
                    currentTextField.setText(currentTextField.getText().substring(0, 3));
                }
            }
        }

        if (par2 == 28) {
            this.formatSignalLevel();
        }

    }

    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        super.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRendererObj, "Channel Name - Channel Number", this.width / 2, 85, 16777215);
        this.drawCenteredString(this.fontRendererObj, "- ", this.width / 2, 105, 16777215);
    }
}
