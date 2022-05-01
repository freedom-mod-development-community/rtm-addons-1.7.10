package keiproductfamily.rtmAddons.formationNumber

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import cpw.mods.fml.relauncher.Side
import io.netty.buffer.ByteBuf
import keiproductfamily.network.PacketHandler
import kotlin.properties.Delegates

class FormationNumberMessage : IMessage {
    var formationID: Long = 0
    var formationNumberKeyPair: FormationNumberKeyPair by Delegates.notNull()

    constructor() {}

    constructor(formationID: Long, formationNumberKeyPair: FormationNumberKeyPair) {
        this.formationID = formationID
        this.formationNumberKeyPair = formationNumberKeyPair
    }

    override fun fromBytes(buf: ByteBuf) {
        this.formationID = buf.readLong()
        this.formationNumberKeyPair = FormationNumberKeyPair.readFromBuf(buf)
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeLong(this.formationID)
        this.formationNumberKeyPair.writeToByteBuf(buf)
    }

    companion object : IMessageHandler<FormationNumberMessage, IMessage?> {
        override fun onMessage(message: FormationNumberMessage, ctx: MessageContext): IMessage? {
            FormationNumberCore.set(message.formationID, message.formationNumberKeyPair)
            if (ctx.side == Side.SERVER) {
                FormationNumberCore.save()
                PacketHandler.sendPacketAll(message)
            }
            return null
        }
    }
}