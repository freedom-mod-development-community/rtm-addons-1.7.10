package keiproductfamily.rtmAddons.trainwarningradio;

public interface ITAWPRReceiverTile {
    double posX();

    double posZ();

    void setReceiveData(boolean isReceived, int receiveLevel);
}
