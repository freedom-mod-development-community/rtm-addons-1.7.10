package keiproductfamily.rtmAddons

enum class EnumForcedSignalMode(val id: Int, val force: Boolean) {
    Auto(0, false), ForceSelect(1, true);

    companion object {
        fun getType(id: Int): EnumForcedSignalMode {
            for (value in values()) {
                if (value.id == id) {
                    return value
                }
            }
            return Auto
        }
    }
}