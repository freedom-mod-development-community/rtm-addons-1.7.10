package keiproductfamily.rtmAddons

import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf

data class ChannelKeyPair(
    val name: String,
    val number: Int
) {
    fun getKey(): String = toString()
    override fun toString(): String = "$name-$number"

    fun writeToByteBuf(buf: ByteBuf){
        ByteBufUtils.writeUTF8String(buf, name)
        buf.writeInt(number)
    }

    companion object{
        fun readFromTyteBuf(buf: ByteBuf): ChannelKeyPair{
            return ChannelKeyPair(
                ByteBufUtils.readUTF8String(buf),
                buf.readInt()
            )
        }
    }
}