package inc.aminkinen.foreigncards.entities

class Settings(var GroupIdForAdding: Int = -1,
               var GroupIdForTraining: Int = -1,
               var GroupIdForMoving1: Int = -1,
               var GroupIdForMoving2: Int = -1,
               var CurrentLanguage: Language = Language.Unknown)