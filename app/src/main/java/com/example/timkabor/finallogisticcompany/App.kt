package com.example.timkabor.finallogisticcompany

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.timkabor.finallogisticcompany.models.MainPreferences
import java.io.*


class App : Application() {
    override fun onCreate() {
        App.appContext = applicationContext
        mainPrefs = MainPreferences(applicationContext)
        super.onCreate()
    }


    companion object {
        private val AUTH_TOKEN_FILE = "token.key"
        private val MAP_TOKEN_FILE = "map.key"
        private val TAG = "MY_APP"
        private val SIGNATURES_FOLDER = "signatures/"

        var mainPrefs: MainPreferences? = null

        var appContext: Context? = null
            private set

        fun saveMapToken(token: String) {
            writeToFile(token, MAP_TOKEN_FILE)
        }

        fun saveAuthToken(token: String) {
            writeToFile(token, AUTH_TOKEN_FILE)
        }


        fun removeAuthToken() {
            val dir = appContext!!.filesDir
            val file = File(dir, AUTH_TOKEN_FILE)
            if (file.delete())
                Log.d(TAG, "Token deleted!")
            else
                Log.d(TAG, "Token not deleted!")
        }

        fun removeSignature(fileName: String) {
            val file = File(appContext!!.filesDir.absolutePath + "/" + SIGNATURES_FOLDER, fileName)
            Log.d(TAG, file.absolutePath)
            if (file.delete())
                Log.d(TAG, "Signature (%s) deleted!".format(fileName))
            else
                Log.d(TAG, "Signature (%S) not deleted!".format(fileName))
        }

        fun getMapToken() = readFromFile(MAP_TOKEN_FILE)

        fun getAuthToken() = readFromFile(AUTH_TOKEN_FILE)


        fun writeToFile(bitmap: Bitmap, fileName: String) {
            val folder = File(appContext!!.filesDir, SIGNATURES_FOLDER)
            folder.mkdirs()
            val file = File(folder, fileName) // the File to save , append increasing numeric counter to prevent files from getting overwritten.
            val fOut = FileOutputStream(file)

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut) // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush() // Not really required
            fOut.close() // do not forget to close the stream
        }

        fun readAllSignatures(): List<Pair<Bitmap, String>>? {
            val folder = File(appContext!!.filesDir, SIGNATURES_FOLDER)
            val signatures = folder.listFiles()
            val a = arrayListOf<Pair<Bitmap, String>>()
            if (signatures != null) {
                a.addAll(signatures.map { file ->
                    Pair(BitmapFactory.decodeByteArray(file.readBytes(), 0, file.readBytes().size),
                            file.name)
                })
                return a
            }
            return null
        }

        private fun writeToFile(mytext: String, fileName: String): Boolean {
            Log.d(TAG, "SAVE")
            try {
                val fos = appContext!!.openFileOutput(fileName, Context.MODE_PRIVATE)
                val out = OutputStreamWriter(fos)
                out.write(mytext)
                out.close()
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, e.message)
                return false
            }
        }

        private fun readFromFile(fileName: String): String? {
            Log.d(TAG, "FILE")
            try {
                val fis = appContext!!.openFileInput(fileName)
                val r = BufferedReader(InputStreamReader(fis))
                val line = r.readLine()
                r.close()
                return line
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, e.message)
                return null
            }
        }

    }
}