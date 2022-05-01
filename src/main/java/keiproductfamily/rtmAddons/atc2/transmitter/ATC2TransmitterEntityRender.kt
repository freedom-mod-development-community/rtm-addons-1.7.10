package keiproductfamily.rtmAddons.atc2.transmitter

import cpw.mods.fml.client.FMLClientHandler
import jp.ngt.ngtlib.renderer.model.IModelNGT
import jp.ngt.rtm.entity.EntityInstalledObject
import jp.ngt.rtm.modelpack.ModelPackManager
import jp.ngt.rtm.modelpack.cfg.MachineConfig
import jp.ngt.rtm.modelpack.cfg.ModelConfig
import jp.ngt.rtm.modelpack.modelset.ModelSetMachineClient
import jp.ngt.rtm.render.ModelObject
import jp.ngt.rtm.render.PartsRenderer
import keiproductfamily.ModKEIProductFamily
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.client.model.AdvancedModelLoader
import net.minecraftforge.client.model.IModelCustom
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12

class ATC2TransmitterEntityRender() : Render() {
    val modelObj: IModelNGT by lazy {
        ModelPackManager.INSTANCE.loadModel("ATC02.mqo", 4, true, null)
    }
    val texture: ResourceLocation = ResourceLocation("textures/atc2.png")

    companion object {
        val INSTANCE = ATC2TransmitterEntityRender()
    }

    private fun renderEntityATC2(
        entity: ATC2TransmitterEntity,
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
        FMLClientHandler.instance().client.renderEngine.bindTexture(texture)
        modelObj.renderAll(false)
        GL11.glPopMatrix()
    }

    override fun doRender(entity: Entity, par2: Double, par4: Double, par6: Double, par8: Float, par9: Float) {
        renderEntityATC2(entity as ATC2TransmitterEntity, par2, par4, par6, par8, par9)
    }

    override fun getEntityTexture(entity: Entity?): ResourceLocation? {
        return texture
    }
}