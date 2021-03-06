package keiproductfamily.normal

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.Packet
import net.minecraft.network.play.server.S35PacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity

abstract class TileNormal : TileEntity() {
    @SideOnly(Side.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Double.MAX_VALUE
    }

    override fun getDescriptionPacket(): Packet? {
        val nbtTagCompound = NBTTagCompound()
        writeToNBT(nbtTagCompound)
        return S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbtTagCompound)
    }

    override fun onDataPacket(net: NetworkManager?, pkt: S35PacketUpdateTileEntity) {
        readFromNBT(pkt.func_148857_g())
    }
}