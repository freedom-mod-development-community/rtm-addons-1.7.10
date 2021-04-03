package keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights

import cpw.mods.fml.common.network.ByteBufUtils
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import cpw.mods.fml.relauncher.Side
import io.netty.buffer.ByteBuf
import keiproductfamily.network.PacketHandler
import keiproductfamily.rtmAddons.ChannelKeyPair
import redstoneChunkLoader.network.TileEntityMessage
import kotlin.properties.Delegates

class ReceiverTrafficLightMessage : TileEntityMessage, IMessageHandler<ReceiverTrafficLightMessage?, IMessage?> {
    var tile: ReceiverTrafficLightTile by Delegates.notNull()
    var channelKeys: Array<ChannelKeyPair> by Delegates.notNull()

    constructor()

    constructor(
        tile: ReceiverTrafficLightTile,
        channelKeys: Array<ChannelKeyPair>
    ) : super(tile) {
        this.channelKeys = channelKeys
    }

    override fun read(buf: ByteBuf) {
        val cnt = buf.readInt()
        this.channelKeys = Array<ChannelKeyPair>(cnt) {
            ChannelKeyPair(
                ByteBufUtils.readUTF8String(buf),
                buf.readInt()
            )
        }
    }

    override fun write(buf: ByteBuf) {
        buf.writeInt(channelKeys.size)
        for(pair in channelKeys) {
            pair.writeToByteBuf(buf)
        }
    }

    override fun onMessage(message: ReceiverTrafficLightMessage?, ctx: MessageContext): IMessage? {
        val tile = message?.getTileEntity(ctx)
        if (tile is ReceiverTrafficLightTile) {
            tile.setChunnelKeys(message.channelKeys)
            if (ctx.side == Side.SERVER) {
                PacketHandler.sendPacketAll(message)
            }
        }
        return null
    }
}