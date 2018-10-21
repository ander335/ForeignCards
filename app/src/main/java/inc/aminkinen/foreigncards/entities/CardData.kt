package inc.aminkinen.foreigncards.entities

open class CardData(var Word: String = "",
                    var Transl: String = "",
                    var Transc: String = "",
                    var Example: String = "",
                    var GroupId: Int = -1) {
    override fun toString() : String {
        return "$GroupId. $Word ($Transc) - $Transl ($Example)"
    }
}