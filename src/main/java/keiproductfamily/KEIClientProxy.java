package keiproductfamily;

import cpw.mods.fml.client.registry.RenderingRegistry;
import keiproductfamily.rtmAddons.trainDetector.EntityTrainDetectorAdvance;
import keiproductfamily.rtmAddons.trainDetector.RenderEntityTrainDetectorAdvance;

public class KEIClientProxy extends KEIProxy {
    @Override
    public void preInit() {
        RenderingRegistry.registerEntityRenderingHandler(EntityTrainDetectorAdvance.class, RenderEntityTrainDetectorAdvance.INSTANCE);
    }
}
