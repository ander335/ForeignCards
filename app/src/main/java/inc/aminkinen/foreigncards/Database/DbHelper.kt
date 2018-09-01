package inc.aminkinen.foreigncards.Database

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DbHelper(private val mContext: Context) : SQLiteOpenHelper(mContext, DB_NAME, null, DB_VERSION) {
    private var mDataBase: SQLiteDatabase? = null
    private var mNeedUpdate = false

    init {
        DB_PATH = "/data/data/${mContext.packageName}/databases/$DB_NAME"

        copyDataBase()

        this.getReadableDatabase();
    }

    @Throws(IOException::class)
    fun updateDataBase() {
        if (mNeedUpdate) {
            val dbFile = File(DB_PATH)
            if (dbFile.exists())
                dbFile.delete()

            copyDataBase()

            mNeedUpdate = false
        }
    }

    private fun checkDataBase(): Boolean {
        return File(DB_PATH).exists()
    }

    private fun copyDataBase() {
        if (checkDataBase())
            return;

        this.getReadableDatabase()
        this.close()
        try {
            copyDBFile()
        } catch (mIOException: IOException) {
            throw Error("ErrorCopyingDataBase")
        }
    }

    @Throws(IOException::class)
    private fun copyDBFile() {
        val input = mContext.assets.open(DB_NAME)
        val output = FileOutputStream(DB_PATH)
        val buff = ByteArray(1024)

        do {
            val len = input.read(buff);
            if (len == 0)
                break;

            output.write(buff, 0, len);
        } while (true)

        output.flush()
        output.close()
        input.close()
    }

    @Throws(SQLException::class)
    fun openDataBase(): Boolean {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.CREATE_IF_NECESSARY)
        return mDataBase != null
    }

    @Synchronized
    override fun close() {
        if (mDataBase != null)
            mDataBase!!.close()
        super.close()
    }

    override fun onCreate(db: SQLiteDatabase) {

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion)
            mNeedUpdate = true
    }

    companion object {
        private const val DB_NAME = "cards.db"
        private var DB_PATH = ""
        private const val DB_VERSION = 2
    }
}