package keiproductfamily.rtmAddons.trainwarningradio

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import keiproductfamily.currentWorld
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World


class ReceiveDataSyncMessage : IMessage, IMessageHandler<ReceiveDataSyncMessage, IMessage?> {
    var x = 0
    var y = 0
    var z = 0
    var isReceived = false
    var receiveLevel = 0

    constructor() {}
    constructor(tile: ITAWPRReceiverTile, isReceived: Boolean, receiveLevel: Int) {
        val tileEntity = tile as TileEntity
        x = tileEntity.xCoord
        y = tileEntity.yCoord
        z = tileEntity.zCoord
        this.isReceived = isReceived
        this.receiveLevel = receiveLevel
    }

    override fun fromBytes(buf: ByteBuf) {
        x = buf.readInt()
        y = buf.readInt()
        z = buf.readInt()
        isReceived = buf.readBoolean()
        receiveLevel = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(x)
        buf.writeInt(y)
        buf.writeInt(z)
        buf.writeBoolean(isReceived)
        buf.writeInt(receiveLevel)
    }

    override fun onMessage(message: ReceiveDataSyncMessage, ctx: MessageContext): IMessage? {
        val world: World = ctx.currentWorld
        val tileEntity = world.getTileEntity(message.x, message.y, message.z)
        if (tileEntity is ITAWPRReceiverTile) {
            (tileEntity as ITAWPRReceiverTile).setReceiveData(message.isReceived, message.receiveLevel)
        }
        return null
    }
}
