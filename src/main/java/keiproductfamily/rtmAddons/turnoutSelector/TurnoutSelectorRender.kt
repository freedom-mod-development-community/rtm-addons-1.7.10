package keiproductfamily.rtmAddons.turnoutSelector

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity

class TurnoutSelectorRender(): TileEntitySpecialRenderer() {
    private val model = TurnoutSelectorModel()
    override fun renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, tick: Float) {
        this.model.render(tile as TurnoutSelectorTile, x, y, z)
    }
}