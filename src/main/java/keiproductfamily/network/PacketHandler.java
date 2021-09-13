package keiproductfamily.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import keiproductfamily.rtmAddons.RequestEntityNBTData;
import keiproductfamily.rtmAddons.atc2.ATC2SignalSendMessage;
import keiproductfamily.rtmAddons.atc2.transmitter.ATC2TransmitterMessage;
import keiproductfamily.rtmAddons.formationNumber.FormationNumberMessage;
import keiproductfamily.rtmAddons.receiverBlock.receiverTraffficLightsType2.ReceiverTrafficLightMessageType2;
import keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights.ReceiverTrafficLightMessage;
import keiproductfamily.rtmAddons.receiverBlock.receiverTurnout.ReceiverTurnoutMessage;
import keiproductfamily.rtmAddons.trainDetector.MessageTrainDetectorAdvance;
import keiproductfamily.rtmAddons.turnoutSelector.TurnoutSelectorMessage;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketHandler {
    private static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("KEIProductFamily");

    public static void init() {
//        registerMessage(new AdvanceChunkSettingSyncMessage(), AdvanceChunkSettingSyncMessage.class, 0x00, Side.SERVER);
//        registerMessage(new AdvanceChunkSettingSyncMessage(), AdvanceChunkSettingSyncMessage.class, 0x00, Side.CLIENT);
//
//        registerMessage(new ReceiveDataSyncMessage(), ReceiveDataSyncMessage.class, 0x01, Side.CLIENT);

        registerMessage(new MessageTrainDetectorAdvance(), MessageTrainDetectorAdvance.class, 0x02, Side.SERVER);
        registerMessage(new MessageTrainDetectorAdvance(), MessageTrainDetectorAdvance.class, 0x02, Side.CLIENT);
        registerMessage(new ReceiverTrafficLightMessage(), ReceiverTrafficLightMessage.class, 0x03, Side.SERVER);
        registerMessage(new ReceiverTrafficLightMessage(), ReceiverTrafficLightMessage.class, 0x03, Side.CLIENT);
        registerMessage(new RequestEntityNBTData(), RequestEntityNBTData.class, 0x04, Side.SERVER);
        registerMessage(new ReceiverTurnoutMessage(), ReceiverTurnoutMessage.class, 0x05, Side.SERVER);
        registerMessage(new ReceiverTurnoutMessage(), ReceiverTurnoutMessage.class, 0x05, Side.CLIENT);
        registerMessage(new TurnoutSelectorMessage(), TurnoutSelectorMessage.class, 0x06, Side.SERVER);
        registerMessage(new TurnoutSelectorMessage(), TurnoutSelectorMessage.class, 0x06, Side.CLIENT);
        registerMessage(ReceiverTrafficLightMessageType2.Companion, ReceiverTrafficLightMessageType2.class, 0x07, Side.SERVER);
        registerMessage(ReceiverTrafficLightMessageType2.Companion, ReceiverTrafficLightMessageType2.class, 0x07, Side.CLIENT);
        registerMessage(new ATC2TransmitterMessage(), ATC2TransmitterMessage.class, 0x08, Side.SERVER);
        registerMessage(new ATC2TransmitterMessage(), ATC2TransmitterMessage.class, 0x08, Side.CLIENT);
        registerMessage(ATC2SignalSendMessage.Companion, ATC2SignalSendMessage.class, 0x09, Side.SERVER);
        registerMessage(ATC2SignalSendMessage.Companion, ATC2SignalSendMessage.class, 0x09, Side.CLIENT);
        registerMessage(FormationNumberMessage.Companion, FormationNumberMessage.class, 0x0a, Side.SERVER);
        registerMessage(FormationNumberMessage.Companion, FormationNumberMessage.class, 0x0a, Side.CLIENT);
    }

    public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(IMessageHandler<? super REQ, ? extends REPLY> messageHandler, Class<REQ> requestMessageType, int discriminator, Side sendTo) {
        INSTANCE.registerMessage(messageHandler, requestMessageType, discriminator, sendTo);
    }

    public static void sendPacketServer(IMessage message) {
        INSTANCE.sendToServer(message);
    }

    public static void sendPacketAll(IMessage message) {
        INSTANCE.sendToAll(message);
    }

    public static void sendPacketEPM(IMessage message, EntityPlayerMP EPM) {
        INSTANCE.sendTo(message, EPM);
    }
}
