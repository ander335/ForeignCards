package inc.aminkinen.foreigncards.entities

import inc.aminkinen.foreigncards.entities.enums.Language

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