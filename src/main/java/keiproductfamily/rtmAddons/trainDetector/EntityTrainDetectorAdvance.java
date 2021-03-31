package keiproductfamily.rtmAddons.trainDetector;

import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.electric.EntityElectricalWiring;
import jp.ngt.rtm.electric.SignalLevel;
import jp.ngt.rtm.entity.train.EntityBogie;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;
import jp.ngt.rtm.item.ItemWire;
import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import keiproductfamily.GuiIDs;
import keiproductfamily.ModKEIProductFamily;
import keiproductfamily.rtmAddons.RTMAChannelData;
import keiproductfamily.rtmAddons.RTMAChannelMaster;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class EntityTrainDetectorAdvance extends EntityElectricalWiring {
    private boolean findTrain;
    private String _channelName = "";
    private int _channelNumber = 0;

    public EntityTrainDetectorAdvance(World world) {
        super(world);
        this.setSize(1.0F, 0.0625F);
        this.ignoreFrustumCheck = true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setString("channelName", _channelName);
        nbt.setInteger("channelNumber", _channelNumber);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this._channelName = nbt.getString("channelName");
        this._channelNumber = nbt.getInteger("channelNumber");
    }

    public String getChannelName() {
        return _channelName;
    }

    public int getChannelNumber() {
        return _channelNumber;
    }

    public String getChannelKey() {
        return _channelName + _channelNumber;
    }

    public void setChunnelName(String chunnelName, int channelNumber) {
        chunnelName = chunnelName.trim();
        if (!_channelName.equals(chunnelName) || _channelNumber != channelNumber) {
            if (!chunnelName.equals("")) {
                this._channelName = chunnelName;
                this._channelNumber = channelNumber;
            }
        }
    }

    @Override
    public void onUpdate() {
        if (!this.worldObj.isRemote) {
            if (_channelNumber > 0) {
                RTMAChannelData channelData = RTMAChannelMaster.getChannelData(getChannelKey());
                if (channelData != null) {
                    EntityTrainBase train = getCollidedTrain();
                    if (train != null) {
                        byte rollSignID = train.getTrainStateData(8);
                        channelData.setTrainData(getSignalLevel(), rollSignID);
                    } else {
                        channelData.setTrainData(getSignalLevel(), (byte) -1);
                    }
                }
            }
        }

        super.onUpdate();
    }

    public EntityTrainBase getCollidedTrain() {
        List collideEntityList = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().expand(0, 2, 0));
        if (collideEntityList != null) {
            for (Object entity : collideEntityList) {
                if (entity instanceof EntityTrainBase) {
                    findTrain = true;
                    return ((EntityTrainBase) entity).getFormation().get(0).train;
                }
                if (entity instanceof EntityBogie) {
                    findTrain = true;
                    return ((EntityBogie) entity).getTrain().getFormation().get(0).train;
                }
            }
        }
        findTrain = false;
        return null;
    }

    @Override
    public boolean interactFirst(EntityPlayer player) {
        if (player.getHeldItem() != null || !(player.getHeldItem().getItem() instanceof ItemWire)) {
            if (worldObj.isRemote) {
                player.openGui(ModKEIProductFamily.instance, GuiIDs.GuiID_EntityTrainDetectorSetting, worldObj, this.getEntityId(), 0, 0);
            }
            return true;
        }
        return super.interactFirst(player);
    }

    @Override
    public int getElectricity() {
        return this.findTrain ? SignalLevel.STOP.level : SignalLevel.PROCEED.level;
    }

    private SignalLevel getSignalLevel() {
        return this.findTrain ? SignalLevel.STOP : SignalLevel.PROCEED;
    }

    @Override
    public void setElectricity(int par1) {
    }

    @Override
    protected void dropItems() {
        //Todo
        this.entityDropItem(new ItemStack(ModKEIProductFamily.itemTrainDetectorAdvance), 0.0F);
    }

    @Override
    public String getSubType() {
        return "Antenna_Receive";
    }

    @Override
    protected String getDefaultName() {
        return "TrainDetector_01";
    }

    @Override
    protected ItemStack getItem() {
        return new ItemStack(ModKEIProductFamily.itemTrainDetectorAdvance);
    }
}