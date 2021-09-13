package keiproductfamily.rtmAddons.atc2.transmitter

import cpw.mods.fml.common.network.ByteBufUtils
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import cpw.mods.fml.relauncher.Side
import io.netty.buffer.ByteBuf
import keiproductfamily.currentWorld
import keiproductfamily.network.PacketHandler
import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.atc2.ATC2Core
import keiproductfamily.rtmAddons.signalchannel.RTMSignalChannelMaster
import keiproductfamily.rtmAddons.turnoutChannel.RTMTurnoutChannelMaster
import org.lwjgl.BufferUtils
import kotlin.properties.Delegates

class ATC2TransmitterMessage : IMessage, IMessageHandler<ATC2TransmitterMessage?, IMessage?> {
    var entityID: Int by Delegates.notNull()
    var signalChannelL: ChannelKeyPair by Delegates.notNull()
    var turnOutChannel: ChannelKeyPair by Delegates.notNull()
    var signalChannelR: ChannelKeyPair by Delegates.notNull()
    var formationRegex: String by Delegates.notNull()

    constructor()

    constructor(
        entityID: Int,
        signalChannelL: ChannelKeyPair,
        turnOutChannel: ChannelKeyPair,
        signalChannelR: ChannelKeyPair,
        formationRegex: String
    ) {
        this.entityID = entityID
        this.signalChannelL = signalChannelL
        this.turnOutChannel = turnOutChannel
        this.signalChannelR = signalChannelR
        this.formationRegex = formationRegex
    }

    override fun fromBytes(buf: ByteBuf) {
        this.entityID = buf.readInt()
        this.signalChannelL = ChannelKeyPair.readFromBuf(buf)
        this.turnOutChannel = ChannelKeyPair.readFromBuf(buf)
        this.signalChannelR = ChannelKeyPair.readFromBuf(buf)
        this.formationRegex = ByteBufUtils.readUTF8String(buf)
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(entityID)
        signalChannelL.writeToByteBuf(buf)
        turnOutChannel.writeToByteBuf(buf)
        signalChannelR.writeToByteBuf(buf)
        ByteBufUtils.writeUTF8String(buf, formationRegex)
    }

    override fun onMessage(message: ATC2TransmitterMessage?, ctx: MessageContext?): IMessage? {
        val entity = message?.let { ctx?.currentWorld?.getEntityByID(it.entityID) }
        if (entity is ATC2TransmitterEntity) {
            entity.setSignalChannelNameL(message.signalChannelL)
            entity.setTurnOutChannelName(message.turnOutChannel)
            entity.setSignalChannelNameR(message.signalChannelR)
            entity.subjectFormationRegex = message.formationRegex

            if (ctx?.side == Side.SERVER) {
                PacketHandler.sendPacketAll(message)
            }
        }
        return null
    }
}