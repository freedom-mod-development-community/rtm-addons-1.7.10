package keiproductfamily.rtmAddons

enum class EnumTurnOutSyncSelection(val id: Int)  {
    OFF(0), Left(1), Right(2);

    companion object {
        fun getType(id: Int): EnumTurnOutSyncSelection {
            for (value in values()) {
                if (value.id == id) {
                    return value
                }
            }
            return OFF
        }
    }

    fun getNext(): EnumTurnOutSyncSelection {
        return getType((id+1) % values().size)
    }

    fun toEnumTurnOutSwitch(): EnumTurnOutSwitch{
        return EnumTurnOutSwitch.getType(id)
    }
}