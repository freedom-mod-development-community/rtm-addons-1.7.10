package keiproductfamily.rtmAddons

enum class EnumTurnOutSwitch(val id: Int) {
    Left(1), Right(2);

    companion object {
        fun getType(id: Int): EnumTurnOutSwitch {
            for (value in values()) {
                if (value.id == id) {
                    return value
                }
            }
            return Right
        }
    }

    fun toEnumTurnOutSyncSelection(): EnumTurnOutSyncSelection{
        return EnumTurnOutSyncSelection.getType(id)
    }
}