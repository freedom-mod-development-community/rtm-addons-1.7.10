package keiproductfamily.rtmAddons.trainDetector;

import jp.ngt.rtm.entity.EntityInstalledObject;
import jp.ngt.rtm.modelpack.cfg.MachineConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetMachineClient;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderEntityTrainDetectorAdvance extends Render {
    public static final RenderEntityTrainDetectorAdvance INSTANCE = new RenderEntityTrainDetectorAdvance();

    private RenderEntityTrainDetectorAdvance() {
    }

    private void renderEntityTrainDetectorAdvance(EntityInstalledObject entity, double x, double y, double z, float par8, float par9) {
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(entity.rotationYaw, 0.0F, 1.0F, 0.0F);

        ModelSetMachineClient modelSet = (ModelSetMachineClient) entity.getModelSet();
        MachineConfig cfg = modelSet.getConfig();
        int pass = MinecraftForgeClient.getRenderPass();
        modelSet.modelObj.render(entity, cfg, pass, par9);

        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9) {
        this.renderEntityTrainDetectorAdvance((EntityTrainDetectorAdvance) entity, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}