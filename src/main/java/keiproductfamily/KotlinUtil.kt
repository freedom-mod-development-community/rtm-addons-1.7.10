package keiproductfamily

import cpw.mods.fml.common.network.simpleimpl.MessageContext
import cpw.mods.fml.relauncher.Side
import net.minecraft.client.Minecraft
import net.minecraft.world.World

val MessageContext.currentWorld: World
    get() = when (side!!) {
        Side.SERVER -> serverHandler.playerEntity.worldObj
        Side.CLIENT -> Minecraft.getMinecraft().theWorld
    }
