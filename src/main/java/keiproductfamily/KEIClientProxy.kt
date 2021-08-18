package keiproductfamily

import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.client.registry.RenderingRegistry
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
    }

    public override fun Init() {
        ClientRegistry.bindTileEntitySpecialRenderer(TurnoutSelectorTile::class.java, TurnoutSelectorRender())
    }
}