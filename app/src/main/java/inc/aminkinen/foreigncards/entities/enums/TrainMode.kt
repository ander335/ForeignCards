package inc.aminkinen.foreigncards.entities.enums

enum class TrainMode(var value: Int) {
    Unknown(-1) {
        override fun toString() : String = "Unknown"
    },
    FirstlyWord(0) {
        override fun toString() : String = "FirstlyWord"
    },
    FirstlyTranslation(1) {
        override fun toString() : String = "FirstlyTranslation"
    };

    companion object {
        fun fromInt(value: Int) = TrainMode.values().first { it.value == value }
    }
}