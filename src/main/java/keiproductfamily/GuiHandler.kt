package keiproductfamily

import cpw.mods.fml.common.network.IGuiHandler
import keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights.ReceiverTrafficLightGui
import keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights.ReceiverTrafficLightTile
import keiproductfamily.rtmAddons.trainDetector.EntityTrainDetectorAdvance
import keiproductfamily.rtmAddons.trainDetector.GuiTrainDetectorAdvance
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

class GuiHandler : IGuiHandler {
    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        when (ID) {
            GuiIDs.GuiID_EntityTrainDetectorSetting -> {
                val entity = world.getEntityByID(x)
                if (entity is EntityTrainDetectorAdvance) {
                    return ContainerKEI()
                }
            }
            GuiIDs.GuiID_ReceiverTrafficLightSetting -> {
                val tile = world.getTileEntity(x, y, z)
                if (tile is ReceiverTrafficLightTile) {
                    return ContainerKEI()
                }
            }
        }
        return null
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        when (ID) {
            GuiIDs.GuiID_EntityTrainDetectorSetting -> {
                val entity = world.getEntityByID(x)
                if (entity is EntityTrainDetectorAdvance) {
                    return GuiTrainDetectorAdvance(entity)
                }
            }
            GuiIDs.GuiID_ReceiverTrafficLightSetting -> {
                val tile = world.getTileEntity(x, y, z)
                if (tile is ReceiverTrafficLightTile) {
                    return ReceiverTrafficLightGui(tile)
                }
            }
        }
        return null
    }
}