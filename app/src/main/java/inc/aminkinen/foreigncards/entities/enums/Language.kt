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
    },
    Czech(2) {
        override fun toString() : String = "Czech"
    };

    companion object {
        fun fromInt(value: Int) = Language.values().first { it.value == value }
    }
}