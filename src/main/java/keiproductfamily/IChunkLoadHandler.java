package keiproductfamily;

import net.minecraftforge.common.ForgeChunkManager;

public interface IChunkLoadHandler {
    void chunkLoaderInit(ForgeChunkManager.Ticket ticket);
}
