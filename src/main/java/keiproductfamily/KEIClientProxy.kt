package keiproductfamily

import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.client.registry.RenderingRegistry
import keiproductfamily.rtmAddons.trainDetector.EntityTrainDetectorAdvance
import keiproductfamily.rtmAddons.trainDetector.RenderEntityTrainDetectorAdvance
import keiproductfamily.rtmAddons.turnoutSelecter.TurnoutSelecterRender
import keiproductfamily.rtmAddons.turnoutSelecter.TurnoutSelecterTile

class KEIClientProxy : KEIProxy() {
    public override fun preInit() {
        RenderingRegistry.registerEntityRenderingHandler(
            EntityTrainDetectorAdvance::class.java,
            RenderEntityTrainDetectorAdvance.INSTANCE
        )
    }

    public override fun Init() {
        ClientRegistry.bindTileEntitySpecialRenderer(TurnoutSelecterTile::class.java, TurnoutSelecterRender())
    }
}