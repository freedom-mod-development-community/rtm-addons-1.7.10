package keiproductfamily.rtmAddons;

import java.util.HashMap;

public class RTMAChannelMaster {
    private static HashMap<String, RTMAChannelData> channelDatas = new HashMap<>();
    public static RTMAChannelData getChannelData(String channelName){
        return channelDatas.get(channelName);
    }
}
