package keiproductfamily

import cpw.mods.fml.common.network.simpleimpl.MessageContext
import cpw.mods.fml.relauncher.Side
import io.netty.buffer.Unpooled
import keiproductfamily.rtmAddons.ChannelKeyPair
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import java.lang.StringBuilder

val MessageContext.currentWorld: World
    get() = when (side!!) {
        Side.SERVER -> serverHandler.playerEntity.worldObj
        Side.CLIENT -> Minecraft.getMinecraft().theWorld
    }

fun NBTTagCompound.getChannelKeyPair(keyName: String): ChannelKeyPair {
    val array = this.getByteArray(keyName)
    if (array != null && array.isNotEmpty()) {
        return ChannelKeyPair.readFromByteArray(array)
    }
    return ChannelKeyPair()
}

fun NBTTagCompound.setChannelKeyPair(keyName: String, pair: ChannelKeyPair) {
    this.setByteArray(keyName, pair.getAsByteArray())
}

fun NBTTagCompound.getChannelKeyPairArray(keyName: String): Array<ChannelKeyPair> {
    val cnt = this.getInteger(keyName + "cnt")
    val array = this.getByteArray(keyName)
    if (array != null && array.isNotEmpty()) {
        val buf = Unpooled.buffer()
        buf.writeBytes(array)
        return Array(cnt) {
            ChannelKeyPair.readFromBuf(buf)
        }
    }
    return Array(cnt) {
        ChannelKeyPair()
    }
}

fun NBTTagCompound.setChannelKeyPairArray(keyName: String, pairArray: Array<ChannelKeyPair>) {
    this.setInteger(keyName + "cnt", pairArray.size)
    val buf = Unpooled.buffer()
    for (pair in pairArray) {
        pair.writeToByteBuf(buf)
    }
    this.setByteArray(keyName, buf.array())
}

fun EntityLivingBase.getDirectional(): Int{
    return MathHelper.floor_double((this.rotationYaw * 4.0f / 360.0f).toDouble() + 2.5) and 3
}