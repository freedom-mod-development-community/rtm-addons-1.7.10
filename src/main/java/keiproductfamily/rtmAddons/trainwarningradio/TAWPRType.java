package keiproductfamily.rtmAddons.trainwarningradio;

public enum TAWPRType {
    TAW(1, 600),
    TPR(2, 600),
    ;
    int id;
    public int range;
    public int range2;

    TAWPRType(int id, int range) {
        this.id = id;
        this.range = range;
        this.range2 = range * range;
    }
}
