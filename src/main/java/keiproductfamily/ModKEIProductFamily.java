package keiproductfamily;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import keiproductfamily.PermissionList.IParmission;
import keiproductfamily.PermissionList.PermissionCompanyList;
import keiproductfamily.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.util.List;

@Mod(modid = ModKEIProductFamily.MOD_ID, name = "Kuma Electric Industry Product Family", version = Constants.version)
public class ModKEIProductFamily {
    public static final String MOD_ID = "KEIProductFamily";
    public static final String DOMAIN = "keiproductfamily";
    @Mod.Instance("KEIProductFamily")
    public static ModKEIProductFamily instance;
    public static Block creativeTabIcon;
    public static CreativeTabs keipfCreativeTabs = new CreativeTabKEIPF();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
//        creativeTabIcon = new ChunkLoadBlock();
//        GameRegistry.registerBlock(creativeTabIcon, "chunkLoadBlock");
//        GameRegistry.registerTileEntity(ChunkLoadTile.class, "ChunkLoadTile");
//        GameRegistry.registerBlock(new AdvanceChunkLoadBlock(), "AdvanceChunkLoadBlock");
//        GameRegistry.registerTileEntity(AdvanceChunkLoadTile.class, "AdvanceChunkLoadTile");
//        GameRegistry.registerItem(new TAWSendItem(), "TAWSendItem");
//        GameRegistry.registerItem(new TAWReceiveItem(), "TAWReceiveItem");
//        GameRegistry.registerBlock(new TAWReceiveBlock(), "TAWReceiveBlock");
//        GameRegistry.registerTileEntity(TAWReceiveBlockTile.class, "TAWReceiveTile");
//        GameRegistry.registerItem(new TPRSendItem(), "TPRSendItem");
//        GameRegistry.registerItem(new TPRReceiveItem(), "TPRReceiveItem");
//        GameRegistry.registerBlock(new TPRSendBlock(), "TPRSendBlock");
//        GameRegistry.registerTileEntity(TPRSendBlockTile.class, "TPRSendTile");


        ForgeChunkManager.setForcedChunkLoadingCallback(this, new ForgeChunkManager.LoadingCallback() {
            public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
                for (ForgeChunkManager.Ticket ticket : tickets) {
                    NBTTagCompound tags = ticket.getModData();
                    if (!tags.hasNoTags()) {
                        if (world != null) {
                            TileEntity tileEntity = world.getTileEntity(tags.getInteger("xCoord"), tags.getInteger("yCoord"), tags.getInteger("zCoord"));
                            if (tileEntity instanceof IChunkLoadHandler) {
                                ((IChunkLoadHandler) tileEntity).chunkLoaderInit(ticket);
                            }
                        }
                    }
                }
            }
        });

        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        PacketHandler.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    @SubscribeEvent
    public void WorldLoadEvent(WorldEvent.Load event) {
        if (!event.world.isRemote) {
            PermissionCompanyList.read();
        }
    }

    @SubscribeEvent
    public void onPlaceEvent(BlockEvent.PlaceEvent event) {
        if (event.block instanceof IParmission) {
            if (!PermissionCompanyList.canUseByKey(((IParmission) event.block).getName(), event.player)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (!event.world.isRemote) {
            if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR && event.entityPlayer.getHeldItem() != null && event.entityPlayer.getHeldItem().getItem() instanceof IParmission) {
                if (!PermissionCompanyList.canUseByKey(((IParmission) event.entityPlayer.getHeldItem().getItem()).getName(), event.entityPlayer)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
//            TAWMaster.updateTick();
//            TPRMaster.updateTick();
        }
    }

    /**
     * コマンド登録
     **/
    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandKEIPF());
    }
}