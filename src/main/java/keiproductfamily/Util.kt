package keiproductfamily

object Util {
    fun intCollar(red: Int, green: Int, blue: Int, alpha: Int): Int {
        val f = red shl 16
        val f1 = green shl 8
        val f2 = blue shl 0
        val f3 = alpha shl 24
        return f + f1 + f2 + f3
    }
}