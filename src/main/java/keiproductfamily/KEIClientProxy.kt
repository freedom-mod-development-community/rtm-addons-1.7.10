package keiproductfamily

import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.client.registry.RenderingRegistry
import keiproductfamily.rtmAddons.atc2.transmitter.ATC2TransmitterEntity
import keiproductfamily.rtmAddons.atc2.transmitter.ATC2TransmitterEntityRender
import keiproductfamily.rtmAddons.trainDetector.EntityTrainDetectorAdvance
import keiproductfamily.rtmAddons.trainDetector.RenderEntityTrainDetectorAdvance
import keiproductfamily.rtmAddons.turnoutSelector.TurnoutSelectorRender
import keiproductfamily.rtmAddons.turnoutSelector.TurnoutSelectorTile

class KEIClientProxy : KEIProxy() {
    public override fun preInit() {
        RenderingRegistry.registerEntityRenderingHandler(
            EntityTrainDetectorAdvance::class.java,
            RenderEntityTrainDetectorAdvance.INSTANCE
        )
        RenderingRegistry.registerEntityRenderingHandler(
            ATC2TransmitterEntity::class.java,
            ATC2TransmitterEntityRender.INSTANCE
        )
    }

    public override fun Init() {
        ClientRegistry.bindTileEntitySpecialRenderer(TurnoutSelectorTile::class.java, TurnoutSelectorRender())
    }
}
