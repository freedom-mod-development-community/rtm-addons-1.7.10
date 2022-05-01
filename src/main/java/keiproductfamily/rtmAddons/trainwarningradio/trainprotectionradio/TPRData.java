package keiproductfamily.rtmAddons.trainwarningradio.trainprotectionradio;

import keiproductfamily.rtmAddons.trainwarningradio.TAWPRType;
import net.minecraft.entity.player.EntityPlayer;

public class TPRData {
    public final TAWPRType type = TAWPRType.TPR;
    public int lostCnt = 0;
    public double lastX, lastZ;

    public TPRData(double x, double z) {
        this.lastX = x;
        this.lastZ = z;
    }

    public void update(EntityPlayer player) {
        this.lostCnt = 0;
        double newX = player.posX;
        double newZ = player.posZ;

        if (newX != lastX || newZ != lastZ) {
            lastX = newX;
            lastZ = newZ;
        }
    }

    public void update(TPRSendBlockTile tile) {
        this.lostCnt = 0;
    }

    public boolean checkRange(EntityPlayer player) {
        return checkRange(player.posX, player.posZ);
    }

    private boolean checkRange(double posX, double posZ) {
        double x = posX - lastX;
        double z = posZ - lastZ;
        double dist = x * x + z * z;
        if (dist < type.range2) {
            return type == TAWPRType.TPR;
        }
        return false;
    }
}
