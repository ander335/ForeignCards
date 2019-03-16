package inc.aminkinen.foreigncards.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DbHelper(ctx: Context) : SQLiteOpenHelper(ctx, DB_NAME, null, DB_VERSION) {
    var needUpdate = false

    init {
        val dbDir = "/data/data/${ctx.packageName}/databases"
        val dir = File(dbDir)
        if (!dir.exists())
            dir.mkdir()

        DB_PATH = "$dbDir/$DB_NAME"

        copyDataBase(ctx)
    }

    @Throws(IOException::class)
    fun updateDataBase(ctx: Context) {
        val dbFile = File(DB_PATH)
        if (dbFile.exists())
            dbFile.delete()

        copyDataBase(ctx)
    }

    private fun copyDataBase(ctx: Context) {
        if (File(DB_PATH).exists())
            return

        try {
            copyDBFile(ctx)
        } catch (mIOException: IOException) {
            throw Error("ErrorCopyingDataBase")
        }
    }

    @Throws(IOException::class)
    private fun copyDBFile(ctx : Context) {
        val input = ctx.assets.open(DB_NAME)
        val output = FileOutputStream(DB_PATH)
        val buff = ByteArray(1024)

        do {
            val len = input.read(buff)
            if (len <= 0)
                break

            output.write(buff, 0, len)
        } while (true)

        output.flush()
        output.close()
        input.close()
    }

    override fun onCreate(d: SQLiteDatabase?) {
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion) {
            needUpdate = true
        }
    }

    companion object {
        private const val DB_NAME = "cards.db"
        private var DB_PATH = ""
        private const val DB_VERSION = 10
    }
}