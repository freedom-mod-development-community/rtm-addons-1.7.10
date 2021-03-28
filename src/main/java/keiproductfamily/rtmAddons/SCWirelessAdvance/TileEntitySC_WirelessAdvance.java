package keiproductfamily.rtmAddons.SCWirelessAdvance;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.rtm.electric.TileEntitySignalConverter;
import jp.ngt.rtm.world.IChunkLoader;
import jp.ngt.rtm.world.RTMChunkManager;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.*;


public class TileEntitySC_WirelessAdvance extends TileEntitySignalConverter implements IChunkLoader {
    private static final Map<Integer, List<TileEntitySC_WirelessAdvance>> ADAPTER_MAP = new HashMap<>();
    private static final Map<Integer, Integer> lastLevel_MAP = new HashMap<>();
    public static void initLastLevel_MAP(){
        lastLevel_MAP.clear();
    }

    private int prevChannel = 0;

    public TileEntitySC_WirelessAdvance() {
        List<TileEntitySC_WirelessAdvance> list = this.getList(this.prevChannel);
        list.add(this);
    }

    private List<TileEntitySC_WirelessAdvance> getList(int par1) {
        return ADAPTER_MAP.computeIfAbsent(par1, k -> new ArrayList<>());
    }

    private void updateAntennaList() {
        List<TileEntitySC_WirelessAdvance> list = this.getList(this.prevChannel);
        list.remove(this);
        List<TileEntitySC_WirelessAdvance> list2 = this.getList(this.getChannel());
        list2.add(this);
        this.prevChannel = this.getChannel();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!this.worldObj.isRemote) {
            this.updateChunks();
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!this.worldObj.isRemote) {
            this.releaseTicket();
        }
        List<TileEntitySC_WirelessAdvance> list = this.getList(this.prevChannel);
        list.remove(this);
    }

    @Override
    public void validate() {
        super.validate();

        if (!this.worldObj.isRemote) {
            this.updateChunks();
        }
        setWirelessSignal(this.xCoord, this.yCoord, this.zCoord, lastLevel_MAP.getOrDefault(this.getChannel(), 0));
        NGTLog.debug("TESC validate");
    }

    public int getChannel() {
        return this.signalOnTrue;
    }

    public int getChunkLoadRange() {
        return this.signalOnFalse;
    }

    @Override
    public void setSignalLevel(int par1, int par2) {
        super.setSignalLevel(par1, par2);
        if (this.worldObj == null || !this.worldObj.isRemote) {
            this.updateAntennaList();
        }
        setWirelessSignal(this.xCoord, this.yCoord, this.zCoord, lastLevel_MAP.getOrDefault(this.getChannel(), 0));
    }

    @Override
    public int getRSOutput() {
        return 0;
    }

    @Override
    public int getElectricity() {
        return this.signal;
    }

    @Override
    public void setElectricity(int x, int y, int z, int level) {
        List<TileEntitySC_WirelessAdvance> list = this.getList(this.getChannel());
        list.forEach(tile -> tile.setWirelessSignal(this.xCoord, this.yCoord, this.zCoord, level));
        lastLevel_MAP.put(this.getChannel(), level);
    }

    private void setWirelessSignal(int x, int y, int z, int level) {
        this.signal = level;
    }

    //**ChunkLoader*******************************************************************************/

    private ForgeChunkManager.Ticket ticket;
    private final Set<ChunkCoordIntPair> loadedChunks = new HashSet<>();
    private boolean finishSetup;

    /**
     * ServerTickごとに呼び出し
     */
    private void updateChunks() {
        if (this.isChunkLoaderEnable()) {
            this.forceChunkLoading();
        } else {
            this.releaseTicket();
        }
    }

    @Override
    public boolean isChunkLoaderEnable() {
        return this.getChunkLoadRange() > 0;
    }

    private void releaseTicket() {
        this.loadedChunks.clear();
        if (this.ticket != null) {
            ForgeChunkManager.releaseTicket(this.ticket);
            this.ticket = null;
        }
    }

    private boolean requestTicket() {
        ForgeChunkManager.Ticket chunkTicket = RTMChunkManager.INSTANCE.getNewTicket(this.worldObj, ForgeChunkManager.Type.NORMAL);
        if (chunkTicket != null) {
            int depth = this.getChunkLoadRange();
            chunkTicket.getModData();
            chunkTicket.setChunkListDepth(depth);
            RTMChunkManager.writeData(chunkTicket, this);
            this.setChunkTicket(chunkTicket);
            return true;
        }
        NGTLog.debug("[RTM] Failed to get ticket (Chunk Loader)");
        return false;
    }

    @Override
    public void setChunkTicket(ForgeChunkManager.Ticket par1) {
        if (this.ticket != par1) {
            ForgeChunkManager.releaseTicket(this.ticket);
        }
        this.ticket = par1;
        this.finishSetup = false;
    }

    @Override
    public void forceChunkLoading() {
        int cX = this.xCoord >> 4;
        int cZ = this.zCoord >> 4;
        this.forceChunkLoading(cX, cZ);
    }

    @Override
    public void forceChunkLoading(int x, int z) {
        if (!this.worldObj.isRemote) {
            if (this.ticket == null) {
                if (!this.requestTicket()) {
                    return;
                }
            }

            if (!this.finishSetup) {
                this.setupChunks(x, z);
                this.finishSetup = true;
            }

            //ForgeChunkManager.reorderChunk(this.ticket, chunk);//並び替え
            this.loadedChunks.forEach(chunk -> ForgeChunkManager.forceChunk(this.ticket, chunk));
            ChunkCoordIntPair myChunk = new ChunkCoordIntPair(x, z);//省くと機能しない
            ForgeChunkManager.forceChunk(this.ticket, myChunk);
        }
    }

    private void setupChunks(int xChunk, int zChunk) {
        int range = this.getChunkLoadRange();
        RTMChunkManager.INSTANCE.getChunksAround(this.loadedChunks, xChunk, zChunk, range);
    }
}




