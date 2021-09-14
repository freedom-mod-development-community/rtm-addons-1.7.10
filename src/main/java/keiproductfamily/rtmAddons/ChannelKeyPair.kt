package keiproductfamily.rtmAddons

import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

data class ChannelKeyPair(
    val name: String = "",
    val number: String = ""
) {
    val keyString: String by lazy {
        if (hasData()) {
            toString()
        } else {
            ""
        }
    }

    override fun toString(): String = "$name-$number"

    fun hasData(): Boolean = name != "" && number != ""

    fun writeToByteBuf(buf: ByteBuf): ByteBuf {
        ByteBufUtils.writeUTF8String(buf, name)
        ByteBufUtils.writeUTF8String(buf, number)
        return buf
    }

    fun getAsByteArray(): ByteArray {
        return this.writeToByteBuf(Unpooled.buffer()).array()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other is ChannelKeyPair) {
            this.name == other.name && this.number == other.number
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return name.hashCode() + number.hashCode()
    }

    companion object {
        fun readFromBuf(buf: ByteBuf): ChannelKeyPair {
            return ChannelKeyPair(
                ByteBufUtils.readUTF8String(buf),
                ByteBufUtils.readUTF8String(buf)
            )
        }

        fun readFromByteArray(array: ByteArray): ChannelKeyPair {
            return readFromBuf(Unpooled.buffer().writeBytes(array))
        }
    }
}