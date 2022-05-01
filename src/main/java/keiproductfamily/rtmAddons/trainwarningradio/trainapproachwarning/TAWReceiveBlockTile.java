package keiproductfamily.rtmAddons.trainwarningradio.trainapproachwarning;

import keiproductfamily.network.PacketHandler;
import keiproductfamily.rtmAddons.trainwarningradio.ITAWPRReceiverTile;
import keiproductfamily.rtmAddons.trainwarningradio.ReceiveDataSyncMessage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TAWReceiveBlockTile extends TileEntity implements ITAWPRReceiverTile {
    public TAWReceiveBlockTile() {
    }

    public int clickCoolTime = 0;


    @Override
    public double posX() {
        return this.xCoord + 0.5;
    }

    @Override
    public double posZ() {
        return this.zCoord + 0.5;
    }

    private boolean _isReceived = false;

    private int _receiveLevel = 0;

    public boolean isReceived() {
        return _isReceived;
    }

    public int getReceiveLevel() {
        return this._receiveLevel;
    }

    @Override
    public void setReceiveData(boolean isReceived, int receiveLevel) {
        if (_isReceived != isReceived || _receiveLevel != receiveLevel) {
            _isReceived = isReceived;
            _receiveLevel = receiveLevel;

            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            this.worldObj.notifyBlockChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());

            if (!this.worldObj.isRemote) {
                PacketHandler.sendPacketAll(new ReceiveDataSyncMessage(this, isReceived, receiveLevel));
            }
            this.markDirty();
        }
    }


    private boolean power = false;

    public boolean getPower() {
        return power;
    }

    public void setPower(boolean power) {
        if (this.power != power) {
            this.power = power;
            int meta = power ? 1 : 0;
            if (!power) {
                setReceiveData(false, 0);
            }
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
            if (TAWMaster.checkReceive(this)) {
                _isReceived = true;
                this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 2, 3);
            } else {
                _isReceived = false;
                this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 1, 3);
            }
            this.worldObj.scheduleBlockUpdate(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 2);
        }
        if (clickCoolTime > 0) {
            clickCoolTime--;
        }
    }


    @Override
    public void readFromNBT(NBTTagCompound p_145839_1_) {
        super.readFromNBT(p_145839_1_);
        this.power = p_145839_1_.getBoolean("power");
        setPower(power);
    }

    @Override
    public void writeToNBT(NBTTagCompound p_145841_1_) {
        super.writeToNBT(p_145841_1_);
        p_145841_1_.setBoolean("power", power);
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
