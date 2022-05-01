package keiproductfamily.rtmAddons.trainwarningradio.trainprotectionradio;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.Objects;

public class TPRSendBlockTile extends TileEntity {
    public TPRSendBlockTile() {
    }

    public int clickCoolTime = 0;

    private boolean power = false;

    public boolean getPower() {
        return power;
    }

    public void setPower(boolean power) {
        if (this.power != power) {
            this.power = power;
            int meta = power ? 1 : 0;
            if (this.worldObj != null) {
                this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, meta, 3);
                this.worldObj.scheduleBlockUpdate(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 2);
            }
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote && power) {
            if (this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord)) {
                this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 2, 3);
                TPRMaster.resetLostCnt(this);
            } else {
                this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 1, 3);
            }
            this.worldObj.scheduleBlockUpdate(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 2);
        }
        if (clickCoolTime > 0) {
            clickCoolTime--;
        }
    }

    private int tprID = -1;

    public int getTprID() {
        if (tprID < 1) {
            tprID = Objects.hash(xCoord, yCoord, zCoord);
        }
        return tprID;
    }


    @Override
    public void readFromNBT(NBTTagCompound p_145839_1_) {
        super.readFromNBT(p_145839_1_);
        this.power = p_145839_1_.getBoolean("power");
        this.tprID = p_145839_1_.getInteger("tprID");
        setPower(power);
    }

    @Override
    public void writeToNBT(NBTTagCompound p_145841_1_) {
        super.writeToNBT(p_145841_1_);
        p_145841_1_.setBoolean("power", power);
        p_145841_1_.setInteger("tprID", tprID);
    }

    /**
     * 同期用のパケット送信
     */
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, compound);
    }

    /**
     * 同期用のパケット受信
     */
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }
}
