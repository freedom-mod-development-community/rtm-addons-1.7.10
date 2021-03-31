package keiproductfamily;

import cpw.mods.fml.common.network.IGuiHandler;
import keiproductfamily.rtmAddons.trainDetector.EntityTrainDetectorAdvance;
import keiproductfamily.rtmAddons.trainDetector.GuiTrainDetectorAdvance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
//            case GuiIDs.GuiID_AdvanceChunkLoadTile:
//                TileEntity tile = world.getTileEntity(x, y, z);
//                if (tile instanceof AdvanceChunkLoadTile) {
//                    return new ContainerKEI();
//                }
//                break;
            case GuiIDs.GuiID_EntityTrainDetectorSetting:
                Entity entity = world.getEntityByID(x);
                if (entity instanceof EntityTrainDetectorAdvance) {
                    return new ContainerKEI();
                }
                break;
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
//            case GuiIDs.GuiID_AdvanceChunkLoadTile :
//              TileEntity tile = world.getTileEntity(x, y, z);
//              if (tile instanceof AdvanceChunkLoadTile) {
//                  return new ChunkLoadSettingGui((AdvanceChunkLoadTile) tile);
//              }
//            break;

            case GuiIDs.GuiID_EntityTrainDetectorSetting:
                Entity entity = world.getEntityByID(x);
                if (entity instanceof EntityTrainDetectorAdvance) {
                    return new GuiTrainDetectorAdvance((EntityTrainDetectorAdvance) entity);
                }
                break;
        }
        return null;
    }
}
