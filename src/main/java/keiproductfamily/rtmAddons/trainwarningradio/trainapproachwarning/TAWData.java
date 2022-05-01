package keiproductfamily.rtmAddons.trainwarningradio.trainapproachwarning;

import keiproductfamily.rtmAddons.trainwarningradio.TAWPRType;
import keiproductfamily.rtmAddons.trainwarningradio.ITAWPRReceiverTile;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Random;

public class TAWData {
    public final TAWPRType type = TAWPRType.TAW;
    public final EntityPlayer player;
    public int lostCnt = 0;
    public double lastX, lastZ;
    public double senderAngRadian = 0;

    Random rand = new Random();

    public TAWData(EntityPlayer player) {
        this.player = player;
        this.lastX = player.posX;
        this.lastZ = player.posZ;
    }

    public void update(EntityPlayer player) {
        this.lostCnt = 0;
        double newX = player.posX;
        double newZ = player.posZ;

        if (Math.abs(newX - lastX) > 1 || Math.abs(newZ - lastZ) > 1) {
            senderAngRadian = Math.atan2(newZ - lastZ, newX - lastX);
            senderAngRadian %= (Math.PI * 2);
            if (senderAngRadian < 0) {
                senderAngRadian += (Math.PI * 2);
            }
            lastX = newX;
            lastZ = newZ;
        }
    }

    public boolean checkRange(EntityPlayer player) {
        return checkRange(player.posX, player.posZ);
    }

    private boolean checkRange(double posX, double posZ) {
        double x = posX - lastX;
        double z = posZ - lastZ;
        double dist = x * x + z * z;
        if (dist < type.range2) {
            double diffPosAngRad = Math.atan2(z, x) % (Math.PI * 2);
            if (diffPosAngRad < 0) {
                diffPosAngRad += (Math.PI * 2);
            }
            double diffRad1 = Math.abs(diffPosAngRad - senderAngRadian);
            double diffRad2 = Math.abs(diffPosAngRad - senderAngRadian - (Math.PI * 2));
            double diffRad = Math.min(diffRad1, diffRad2);

            double range = Math.PI / 4 + (rand.nextDouble() * Math.PI / 4);

            return diffRad < range;
        }
        return false;
    }

    public boolean checkRange(ITAWPRReceiverTile tile) {
        double x = tile.posX() - lastX;
        double z = tile.posZ() - lastZ;
        double dist2 = x * x + z * z;
        double power;
        if (dist2 < type.range2) {
            power = ((type.range - Math.sqrt(dist2)) / type.range) * 15;

            double diffPosAngRad = Math.atan2(z, x) % (Math.PI * 2);
            if (diffPosAngRad < 0) {
                diffPosAngRad += (Math.PI * 2);
            }
            double diffRad1 = Math.abs(diffPosAngRad - senderAngRadian);
            double diffRad2 = Math.abs(diffPosAngRad - senderAngRadian - (Math.PI * 2));
            double diffRad = Math.min(diffRad1, diffRad2);

            if (Math.PI / 2 < diffRad) {
                tile.setReceiveData(false, 0);
                return false;
            }
            if (Math.PI / 4 <= diffRad) {
                power *= (diffRad - Math.PI / 4) / (Math.PI / 4);
            }

            tile.setReceiveData(true, (int) Math.ceil(power));
            return true;
        }
        tile.setReceiveData(false, 0);
        return false;
    }
}
