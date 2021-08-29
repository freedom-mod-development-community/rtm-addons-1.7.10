package keiproductfamily.rtmAddons.receiverBlock.receiverTraffficLightsType2

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import cpw.mods.fml.relauncher.Side
import io.netty.buffer.ByteBuf
import jp.ngt.rtm.electric.SignalLevel
import keiproductfamily.network.PacketHandler
import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.EnumForcedSignalMode
import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection
import keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights.ReceiverTrafficLightMessage
import keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights.ReceiverTrafficLightTile
import kotlin.properties.Delegates

class ReceiverTrafficLightMessageType2 : ReceiverTrafficLightMessage {
    var turnOutSyncSelection2: EnumTurnOutSyncSelection by Delegates.notNull()
    var turnOutChannelKeyPair2: ChannelKeyPair by Delegates.notNull()

    constructor()

    constructor(
        tile: ReceiverTrafficLightTile,
        detectorChannelKeys: Array<ChannelKeyPair>,
        forcedSignalSelection: EnumForcedSignalMode,
        turnOutSyncSelection: EnumTurnOutSyncSelection,
        turnOutSyncSelection2: EnumTurnOutSyncSelection,
        turnOutChannelKey: ChannelKeyPair,
        turnOutChannelKey2: ChannelKeyPair,
        forceSelectSignal: SignalLevel
    ) : super(
        tile,
        detectorChannelKeys,
        forcedSignalSelection,
        turnOutSyncSelection,
        turnOutChannelKey,
        forceSelectSignal
    ) {
        this.turnOutSyncSelection2 = turnOutSyncSelection2
        this.turnOutChannelKeyPair2 = turnOutChannelKey2
    }

    override fun read(buf: ByteBuf) {
        super.read(buf)
        this.turnOutSyncSelection2 = EnumTurnOutSyncSelection.getType(buf.readInt())
        this.turnOutChannelKeyPair2 = ChannelKeyPair.readFromBuf(buf)
    }

    override fun write(buf: ByteBuf) {
        super.write(buf)
        buf.writeInt(turnOutSyncSelection2.id)
        turnOutChannelKeyPair2.writeToByteBuf(buf)
    }

    companion object : IMessageHandler<ReceiverTrafficLightMessageType2, IMessage?> {
        override fun onMessage(message: ReceiverTrafficLightMessageType2, ctx: MessageContext): IMessage? {
            val tile = message.getTileEntity(ctx)
            if (tile is ReceiverTrafficLightTileType2) {
                tile.detectorChannelKeys = message.detectorChannelKeys
                tile.turnOutChannelKeyPair = message.turnOutChannelKeyPair
                tile.turnOutChannelKeyPair2 = message.turnOutChannelKeyPair2
                tile.setDatas(
                    message.forcedSignalSelection,
                    message.turnOutSyncSelection,
                    message.turnOutSyncSelection2,
                    message.forceSelectSignal
                )

                if (ctx.side == Side.SERVER) {
                    PacketHandler.sendPacketAll(message)
                }
            }
            return null
        }
    }
}