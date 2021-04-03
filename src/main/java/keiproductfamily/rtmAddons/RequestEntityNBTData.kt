package keiproductfamily.rtmAddons

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import jp.ngt.ngtlib.network.PacketNBT
import keiproductfamily.currentWorld
import net.minecraft.entity.Entity
import kotlin.properties.Delegates

class RequestEntityNBTData : IMessage, IMessageHandler<RequestEntityNBTData, IMessage> {
    var entityID: Int by Delegates.notNull<Int>()

    constructor() {}
    constructor(entity: Entity) {
        this.entityID = entity.entityId
    }

    override fun fromBytes(buf: ByteBuf) {
        this.entityID = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(this.entityID)
    }

    override fun onMessage(message: RequestEntityNBTData, ctx: MessageContext): IMessage? {
        PacketNBT.sendTo(ctx.currentWorld.getEntityByID(message.entityID), ctx.serverHandler.playerEntity)
        return null
    }
}