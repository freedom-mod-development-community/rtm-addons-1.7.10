package keiproductfamily.rtmAddons;

import jp.ngt.rtm.electric.SignalLevel;

public class RTMAChannelData {
    SignalLevel signalLevel = SignalLevel.PROCEED;
    byte rollSignID = -1;
    ForcedTurnoutSelection forcedTurnoutSelection = ForcedTurnoutSelection.Auto;

    public void setTrainData(SignalLevel signalLevel, byte rollSignID){
        if(this.signalLevel != signalLevel || this.rollSignID != rollSignID){
            this.signalLevel = signalLevel;
            this.rollSignID = rollSignID;
        }
    }

    public void setForceSelect(ForcedTurnoutSelection selection){
        if(this.forcedTurnoutSelection != selection){
            this.forcedTurnoutSelection = selection;
        }
    }
}
