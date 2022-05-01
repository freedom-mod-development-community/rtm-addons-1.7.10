package keiproductfamily.rtmAddons.atc2

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import keiproductfamily.rtmAddons.formationNumber.FormatinNumberCli
import keiproductfamily.rtmAddons.formationNumber.FormationNumberCore
import keiproductfamily.rtmAddons.formationNumber.FormationNumberKeyPair
import kotlin.properties.Delegates

class ATC2SignalSendMessage : IMessage {
    constructor()

    var formationID: Long = 0
    var formationNumber: FormationNumberKeyPair by Delegates.notNull()
    var signalLevel: Int = 0

    constructor(formationID: Long, formationNumber: FormationNumberKeyPair, signalLevel: Int) {
        this.formationID = formationID
        this.formationNumber = formationNumber
        this.signalLevel = signalLevel
    }

    override fun fromBytes(buf: ByteBuf) {
        this.formationID = buf.readLong()
        this.formationNumber = FormationNumberKeyPair.readFromBuf(buf)
        this.signalLevel = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeLong(this.formationID)
        this.formationNumber.writeToByteBuf(buf)
        buf.writeInt(this.signalLevel)
    }

    companion object : IMessageHandler<ATC2SignalSendMessage, IMessage?> {
        override fun onMessage(message: ATC2SignalSendMessage, ctx: MessageContext): IMessage? {
            ATC2Cli.formationID = message.formationID
            FormatinNumberCli.formationNumber = message.formationNumber
            ATC2Cli.nowSignal = message.signalLevel
            ATC2Cli.showGuiForce = ATC2Cli.nowSignal > 0
            return null
        }
    }
}