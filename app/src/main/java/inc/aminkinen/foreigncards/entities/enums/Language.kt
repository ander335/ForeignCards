package inc.aminkinen.foreigncards.entities.enums

enum class Language(var value: Int) {
    Unknown(-1) {
        override fun toString() : String = "Unknown"
    },
    English(0) {
        override fun toString() : String = "English"
    },
    Finish(1) {
        override fun toString() : String = "Finish"
    };

    companion object {
        fun fromInt(value: Int) = Language.values().first { it.value == value }
    }
}