package keiproductfamily.rtmAddons.detectorChannel

enum class EnumDirection(val id: Byte) {
    Access(1), Elimination(-1), Null(0);

    fun getType(id: Byte): EnumDirection {
        for (value in values()) {
            if (value.id == id) {
                return value
            }
        }
        return Null
    }
}
