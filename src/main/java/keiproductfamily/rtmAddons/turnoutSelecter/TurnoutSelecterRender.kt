package keiproductfamily.rtmAddons.turnoutSelecter

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity

class TurnoutSelecterRender(): TileEntitySpecialRenderer() {
    private val model = TurnoutSelecterModel()
    override fun renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, tick: Float) {
        this.model.render(tile as TurnoutSelecterTile, x, y, z)
    }
}