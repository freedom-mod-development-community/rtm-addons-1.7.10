package keiproductfamily.rtmAddons.turnoutSelector

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import cpw.mods.fml.relauncher.Side
import io.netty.buffer.ByteBuf
import keiproductfamily.network.PacketHandler
import keiproductfamily.network.TileEntityMessage
import keiproductfamily.rtmAddons.ChannelKeyPair
import kotlin.properties.Delegates

class TurnoutSelectorMessage : TileEntityMessage, IMessageHandler<TurnoutSelectorMessage?, IMessage?> {
    var tile: TurnoutSelectorTile by Delegates.notNull()
    var thisTurnOutChannelKeyPair: ChannelKeyPair by Delegates.notNull()

    constructor()

    constructor(
        tile: TurnoutSelectorTile,
        thisTurnOutChannelKeyPair: ChannelKeyPair
    ) : super(tile) {
        this.thisTurnOutChannelKeyPair = thisTurnOutChannelKeyPair
    }

    override fun read(buf: ByteBuf) {
        this.thisTurnOutChannelKeyPair = ChannelKeyPair.readFromBuf(buf)
    }

    override fun write(buf: ByteBuf) {
        this.thisTurnOutChannelKeyPair.writeToByteBuf(buf)
    }

    override fun onMessage(message: TurnoutSelectorMessage?, ctx: MessageContext): IMessage? {
        val tile = message?.getTileEntity(ctx)
        if (tile is TurnoutSelectorTile) {
            tile.setTurnoutChunnelKey(message.thisTurnOutChannelKeyPair)
            if (ctx.side == Side.SERVER) {
                PacketHandler.sendPacketAll(message)
            }
        }
        return null
    }
}