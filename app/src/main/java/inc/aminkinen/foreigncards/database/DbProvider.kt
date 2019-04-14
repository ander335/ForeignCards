package inc.aminkinen.foreigncards.database

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import inc.aminkinen.foreigncards.entities.*
import inc.aminkinen.foreigncards.entities.enums.*
import java.util.*

class DbProvider(ctx : Context) {
    private val db: SQLiteDatabase = init(ctx)

    private fun init(ctx : Context) : SQLiteDatabase {
        try {
            val helper = DbHelper(ctx)
            val db = helper.writableDatabase
            if (!helper.needUpdate)
                return db

            db.close()
            helper.updateDataBase(ctx)
            return helper.writableDatabase
        } catch (ex: Exception) {
            throw Error("Unable to mount database", ex)
        }
    }

    fun addCard(c: CardData) {
        val v = ContentValues()
        v.put("Word", c.Word)
        v.put("Transc", c.Transc)
        v.put("Transl", c.Transl)
        if (c.GroupId >= 0)
            v.put("GroupId", c.GroupId)
        v.put("Example", c.Example)
        v.put("Language", c.Lang.value)

        db.insert("Cards", null, v)
    }

    fun addView(view: View) {
        val v = ContentValues()
        v.put("CardId", view.CardId)
        v.put("Duration", view.Duration)
        v.put("Time", view.Time)
        if (view.MovingGroupId >= 0)
            v.put("MovingGroupId", view.MovingGroupId)

        db.insert("Views", null, v)
    }

    fun removeCard(id: Int) {
        db.execSQL("DELETE FROM Cards WHERE Id = ?", arrayOf<Any>(id))
        db.execSQL("DELETE FROM Views WHERE CardId = ?", arrayOf<Any>(id))
    }

    fun updateCard(c: Card) {
        val v = ContentValues()
        v.put("Word", c.Word)
        v.put("Transc", c.Transc)
        v.put("Transl", c.Transl)
        if (c.GroupId >= 0)
            v.put("GroupId", c.GroupId)
        v.put("Example", c.Example)
        v.put("Language", c.Lang.value)

        db.update("Cards", v, "Id = ?", arrayOf(Integer.toString(c.Id)))
    }

    fun cardsCount(Lang: Language): Int {
        return DatabaseUtils.queryNumEntries(db, "Cards", "Language=?", arrayOf(Integer.toString(Lang.value))).toInt()
    }

    fun getCards(groupId: Int, Lang: Language): ArrayList<Card> {
        val result = ArrayList<Card>()
        val q = "SELECT * FROM Cards WHERE GroupId = $groupId AND Language = ${Lang.value}"

        val c = db.rawQuery(q, null)
        c.moveToFirst()
        while (!c.isAfterLast) {
            val groupIdx = c.getColumnIndex("GroupId")
            val gId = if (c.isNull(groupIdx)) -1 else c.getInt(groupIdx)
            val exampleIdx = c.getColumnIndex("Example")
            val example = if (c.isNull(exampleIdx)) "" else c.getString(exampleIdx)

            val a = Card(
                    c.getInt(c.getColumnIndex("Id")),
                    c.getString(c.getColumnIndex("Word")),
                    c.getString(c.getColumnIndex("Transl")),
                    c.getString(c.getColumnIndex("Transc")),
                    example,
                    gId,
                    Language.fromInt(c.getInt(c.getColumnIndex("Language"))))

            result.add(a)
            c.moveToNext()
        }
        c.close()

        return result
    }

    fun getSettings(): Settings {
        val c = db.rawQuery("SELECT * FROM Settings", null)
        c.moveToFirst()

        val result = Settings(
                c.getInt(c.getColumnIndex("GroupIdForAdding")),
                c.getInt(c.getColumnIndex("GroupIdForTraining")),
                c.getInt(c.getColumnIndex("GroupIdForMoving1")),
                c.getInt(c.getColumnIndex("GroupIdForMoving2")),
                c.getInt(c.getColumnIndex("GreenReceiveGroupId")),
                Language.fromInt(c.getInt(c.getColumnIndex("CurrentLanguage"))),
                TrainMode.fromInt(c.getInt(c.getColumnIndex("TrainMode"))))
        c.close()

        return result
    }

    fun updateSettings(s: Settings) {
        val v = ContentValues()
        v.put("GroupIdForAdding", s.GroupIdForAdding)
        v.put("GroupIdForTraining", s.GroupIdForTraining)
        v.put("GroupIdForMoving1", s.GroupIdForMoving1)
        v.put("GroupIdForMoving2", s.GroupIdForMoving2)
        v.put("GreenReceiveGroupId", s.GreenReceiveGroupId)
        v.put("CurrentLanguage", s.CurrentLanguage.value)
        v.put("TrainMode", s.TrainMode_.value)

        db.update("Settings", v, null, null)
    }

    fun getGroupsFilling(Lang: Language): Map<Int, Int> {
        val result = HashMap<Int, Int>()
        val q = "SELECT GroupId, COUNT(*) AS WordsCount FROM Cards WHERE Language = ${Lang.value} GROUP BY GroupId"

        val c = db.rawQuery(q, null)
        c.moveToFirst()
        while (!c.isAfterLast) {
            val groupId = c.getInt(c.getColumnIndex("GroupId"))
            val count = c.getInt(c.getColumnIndex("WordsCount"))

            result.put(groupId, count)
            c.moveToNext()
        }
        c.close()

        return result
    }

    companion object {
        private object Holder { var instance : DbProvider? = null }
        val Instance : DbProvider by lazy { Holder.instance ?: throw Exception("Provider wasn't initialized!") }

        fun init(ctx : Context) {
            if (Holder.instance != null)
                throw Exception("Provider was already initialized!")

            Holder.instance = DbProvider(ctx)
        }

        fun isInited () : Boolean { return Holder.instance != null }
    }
}
