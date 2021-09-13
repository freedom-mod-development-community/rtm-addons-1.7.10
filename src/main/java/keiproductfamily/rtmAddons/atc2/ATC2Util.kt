package keiproductfamily.rtmAddons.atc2

import jp.ngt.rtm.entity.train.EntityTrainBase
import jp.ngt.rtm.entity.train.util.Formation
import jp.ngt.rtm.entity.train.util.FormationEntry
import java.util.*

fun Formation.getControlCar(): EntityTrainBase {
    return Arrays.stream(this.entries)
        .filter { obj: FormationEntry? ->
        Objects.nonNull(
            obj
        ) }
        .map { entry: FormationEntry -> entry.train }
        .filter { obj: EntityTrainBase? -> Objects.nonNull(obj) }
        .filter { obj: EntityTrainBase -> obj.isControlCar }
        .findFirst().orElse(this[0].train)
}