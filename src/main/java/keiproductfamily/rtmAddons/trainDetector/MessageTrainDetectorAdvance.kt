package keiproductfamily.rtmAddons.trainDetector

import cpw.mods.fml.common.network.ByteBufUtils
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import cpw.mods.fml.relauncher.Side
import io.netty.buffer.ByteBuf
import keiproductfamily.currentWorld
import keiproductfamily.network.PacketHandler
import kotlin.properties.Delegates

class MessageTrainDetectorAdvance : IMessage, IMessageHandler<MessageTrainDetectorAdvance?, IMessage?> {
    var entityID: Int by Delegates.notNull()
    var channelName: String by Delegates.notNull()
    var channelNumber: String by Delegates.notNull()

    constructor()

    constructor(entityID: Int, channelName: String, channelNumber: String) {
        this.entityID = entityID
        this.channelName = channelName
        this.channelNumber = channelNumber
    }

    override fun fromBytes(buf: ByteBuf) {
        entityID = buf.readInt()
        channelName = ByteBufUtils.readUTF8String(buf)
        channelNumber = ByteBufUtils.readUTF8String(buf)
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(entityID)
        ByteBufUtils.writeUTF8String(buf, channelName)
        ByteBufUtils.writeUTF8String(buf, channelNumber)
    }

    override fun onMessage(message: MessageTrainDetectorAdvance?, ctx: MessageContext?): IMessage? {
        val entity = message?.let { ctx?.currentWorld?.getEntityByID(it.entityID) }
        if (entity is EntityTrainDetectorAdvance) {
            entity.setChannelName(message.channelName, message.channelNumber)
            if (ctx?.side == Side.SERVER) {
                PacketHandler.sendPacketAll(message)
            }
        }
        return null
    }
}