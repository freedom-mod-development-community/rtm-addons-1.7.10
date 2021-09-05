package keiproductfamily

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11

/**
 * OpenGLによる描画処理ツール
 *
 * @author kuma_yamamoto
 */
object GLTool {
    private var mc: Minecraft? = null

    /*
    public static void drawCircle(double x, double y, double r, int c, boolean filled){
        float f =  c >> 16 & 0xFF;
        float f1 = c >> 8 & 0xFF;
        float f2 = c & 0xFF;
        float f3 = c >> 24 & 0xFF;
        drawCircle(x,y,r,f,f1,f2,f3,filled);
    }*/

    fun glColor(r: Int, g: Int, b: Int, a: Int): Int {
        var ret = 0
        ret += (r and 255) shl 16
        ret += (g and 255) shl 8
        ret += (b and 255)
        ret += (a and 255) shl 24
        return ret
    }

    fun glColorARGB(color: Int) {
        val a = (color shr 24 and 0xFF).toByte()
        val r = (color shr 16 and 0xFF).toByte()
        val g = (color shr 8 and 0xFF).toByte()
        val b = (color and 0xFF).toByte()
        GL11.glColor4ub(r, g, b, a)
    }

    fun glColorARGBWithAutoAlpha(color: Int) {
        var a = (color shr 24 and 0xFF).toByte()
        val r = (color shr 16 and 0xFF).toByte()
        val g = (color shr 8 and 0xFF).toByte()
        val b = (color and 0xFF).toByte()

        if (a == 0.toByte()) a = 0xFF.toByte()

        GL11.glColor4ub(r, g, b, a)
    }

    /**
     * @param x      中心座標X
     * @param y      中心座標Y
     * @param r      半径
     * @param red    赤(0～255)
     * @param green  緑(0～255)
     * @param blue   青(0～255)
     * @param alpha  不透明度((0～255)
     * @param filled 中を塗りつぶすか
     */
    fun drawCircle(
        x: Double,
        y: Double,
        r: Double,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        filled: Boolean
    ) {
        var colorRed = red
        var colorGreen = green
        var colorBlue = blue
        var colorAlpha = alpha
        colorRed /= 255.0f
        colorGreen /= 255.0f
        colorBlue /= 255.0f
        colorAlpha /= 255.0f
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glBlendFunc(770, 771)
        GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha)
        GL11.glBegin(if (filled) 6 else 2)
        var x2: Double
        var y2: Double
        for (i in 0..360) {
            x2 = kotlin.math.sin(i * Math.PI / 180.0) * r
            y2 = kotlin.math.cos(i * Math.PI / 180.0) * r
            GL11.glVertex2d(x + x2, y + y2)
        }
        GL11.glEnd()
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
    }

    /**
     * @param x      中心座標X
     * @param y      中心座標Y
     * @param r      半径
     * @param filled 中を塗りつぶすか
     */
    fun drawCircle(x: Double, y: Double, r: Double, filled: Boolean) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glBegin(if (filled) GL11.GL_TRIANGLE_FAN else GL11.GL_LINE_LOOP)
        for (i in 0..360) {
            val x2 = kotlin.math.sin(i * Math.PI / 180.0) * r
            val y2 = kotlin.math.cos(i * Math.PI / 180.0) * r
            GL11.glVertex2d(x + x2, y + y2)
        }
        GL11.glEnd()
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
    }

    /**
     * 描画処理方法指定
     */
    fun setOverlayBlending() {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glEnable(GL11.GL_BLEND)
        OpenGlHelper.glBlendFunc(770, 771, 1, 0)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    }

    /**
     * 直線
     */
    fun line(x1: Int, y1: Int, x2: Int, y2: Int, lineWidth: Float) {
        GL11.glLineWidth(lineWidth)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        //GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(770, 771)
        GL11.glBegin(GL11.GL_LINE_LOOP)
        GL11.glVertex2i(x1, y1)
        GL11.glVertex2i(x2, y2)
        GL11.glEnd()
        //GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
    }

    /**
     * 直線
     */
    fun line(x1: Double, y1: Double, x2: Double, y2: Double, lineWidth: Float) {
        GL11.glLineWidth(lineWidth)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        //GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(770, 771)
        GL11.glBegin(GL11.GL_LINE_LOOP)
        GL11.glVertex2d(x1, y1)
        GL11.glVertex2d(x2, y2)
        GL11.glEnd()
        //GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
    }

    /**
     * 長方形 外枠のみ
     */
    fun square2D(x1: Int, y1: Int, x2: Int, y2: Int, size: Float) {
        GL11.glLineWidth(size)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        //GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(770, 771)
        GL11.glBegin(GL11.GL_LINE_LOOP)
        GL11.glVertex2i(x1, y1)
        GL11.glVertex2i(x2, y1)
        GL11.glVertex2i(x2, y2)
        GL11.glVertex2i(x1, y2)
        GL11.glEnd()
        //GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
    }

    /**
     * 長方形 中塗りつぶし
     */
    fun squareFill2D(x1: Int, y1: Int, x2: Int, y2: Int) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        //GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(770, 771)
        GL11.glBegin(6)
        GL11.glVertex2d(x1.toDouble(), y2.toDouble())
        GL11.glVertex2d(x2.toDouble(), y2.toDouble())
        GL11.glVertex2d(x2.toDouble(), y1.toDouble())
        GL11.glVertex2d(x1.toDouble(), y1.toDouble())
        GL11.glEnd()
        //GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
    }

