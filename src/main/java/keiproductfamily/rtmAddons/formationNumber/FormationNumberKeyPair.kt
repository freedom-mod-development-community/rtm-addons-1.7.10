package keiproductfamily.rtmAddons.formationNumber

import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

class FormationNumberKeyPair(
    val name: String = "",
    val number: String = ""
) {
    val keyString: String by lazy { toString() }
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
        return if (other is FormationNumberKeyPair) {
            this.name == other.name && this.number == other.number
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return name.hashCode() + number.hashCode()
    }

    companion object {
        fun readFromBuf(buf: ByteBuf): FormationNumberKeyPair {
            return FormationNumberKeyPair(
                ByteBufUtils.readUTF8String(buf),
                ByteBufUtils.readUTF8String(buf)
            )
        }

        fun readFromByteArray(array: ByteArray): FormationNumberKeyPair {
            return readFromBuf(Unpooled.buffer().writeBytes(array))
        }
    }
}