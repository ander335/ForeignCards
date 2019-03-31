package inc.aminkinen.foreigncards.entities

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

class Settings(var GroupIdForAdding: Int = -1,
               var GroupIdForTraining: Int = -1,
               var GroupIdForMoving1: Int = -1,
               var GroupIdForMoving2: Int = -1,
               var CurrentLanguage: Language = Language.Unknown,
               var TrainMode_: TrainMode = TrainMode.Unknown)