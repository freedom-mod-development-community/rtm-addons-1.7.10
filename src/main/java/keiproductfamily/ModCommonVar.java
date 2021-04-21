package keiproductfamily;

import jp.ngt.rtm.electric.SignalLevel;
import keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights.ReceiverTrafficLightBlock;
import keiproductfamily.rtmAddons.receiverBlock.receiverTurnout.ReceiverTurnoutBlock;
import net.minecraft.block.Block;

public class ModCommonVar {
    public static final SignalLevel findTrainLevel = SignalLevel.STOP;
    public static final SignalLevel notfindTrainLevel = SignalLevel.PROCEED;

    public static final Block receiverTrafficLightBlock = new ReceiverTrafficLightBlock();
    public static final Block receiverTurnoutBlock = new ReceiverTurnoutBlock();
}