//    fun squareVertex(a: Vec2D, b: Vec2D) {
//        GL11.glVertex2d(a.x, a.y)
//        GL11.glVertex2d(a.x, b.y)
//        GL11.glVertex2d(b.x, b.y)
//        GL11.glVertex2d(b.x, a.y)
//    }

    /**
     * 文字列を中央揃えで描画
     *
     * @param fontRenderer FontRenderer
     * @param string       表示する文字列
     * @param x            中心座標X
     * @param y            中心座標Y
     * @param color        色 [Tool.intCollar]
     * @param shadow       影をつけるか
     */
    fun drawCenteredString(
        fontRenderer: FontRenderer,
        string: String,
        x: Int,
        y: Int,
        color: Int,
        shadow: Boolean = true
    ) {
        if (shadow) {
            fontRenderer.drawStringWithShadow(string, x - fontRenderer.getStringWidth(string) / 2, y, color)
        } else {
            fontRenderer.drawString(string, x - fontRenderer.getStringWidth(string) / 2, y, color)
        }
    }

    fun drawString(fontRenderer: FontRenderer, str: String?, x: Double, y: Double, color: Int, shadow: Boolean): Int {
        GL11.glTranslated(x, y, 0.0)
        val ret = fontRenderer.drawString(str, 0, 0, color, shadow)
        GL11.glTranslated(-x, -y, 0.0)
        return ret
    }

    /**
     * Minecraftの拡大率を取得
     */
    private fun magnificationRateWidth(): Double {
        mc = Minecraft.getMinecraft()
        return mc!!.displayWidth.toDouble() / ScaledResolution(mc!!, mc!!.displayWidth, mc!!.displayHeight).scaledWidth
    }

    /**
     * Minecraftの拡大率を取得
     */
    private fun magnificationRateHeight(): Double {
        mc = Minecraft.getMinecraft()
        return mc!!.displayHeight.toDouble() / ScaledResolution(
            mc!!,
            mc!!.displayWidth,
            mc!!.displayHeight
        ).scaledHeight
    }

    /**
     * 描画範囲を指定
     */
    fun glViewport(x: Int, y: Int, width: Int, height: Int) {
        val mrW = magnificationRateWidth()
        val mrH = magnificationRateHeight()
        val afterWidth = (mrW * width).toInt()
        val afterHeight = (mrH * height).toInt()
        val viewport = BufferUtils.createIntBuffer(16)
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport)
        GL11.glViewport((mrW * x).toInt(), (mrH * y).toInt(), afterWidth, afterHeight)
        GL11.glScalef(viewport.get(2).toFloat() / afterWidth, viewport.get(3).toFloat() / afterHeight, 1f)
    }


    /**
     * 256x256のテクスチャ前提で四角を描画する
     */
    fun drawTexturedModalRect(x: Double, y: Double, u: Double, v: Double, w: Double, h: Double) {
        val f = 1 / 256f
        val f1 = 1 / 256f
        val tessellator = Tessellator.instance
        tessellator.startDrawingQuads()
        tessellator.addVertexWithUV(x + 0, y + h, .0, (u + 0) * f, (v + h) * f1)
        tessellator.addVertexWithUV(x + w, y + h, .0, (u + w) * f, (v + h) * f1)
        tessellator.addVertexWithUV(x + w, y + 0, .0, (u + w) * f, (v + 0) * f1)
        tessellator.addVertexWithUV(x + 0, y + 0, .0, (u + 0) * f, (v + 0) * f1)
        tessellator.draw()
    }

    /**
     * 256x256のテクスチャ前提で四角を描画する
     */
    fun drawTexturedModalRect(x: Int, y: Int, u: Int, v: Int, w: Int, h: Int) {
        drawTexturedModalRect(x.toDouble(), y.toDouble(), u.toDouble(), v.toDouble(), w.toDouble(), h.toDouble())
    }
}
