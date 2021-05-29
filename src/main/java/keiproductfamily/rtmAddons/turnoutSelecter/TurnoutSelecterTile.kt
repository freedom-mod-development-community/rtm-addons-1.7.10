package keiproductfamily.rtmAddons.turnoutSelecter

import keiproductfamily.rtmAddons.ChannelKeyPair
import keiproductfamily.rtmAddons.EnumTurnOutSwitch
import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection
import keiproductfamily.rtmAddons.detectorChannel.RTMDetectorChannelMaster
import keiproductfamily.rtmAddons.turnoutChannel.RTMTurnoutChannelMaster
import net.minecraft.tileentity.TileEntity

class TurnoutSelecterTile : TileEntity() {
    /**
     * 動かす対象の分岐名称
     */
    var turnoutChannelKeyPair = ChannelKeyPair("", "")


    /**
     * 選択している強制分岐選択モード
     */
    var turnOutSelection = EnumTurnOutSyncSelection.OFF

    var isUpdate = false

    fun nextTurnOutSelection(){
        turnOutSelection = turnOutSelection.getNext()
        RTMTurnoutChannelMaster.getChannelData(turnoutChannelKeyPair.keyString)?.setTurnOutForceData(turnOutSelection)
    }

    fun setTurnoutChunnelKey(channelKey: ChannelKeyPair) {
        this.turnoutChannelKeyPair = channelKey
        isUpdate = true
    }
}