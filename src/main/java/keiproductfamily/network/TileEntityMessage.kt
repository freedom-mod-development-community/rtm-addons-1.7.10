package redstoneChunkLoader.network

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import keiproductfamily.Vector.Vec3I
import keiproductfamily.currentWorld
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

abstract class TileEntityMessage : IMessage {
    protected var pos: Vec3I? = null

    constructor() {}
    constructor(tile: TileEntity) {
        pos = Vec3I(tile.xCoord, tile.yCoord, tile.zCoord)
    }

    override fun fromBytes(buf: ByteBuf) {
        pos = Vec3I(buf.readInt(), buf.readInt(), buf.readInt())
        read(buf)
    }

    protected abstract fun read(buf: ByteBuf)
    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(pos!!.x)
        buf.writeInt(pos!!.y)
        buf.writeInt(pos!!.z)
        write(buf)
    }

    protected abstract fun write(buf: ByteBuf)
    protected fun getWorld(ctx: MessageContext): World {
        return ctx.currentWorld
    }

    protected fun getTileEntity(ctx: MessageContext): TileEntity? {
        val world = getWorld(ctx)
        return world.getTileEntity(pos!!.x, pos!!.y, pos!!.z)
    }
}