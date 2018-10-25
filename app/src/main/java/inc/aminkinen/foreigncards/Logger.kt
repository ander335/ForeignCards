package inc.aminkinen.foreigncards

import android.util.Log

class Logger(private val _module: String) {
    fun info(msg: String) {
        Log.i(String.format(_fmtKey, _module), msg)
    }
    fun warn(msg: String) {
        Log.w(String.format(_fmtKey, _module), msg)
    }
    fun error(msg: String) {
        Log.e(String.format(_fmtKey, _module), msg)
    }

    companion object {
        private val _fmtKey : String = "[ForeignCards] %s"
    }
}