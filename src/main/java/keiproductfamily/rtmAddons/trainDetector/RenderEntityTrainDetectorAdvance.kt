package keiproductfamily.rtmAddons.trainDetector

import cpw.mods.fml.client.FMLClientHandler
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import jp.ngt.ngtlib.renderer.model.IModelNGT
import jp.ngt.rtm.entity.EntityInstalledObject
import jp.ngt.rtm.modelpack.ModelPackManager
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12

@SideOnly(Side.CLIENT)
class RenderEntityTrainDetectorAdvance private constructor() : Render() {
    private val modelObj: IModelNGT by lazy {
        ModelPackManager.INSTANCE.loadModel("ATC01.mqo", 4, true, null)
    }
    var texture = ResourceLocation("textures/advanceTrainDetector.png")

    companion object {
        val INSTANCE = RenderEntityTrainDetectorAdvance()
    }

    private fun renderEntityTrainDetectorAdvance(
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
        FMLClientHandler.instance().client.renderEngine.bindTexture(texture)
        modelObj.renderAll(false)
        GL11.glPopMatrix()
    }

    override fun doRender(entity: Entity, par2: Double, par4: Double, par6: Double, par8: Float, par9: Float) {
        renderEntityTrainDetectorAdvance(entity as EntityTrainDetectorAdvance, par2, par4, par6, par8, par9)
    }

    override fun getEntityTexture(entity: Entity): ResourceLocation {
        return texture
    }
}
