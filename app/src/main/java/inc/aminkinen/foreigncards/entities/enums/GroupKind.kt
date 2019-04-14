package inc.aminkinen.foreigncards.entities.enums

enum class GroupKind(var value: Int) {
    Unknown(-1) {
        override fun toString() : String = "Unknown"
        override fun upKind() : GroupKind = Unknown
        override fun downKind() : GroupKind = Unknown
    },
    Red(0) {
        override fun toString() : String = "Red"
        override fun upKind() : GroupKind = Yellow
        override fun downKind() : GroupKind = Unknown
    },
    Yellow(1) {
        override fun toString() : String = "Yellow"
        override fun upKind() : GroupKind = Green
        override fun downKind() : GroupKind = Red
    },
    YellowRevise(2) {
        override fun toString() : String = "YellowRevise"
        override fun upKind() : GroupKind = Green
        override fun downKind() : GroupKind = Yellow
    },
    Green(3) {
        override fun toString() : String = "Green"
        override fun upKind() : GroupKind = Blue
        override fun downKind() : GroupKind = YellowRevise
    },
    Blue(4) {
        override fun toString() : String = "Blue"
        override fun upKind() : GroupKind = Unknown
        override fun downKind() : GroupKind = YellowRevise
    };

    open fun upKind() : GroupKind = this.upKind()
    open fun downKind() : GroupKind = this.downKind()

    companion object {
        fun fromInt(value: Int) = GroupKind.values().first { it.value == value }
    }
}