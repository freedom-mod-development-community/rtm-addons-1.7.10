package keiproductfamily.rtmAddons.trainwarningradio.trainapproachwarning;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TAWMaster {
    private static HashMap<Integer, TAWData> tawSenderList = new HashMap<Integer, TAWData>();

    public static void resetLostCnt(int itawpr, EntityPlayer player) {
        if (!tawSenderList.containsKey(itawpr)) {
            tawSenderList.put(itawpr, new TAWData(player));
        }

        TAWData data = tawSenderList.get(itawpr);
        data.update(player);
    }

    public static void updateTick() {
        ArrayList<Integer> removeList = new ArrayList<Integer>();
        for (Map.Entry<Integer, TAWData> entry : tawSenderList.entrySet()) {
            entry.getValue().lostCnt++;
            if (entry.getValue().lostCnt >= 10) {
                removeList.add(entry.getKey());
            }
        }
        for (Integer key : removeList) {
            tawSenderList.remove(key);
        }
    }

    public static boolean checkReceive(EntityPlayer entityPlayer) {
        for (Map.Entry<Integer, TAWData> entry : tawSenderList.entrySet()) {
            if (entry.getValue().checkRange(entityPlayer)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkReceive(TAWReceiveBlockTile tile) {
        for (Map.Entry<Integer, TAWData> entry : tawSenderList.entrySet()) {
            if (entry.getValue().checkRange(tile)) {
                return true;
            }
        }
        return false;
    }

    private static final Random random = new Random();

    public static int getID(ItemStack itemStack) {
        if (itemStack.getItem() instanceof TAWSendItem && itemStack.hasTagCompound()) {
            if (!itemStack.getTagCompound().hasKey("tawID")) {
                itemStack.getTagCompound().setInteger("tawID", random.nextInt());
            }

            return itemStack.getTagCompound().getInteger("tawID");
        }
        return -1;
    }
}
