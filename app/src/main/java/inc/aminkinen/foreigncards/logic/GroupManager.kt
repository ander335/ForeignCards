package inc.aminkinen.foreigncards.logic

import inc.aminkinen.foreigncards.Logger
import inc.aminkinen.foreigncards.entities.enums.GroupKind

class GroupManager {
    companion object {
        fun getUpGroup(groupId : Int, filling: Map<Int, Int>) : Int? {
            val kind = getKind(groupId)
            val upKind = kind.upKind()
            if (upKind == GroupKind.Unknown)
                return null

            return getSuitableGroupId(upKind, filling)
        }

        fun getDownGroup(groupId : Int, filling: Map<Int, Int>) : Int? {
            val kind = getKind(groupId)
            val downKind = kind.downKind()
            if (downKind == GroupKind.Unknown)
                return null

            return getSuitableGroupId(downKind, filling)
        }

        fun findLowestGroup(groupKind: GroupKind, filling: Map<Int, Int>) : Int {
            var minSize = Int.MAX_VALUE
            var targetId = -1

            val range = getKindRange(groupKind)
            for (i in range.first until range.second) {
                val size = filling.getOrElse(i) { 0 }
                if (size == 0)
                    return i

                if (size < minSize) {
                    minSize = size
                    targetId = i
                }
            }

            return targetId
        }

        private fun getSuitableGroupId(groupKind: GroupKind, filling: Map<Int, Int>) : Int? {
            val threshold = getKindLimit(groupKind)

            var minSize = threshold
            var targetId = -1
            var firstEmpty = -1

            val range = getKindRange(groupKind)
            for (i in range.first until range.second) {
                val size = filling.getOrElse(i) { 0 }
                if (size >= threshold)
                    continue
                if (size == 0) {
                    if (firstEmpty == -1)
                        firstEmpty = i
                    continue
                }

                if (size < minSize) {
                    minSize = size
                    targetId = i
                }
            }

            if (targetId == -1 && firstEmpty == -1) {
                if (groupKind != GroupKind.YellowRevise && groupKind != GroupKind.Yellow) {
                    return null
                }

                var minSize = Int.MAX_VALUE
                var targetId = -1
                for (i in range.first until range.second) {
                    val size = filling.getOrElse(i) { 0 }
                    if (size < minSize) {
                        minSize = size
                        targetId = i
                    }
                }
                return targetId
            }
            if (targetId == -1)
                return firstEmpty
            return targetId
        }

        fun getKind(groupId: Int) : GroupKind {
            if (groupId < RedThreshold)
                return GroupKind.Unknown
            if (groupId < YellowThreshold)
                return GroupKind.Red
            if (groupId < YellowReviseThreshold)
                return GroupKind.Yellow
            if (groupId < GreenThreshold)
                return GroupKind.YellowRevise
            if (groupId < BlueThreshold)
                return GroupKind.Green
            return GroupKind.Blue
        }

        fun getKindRange(groupKind: GroupKind) : Pair<Int, Int> {
            if (groupKind == GroupKind.Red)
                return Pair(RedThreshold, YellowThreshold)
            if (groupKind == GroupKind.Yellow)
                return Pair(YellowThreshold, YellowReviseThreshold)
            if (groupKind == GroupKind.YellowRevise)
                return Pair(YellowReviseThreshold, GreenThreshold)
            if (groupKind == GroupKind.Green)
                return Pair(GreenThreshold, BlueThreshold)
            if (groupKind == GroupKind.Blue)
                // TODO: fix it
                return Pair(BlueThreshold, 100)
            return Pair(0, 0)
        }

        fun getKindLimit(groupKind: GroupKind) : Int {
            if (groupKind == GroupKind.YellowRevise || groupKind == GroupKind.Yellow)
                return YellowsPreferred

            if (groupKind == GroupKind.Green)
                return GreenMaxSize
            if (groupKind == GroupKind.Blue)
                return BlueMaxSize

            return Int.MAX_VALUE
        }

        const val RedThreshold = 0
        const val YellowThreshold = 1
        const val YellowReviseThreshold = 2
        const val GreenThreshold = 4
        const val BlueThreshold = 20

        const val YellowsPreferred = 100
        const val GreenMaxSize = 150
        const val BlueMaxSize = 200

        private val Log : Logger = Logger("GroupManager")
    }
}
