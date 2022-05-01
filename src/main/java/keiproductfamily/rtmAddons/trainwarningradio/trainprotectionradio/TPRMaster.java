package keiproductfamily.rtmAddons.trainwarningradio.trainprotectionradio;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TPRMaster {
    private static HashMap<Integer, TPRData> tprSenderList = new HashMap<Integer, TPRData>();

    public static void resetLostCnt(int itawpr, EntityPlayer player){
        if(!tprSenderList.containsKey(itawpr)){
            tprSenderList.put(itawpr, new TPRData(player.posX, player.posZ));
        }

        TPRData data = tprSenderList.get(itawpr);
        data.update(player);
    }
    public static void resetLostCnt(TPRSendBlockTile tile){
        if(!tprSenderList.containsKey(tile.getTprID())){
            tprSenderList.put(tile.getTprID(), new TPRData(tile.xCoord + 0.5, tile.zCoord + 0.5));
        }

        TPRData data = tprSenderList.get(tile.getTprID());
        data.update(tile);
    }

    public static void updateTick(){
        ArrayList<Integer> removeList = new ArrayList<Integer>();
        for(Map.Entry<Integer, TPRData> entry : tprSenderList.entrySet()){
            entry.getValue().lostCnt++;
            if(entry.getValue().lostCnt>=10){
                removeList.add(entry.getKey());
            }
        }
        for(Integer key : removeList) {
            tprSenderList.remove(key);
        }
    }

    public static boolean checkReceive(EntityPlayer entityPlayer){
        for(TPRData data : tprSenderList.values()){
            if(data.checkRange(entityPlayer)){
                return true;
            }
        }
        return false;
    }

    private static final Random random = new Random();
    public static int getID(ItemStack itemStack){
        if (itemStack.getItem() instanceof TPRSendItem && itemStack.hasTagCompound()) {
            if (!itemStack.getTagCompound().hasKey("tprID")) {
                itemStack.getTagCompound().setInteger("tprID", random.nextInt());
            }

            return itemStack.getTagCompound().getInteger("tprID");
        }
        return -1;
    }
}
