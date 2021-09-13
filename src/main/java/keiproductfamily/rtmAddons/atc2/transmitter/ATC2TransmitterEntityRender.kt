package keiproductfamily.rtmAddons.atc2.transmitter

import jp.ngt.rtm.entity.EntityInstalledObject
import jp.ngt.rtm.modelpack.modelset.ModelSetMachineClient
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.MinecraftForgeClient
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12

class ATC2TransmitterEntityRender() : Render() {
    companion object {
        val INSTANCE = ATC2TransmitterEntityRender()
    }

    private fun renderEntityATC2(
        entity: EntityInstalledObject,
        x: Double,
        y: Double,
        z: Double,
        par8: Float,
        par9: Float
    ) {
        GL11.glPushMatrix()
        GL11.glEnable(GL12.GL_RESCALE_NORMAL)
        GL11.glTranslatef(x.toFloat(), y.toFloat(), z.toFloat())
        GL11.glRotatef(entity.rotationYaw, 0.0f, 1.0f, 0.0f)
        val modelSet = entity.modelSet as ModelSetMachineClient
        val cfg = modelSet.config
        val pass = MinecraftForgeClient.getRenderPass()
        modelSet.modelObj.render(entity, cfg, pass, par9)
        GL11.glPopMatrix()
    }

    override fun doRender(entity: Entity, par2: Double, par4: Double, par6: Double, par8: Float, par9: Float) {
        renderEntityATC2(entity as ATC2TransmitterEntity, par2, par4, par6, par8, par9)
    }

    override fun getEntityTexture(entity: Entity?): ResourceLocation? {
        return null
    }
}