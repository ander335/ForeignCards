package inc.aminkinen.foreigncards.entities

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

open class CardData(var Word: String = "",
                    var Transl: String = "",
                    var Transc: String = "",
                    var Example: String = "",
                    var GroupId: Int = -1,
                    var Lang: Language = Language.Unknown) {
    override fun toString() : String {
        return "[$Lang] Group: $GroupId. $Word ($Transc) - $Transl ($Example)"
    }
}