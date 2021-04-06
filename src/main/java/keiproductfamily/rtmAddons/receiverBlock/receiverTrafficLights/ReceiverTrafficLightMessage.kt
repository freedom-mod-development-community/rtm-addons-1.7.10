package keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights

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
import redstoneChunkLoader.network.TileEntityMessage
import kotlin.properties.Delegates

class ReceiverTrafficLightMessage : TileEntityMessage, IMessageHandler<ReceiverTrafficLightMessage?, IMessage?> {
    var tile: ReceiverTrafficLightTile by Delegates.notNull()
    var detectorChannelKeys: Array<ChannelKeyPair> by Delegates.notNull()
    var forcedSignalSlection: EnumForcedSignalMode by Delegates.notNull()
    var turnOutSyncSelection: EnumTurnOutSyncSelection by Delegates.notNull()
    var turnOutChannelKeyPair: ChannelKeyPair by Delegates.notNull()
    var forceSelectSignal: SignalLevel by Delegates.notNull()

    constructor()

    constructor(
        tile: ReceiverTrafficLightTile,
        detectorChannelKeys: Array<ChannelKeyPair>,
        forcedSignalSlection: EnumForcedSignalMode,
        turnOutSyncSelection: EnumTurnOutSyncSelection,
        turnOutChannelKey: ChannelKeyPair,
        forceSelectSignal: SignalLevel
    ) : super(tile) {
        this.detectorChannelKeys = detectorChannelKeys
        this.forcedSignalSlection = forcedSignalSlection
        this.turnOutSyncSelection = turnOutSyncSelection
        this.turnOutChannelKeyPair = turnOutChannelKey
        this.forceSelectSignal = forceSelectSignal
    }

    override fun read(buf: ByteBuf) {
        val cnt = buf.readInt()
        this.detectorChannelKeys = Array<ChannelKeyPair>(cnt) {
            ChannelKeyPair.readFromBuf(buf)
        }
        this.forcedSignalSlection = EnumForcedSignalMode.getType(buf.readInt())
        this.turnOutSyncSelection = EnumTurnOutSyncSelection.getType(buf.readInt())
        this.turnOutChannelKeyPair = ChannelKeyPair.readFromBuf(buf)
        this.forceSelectSignal = SignalLevel.getSignal(buf.readInt())
    }

    override fun write(buf: ByteBuf) {
        buf.writeInt(detectorChannelKeys.size)
        for(pair in detectorChannelKeys) {
            pair.writeToByteBuf(buf)
        }
        buf.writeInt(forcedSignalSlection.id)
        buf.writeInt(turnOutSyncSelection.id)
        turnOutChannelKeyPair.writeToByteBuf(buf)
        buf.writeInt(forceSelectSignal.level)
    }

    override fun onMessage(message: ReceiverTrafficLightMessage?, ctx: MessageContext): IMessage? {
        val tile = message?.getTileEntity(ctx)
        if (tile is ReceiverTrafficLightTile) {
            tile.setDetectorChunnelKeys(message.detectorChannelKeys)
            tile.setTurnoutChunnelKey(message.turnOutChannelKeyPair)
            tile.setDatas(
                message.forcedSignalSlection,
                message.turnOutSyncSelection,
                message.forceSelectSignal
            )
            if (ctx.side == Side.SERVER) {
                PacketHandler.sendPacketAll(message)
            }
        }
        return null
    }
}