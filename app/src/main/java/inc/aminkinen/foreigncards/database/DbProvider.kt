package inc.aminkinen.foreigncards.database

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import inc.aminkinen.foreigncards.entities.Card
import inc.aminkinen.foreigncards.entities.CardData
import inc.aminkinen.foreigncards.entities.Language
import inc.aminkinen.foreigncards.entities.Settings
import java.io.IOException

import java.util.ArrayList

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

    fun removeCard(id: Int) {
        db.execSQL("DELETE FROM Cards WHERE Id = ?", arrayOf<Any>(id))
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
                Language.fromInt(c.getInt(c.getColumnIndex("CurrentLanguage"))))
        c.close()

        return result
    }

    fun updateSettings(s: Settings) {
        val v = ContentValues()
        v.put("GroupIdForAdding", s.GroupIdForAdding)
        v.put("GroupIdForTraining", s.GroupIdForTraining)
        v.put("GroupIdForMoving1", s.GroupIdForMoving1)
        v.put("GroupIdForMoving2", s.GroupIdForMoving2)
        v.put("CurrentLanguage", s.CurrentLanguage.value)

        db.update("Settings", v, null, null)
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
