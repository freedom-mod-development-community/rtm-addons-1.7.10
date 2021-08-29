package keiproductfamily

import cpw.mods.fml.common.network.IGuiHandler
import keiproductfamily.rtmAddons.receiverBlock.receiverTraffficLightsType2.ReceiverTrafficLightGuiType2
import keiproductfamily.rtmAddons.receiverBlock.receiverTraffficLightsType2.ReceiverTrafficLightTileType2
import keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights.ReceiverTrafficLightGui
import keiproductfamily.rtmAddons.receiverBlock.receiverTrafficLights.ReceiverTrafficLightTile
import keiproductfamily.rtmAddons.receiverBlock.receiverTurnout.ReceiverTurnoutGui
import keiproductfamily.rtmAddons.receiverBlock.receiverTurnout.ReceiverTurnoutTile
import keiproductfamily.rtmAddons.trainDetector.EntityTrainDetectorAdvance
import keiproductfamily.rtmAddons.trainDetector.GuiTrainDetectorAdvance
import keiproductfamily.rtmAddons.turnoutSelector.TurnoutSelectorGui
import keiproductfamily.rtmAddons.turnoutSelector.TurnoutSelectorTile
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
            GuiIDs.GuiID_ReceiverTurnoutSetting -> {
                val tile = world.getTileEntity(x, y, z)
                if (tile is ReceiverTurnoutTile) {
                    return ContainerKEI()
                }
            }
            GuiIDs.GuiID_TurnoutSelecterGui -> {
                val tile = world.getTileEntity(x, y, z)
                if (tile is TurnoutSelectorTile) {
                    return ContainerKEI()
                }
            }
            GuiIDs.GuiID_ReceiverTrafficLightSettingType2 -> {
                val tile = world.getTileEntity(x, y, z)
                if (tile is ReceiverTrafficLightTileType2) {
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
            GuiIDs.GuiID_ReceiverTurnoutSetting -> {
                val tile = world.getTileEntity(x, y, z)
                if (tile is ReceiverTurnoutTile) {
                    return ReceiverTurnoutGui(tile)
                }
            }
            GuiIDs.GuiID_TurnoutSelecterGui -> {
                val tile = world.getTileEntity(x, y, z)
                if (tile is TurnoutSelectorTile) {
                    return TurnoutSelectorGui(tile)
                }
            }
            GuiIDs.GuiID_ReceiverTrafficLightSettingType2 -> {
                val tile = world.getTileEntity(x, y, z)
                if (tile is ReceiverTrafficLightTileType2) {
                    return ReceiverTrafficLightGuiType2(tile)
                }
            }
        }
        return null
    }
}