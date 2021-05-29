package keiproductfamily.rtmAddons.turnoutSelecter

import cpw.mods.fml.client.FMLClientHandler
import keiproductfamily.ModKEIProductFamily
import keiproductfamily.rtmAddons.EnumTurnOutSyncSelection
import net.minecraft.client.model.ModelBase
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.AdvancedModelLoader
import net.minecraftforge.client.model.IModelCustom
import org.lwjgl.opengl.GL11

class TurnoutSelecterModel : ModelBase() {
    private val model: IModelCustom by lazy {
        AdvancedModelLoader.loadModel(
            ResourceLocation(
                ModKEIProductFamily.DOMAIN,
                "models/turnoutSelecter.obj"
            )
        )
    }
    private val textureblackMetalic = ResourceLocation(ModKEIProductFamily.DOMAIN, "textures/model/black-metallic.png")
    private val texturesilcerMetalic = ResourceLocation(ModKEIProductFamily.DOMAIN, "textures/model/silver-metallic.png")

    fun render(tile: TurnoutSelecterTile, x: Double, y: Double, z: Double) {
        GL11.glPushMatrix()
        GL11.glTranslated(x + 0.5, y, z + 0.5)
        FMLClientHandler.instance().client.renderEngine.bindTexture(this.textureblackMetalic)
        this.model.renderPart("base")
        FMLClientHandler.instance().client.renderEngine.bindTexture(this.texturesilcerMetalic)
        this.model.renderPart("basesilver")
        FMLClientHandler.instance().client.renderEngine.bindTexture(this.textureblackMetalic)
        GL11.glTranslated(0.0, 0.32, 0.0)
        val ang = when(tile.turnOutSelection){
            EnumTurnOutSyncSelection.Left -> 45.0
            EnumTurnOutSyncSelection.OFF  -> 0.0
            EnumTurnOutSyncSelection.Right -> -45.0
        }
        GL11.glRotated(ang, 0.0, 0.0, 1.0)
        this.model.renderPart("hand")
        FMLClientHandler.instance().client.renderEngine.bindTexture(this.texturesilcerMetalic)
        this.model.renderPart("handsilver")
        GL11.glPopMatrix()
    }
}