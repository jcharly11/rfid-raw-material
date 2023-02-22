package com.checkpoint.rfid_raw_material.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContentProviderCompat.requireContext
import com.checkpoint.rfid_raw_material.db.tblItem
import com.checkpoint.rfid_raw_material.ui.test.TestFragment
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class LogCreator constructor(context: Context): View(context) {
    var pathApplication = ""
    var isFileExists:Boolean = false

    companion object {
        internal const val REQUEST_CODE_PERMISSIONS = 2
        @SuppressLint("InlinedApi")
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    init {
        //pathApplication= context?.filesDir?.absolutePath //app storage
        pathApplication = "${context?.getExternalFilesDir(null)}/logs" //internal storage}
        isFileExists=false
    }

    suspend fun createLog(type:String){
        //val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateFormatter: String = df.format(Date())
        var fileName = "write_$dateFormatter.csv"
        var fullPath= "$pathApplication/$fileName"

        var initialFile: File
        var targetStream: InputStream
        var list= listOf<tblItem>()

        createFile(fileName)

        if(isFileExists) {
            initialFile = File("$pathApplication/$fileName")
            targetStream = FileInputStream(initialFile)
            list = readCsv(targetStream)
        }

        FileOutputStream(fullPath).apply { writeCsv( list,fileName) }
        Toast.makeText(context, "Log created $fullPath", Toast.LENGTH_SHORT).show()
    }

    private fun createFile(fileName: String) {
        val path = pathApplication
        val directory = File(path)

        if (!directory.exists()) {
            if (directory.mkdir()) {
                Log.e("directory", "Directory created successfully")
            }
        }
        val file = File("$directory/$fileName")
        val isNewFileCreated: Boolean = file.createNewFile()
        if (isNewFileCreated) {
            isFileExists=false
            Log.e("file", "$file is created successfully.")
        } else {
            isFileExists=true
            Log.e("file", "$file already exists.")
        }
    }

    private fun OutputStream.writeCsv(listCSV: List<tblItem>, fileName:String) {
        val writer= bufferedWriter()
        writer.write("""ID,Name""")
        writer.newLine()

        listCSV.forEach {
            writer.write("${it.id},${it.nameItem}")
            writer.newLine()
        }

        /*items.forEach {
            //writer.write("${date},${epc},${it.id},${type},${subversion},${identifier},${supplier}")
            writer.write("${it.id},${epc}")
            writer.newLine()
        }*/

        var epc="epc"
        writer.write("1,$epc")
        writer.flush()
        writer.close()
    }



    fun readCsv(inputStream: InputStream): List<tblItem> {
        val reader = inputStream.bufferedReader()
        val header = reader.readLine()
        return reader.lineSequence()
            .filter { it.isNotBlank() }
            .map {
                val (id, name) = it.split(',', ignoreCase = false, limit = 3)
                tblItem(id.toInt(), name.trim().removeSurrounding("\""))
            }.toList()
    }


}