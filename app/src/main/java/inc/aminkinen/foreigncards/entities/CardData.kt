package inc.aminkinen.foreigncards.entities

open class CardData(val Word: String = "",
                    val Transl: String = "",
                    val Transc: String = "",
                    val Example: String = "",
                    val GroupId: Int = -1) {
    override fun toString() : String {
        return "$GroupId. $Word ($Transc) - $Transl ($Example)"
    }
}