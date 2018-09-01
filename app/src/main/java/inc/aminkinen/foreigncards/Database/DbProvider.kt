package inc.aminkinen.foreigncards.Database

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import inc.aminkinen.foreigncards.Entities.Card
import inc.aminkinen.foreigncards.Entities.CardData
import inc.aminkinen.foreigncards.Entities.Settings
import java.io.IOException

import java.util.ArrayList

class DbProvider(ctx : Context) {
    val Db: SQLiteDatabase = init(ctx)

    private fun init(ctx : Context) : SQLiteDatabase {
        val mDBHelper = DbHelper(ctx)

        try {
            mDBHelper.updateDataBase()
        } catch (mIOException: IOException) {
            throw Error("UnableToUpdateDatabase")
        }

        try {
            return mDBHelper.getWritableDatabase()
        } catch (mSQLException: SQLException) {
            throw mSQLException
        }
    }

    fun addCard(c: CardData) {
        val v = ContentValues()
        v.put("Word", c.Word)
        v.put("Transc", c.Transc)
        v.put("Transl", c.Transl)
        if (c.GroupId >= 0)
            v.put("GroupId", c.GroupId)

        Db.insert("Cards", null, v)
    }

    fun removeCard(id: Int) {
        Db.execSQL("DELETE FROM Cards WHERE Id = ?", arrayOf<Any>(id))
    }

    fun UpdateCard(c: Card) {
        val v = ContentValues()
        v.put("Word", c.Word)
        v.put("Transc", c.Transc)
        v.put("Transl", c.Transl)
        if (c.GroupId >= 0)
            v.put("GroupId", c.GroupId)

        Db.update("Cards", v, "Id = ?", arrayOf(Integer.toString(c.Id)))
    }

    fun cardsCount(): Int {
        return DatabaseUtils.queryNumEntries(Db, "Cards").toInt()
    }

    fun getCards(groupId: Int?): ArrayList<Card> {
        val result = ArrayList<Card>()

        var q = "SELECT * FROM Cards"
        if (groupId != null)
            q = String.format("SELECT * FROM Cards WHERE GroupId = %d", groupId.toInt())

        val c = Db.rawQuery(q, null)
        c.moveToFirst()
        while (!c.isAfterLast) {
            val groupIdx = c.getColumnIndex("GroupId")
            val groupId = if (c.isNull(groupIdx)) -1 else c.getInt(groupIdx)

            val a = Card(
                    c.getInt(c.getColumnIndex("Id")),
                    c.getString(c.getColumnIndex("Word")),
                    c.getString(c.getColumnIndex("Transl")),
                    c.getString(c.getColumnIndex("Transc")),
                    "", // TODO: do example
                    groupId)

            result.add(a)
            c.moveToNext()
        }
        c.close()

        return result
    }

    fun getSettings(): Settings {
        val c = Db.rawQuery("SELECT * FROM Settings", null)
        c.moveToFirst()

        val result = Settings(c.getInt(c.getColumnIndex("GroupIdForAdding")))
        c.close()

        return result
    }

    fun updateSettings(s: Settings) {
        val v = ContentValues()
        v.put("GroupIdForAdding", s.GroupIdForAdding)

        Db.update("Settings", v, null, null)
    }

    companion object {
        private object Holder { var instance : DbProvider? = null }
        val Instance : DbProvider by lazy { Holder.instance ?: throw Exception("Processor wasn't initialized!") }

        fun init(ctx : Context) {
            if (Holder.instance != null)
                throw Exception("Provider was already initialized!")

            Holder.instance = DbProvider(ctx)
        }

        fun isInited () : Boolean { return Holder.instance != null }
    }
}
