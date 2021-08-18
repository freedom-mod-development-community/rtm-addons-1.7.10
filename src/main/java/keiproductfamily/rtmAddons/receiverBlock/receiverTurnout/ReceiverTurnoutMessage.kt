package keiproductfamily.rtmAddons.receiverBlock.receiverTurnout

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import cpw.mods.fml.relauncher.Side
import io.netty.buffer.ByteBuf
import keiproductfamily.network.PacketHandler
import keiproductfamily.network.TileEntityMessage
import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.EnumTurnOutSwitch
import java.util.*
import kotlin.collections.HashSet
import kotlin.properties.Delegates

class ReceiverTurnoutMessage : TileEntityMessage, IMessageHandler<ReceiverTurnoutMessage?, IMessage?> {
    var tile: ReceiverTurnoutTile by Delegates.notNull()
    var thisTurnOutChannelKeyPair: ChannelKeyPair by Delegates.notNull()
    var detectorChannelKey: ChannelKeyPair by Delegates.notNull()
    var rsOFFTurnOutSelection: EnumTurnOutSwitch by Delegates.notNull()
    var defaultTurnOutSelection: EnumTurnOutSwitch by Delegates.notNull()
    var turnOutSelectRollIDs: BitSet by Delegates.notNull()
    var keepTurnOutSelectTime: Int by Delegates.notNull()

    constructor()

    constructor(
        tile: ReceiverTurnoutTile,
        thisTurnOutChannelKeyPair: ChannelKeyPair,
        detectorChannelKey: ChannelKeyPair,
        rsOFFTurnOutSelection: EnumTurnOutSwitch,
        defaultTurnOutSelection: EnumTurnOutSwitch,
        turnOutSelectRollIDs: BitSet,
        keepTurnOutSelectTime: Int
    ) : super(tile) {
        this.thisTurnOutChannelKeyPair = thisTurnOutChannelKeyPair
        this.detectorChannelKey = detectorChannelKey
        this.rsOFFTurnOutSelection = rsOFFTurnOutSelection
        this.defaultTurnOutSelection = defaultTurnOutSelection
        this.turnOutSelectRollIDs = turnOutSelectRollIDs
        this.keepTurnOutSelectTime = keepTurnOutSelectTime
    }

    override fun read(buf: ByteBuf) {
        this.thisTurnOutChannelKeyPair = ChannelKeyPair.readFromBuf(buf)
        this.detectorChannelKey = ChannelKeyPair.readFromBuf(buf)
        this.rsOFFTurnOutSelection = EnumTurnOutSwitch.getType(buf.readInt())
        this.defaultTurnOutSelection = EnumTurnOutSwitch.getType(buf.readInt())
        val length = buf.readInt()
        val set = IntArray(length) { buf.readInt() }
        this.turnOutSelectRollIDs = BitSet(16)
        for (id in set) {
            if (id in 0..15) {
                this.turnOutSelectRollIDs[id] = true
            }
        }
        this.keepTurnOutSelectTime = buf.readInt()
    }

    override fun write(buf: ByteBuf) {
        this.thisTurnOutChannelKeyPair.writeToByteBuf(buf)
        this.detectorChannelKey.writeToByteBuf(buf)
        buf.writeInt(this.rsOFFTurnOutSelection.id)
        buf.writeInt(this.defaultTurnOutSelection.id)

        //turnOutSelectRollIDs
        val array = HashSet<Int>()
        var i: Int = turnOutSelectRollIDs.nextSetBit(0)
        if (i != -1) {
            array.add(i)
            while (true) {
                if (++i < 0) break
                if (turnOutSelectRollIDs.nextSetBit(i).also { i = it } < 0) break
                val endOfRun: Int = turnOutSelectRollIDs.nextClearBit(i)
                do {
                    array.add(i)
                } while (++i != endOfRun)
            }
        }

        buf.writeInt(array.size)
        for (bit in array) {
            buf.writeInt(bit)
        }

        buf.writeInt(keepTurnOutSelectTime)
    }

    override fun onMessage(message: ReceiverTurnoutMessage?, ctx: MessageContext): IMessage? {
        val tile = message?.getTileEntity(ctx)
        if (tile is ReceiverTurnoutTile) {
            tile.thisTurnOutChannelKeyPair = message.thisTurnOutChannelKeyPair
            tile.detectorChannelKey = message.detectorChannelKey
            tile.setDatas(
                message.rsOFFTurnOutSelection,
                message.defaultTurnOutSelection,
                message.turnOutSelectRollIDs,
                message.keepTurnOutSelectTime
            )
            if (ctx.side == Side.SERVER) {
                PacketHandler.sendPacketAll(message)
            }
        }
        return null
    }
}