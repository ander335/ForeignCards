package inc.aminkinen.foreigncards.entities

class Card(val Id : Int,
           Word: String,
           Transl: String,
           Transc: String,
           Example: String,
           GroupId: Int,
           Lang: Language) : CardData(Word, Transl, Transc, Example, GroupId, Lang)