package com.checkpoint.rfid_raw_material.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.checkpoint.rfid_raw_material.source.db.Inventory
import com.checkpoint.rfid_raw_material.source.db.Tags
import com.checkpoint.rfid_raw_material.source.model.Logs
import com.checkpoint.rfid_raw_material.source.model.TagsLogs
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

    fun createLog(typeCSV:String, date: String, epc:String, version:String, type:String,
                          subversion:String, identifier:String,supplier:String){
        //val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateFormatter: String = df.format(Date())
        var fileName = "${typeCSV}_$dateFormatter.csv"
        var fullPath= "$pathApplication/$fileName"

        var initialFile: File
        var targetStream: InputStream
        var list= listOf<Logs>()

        createFile(fileName)

        if(isFileExists) {
            initialFile = File("$pathApplication/$fileName")
            targetStream = FileInputStream(initialFile)
            list = readCsv(targetStream)
        }

        FileOutputStream(fullPath).apply {
            writeCsv(list,date,epc,version,type,subversion,identifier,supplier) }
    }

    fun  <T: Any> createLog(typeCSV:String, list: List<T>){
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateFormatter: String = df.format(Date())
        var fileName = "${typeCSV}_$dateFormatter.csv"
        var fullPath= "$pathApplication/$fileName"

        createFile(fileName)

        if(typeCSV=="read")
            FileOutputStream(fullPath).apply { writeCsvInventory(list as List<Inventory>) }
        else
            FileOutputStream(fullPath).apply { writeCsvTags(list as List<Tags>) }

        Toast.makeText(context, "Log file create in $fullPath", Toast.LENGTH_LONG).show()
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

    private fun OutputStream.writeCsv(listCSV: List<Logs>,date: String, epc:String, version:String,
                                      type:String,subversion:String, identifier:String,supplier:String ) {
        val writer= bufferedWriter()
        writer.write("""Date,EPC,Version,Type,Subversion,Identifier,Supplier""")
        writer.newLine()

        listCSV.forEach {
            writer.write("${it.date},${it.epc},${it.version},${it.type}" +
                    ",${it.subversion},${it.identifier},${it.suplier}")
            writer.newLine()
        }

        writer.flush()
        writer.write("${date},${epc},${version},${type},${subversion},${identifier},${supplier}")
        writer.close()
    }

    private fun OutputStream.writeCsvInventory(list: List<Inventory>) {
        val writer= bufferedWriter()
        writer.write("""Date,EPC,Version,Type,Subversion,Identifier,Supplier""")
        writer.newLine()

        list.forEach {
            writer.write("${it.timeStamp},${it.epc}")
            writer.newLine()
        }
        writer.flush()
        writer.close()
    }

    private fun OutputStream.writeCsvTags(list: List<Tags>) {
        val writer= bufferedWriter()
        writer.write("""Date,EPC,Version,Type,Subversion,Identifier,Supplier""")
        writer.newLine()

        list.forEach {
            writer.write("${it.timeStamp},${it.epc},${it.version},${it.type},${it.subversion},${it.piece},${it.idProvider}")
            writer.newLine()
        }
        writer.flush()
        writer.close()
    }


    fun readCsv(inputStream: InputStream): List<Logs> {
        val reader = inputStream.bufferedReader()
        val header = reader.readLine()
        return reader.lineSequence()
            .filter { it.isNotBlank() }
            .map {
                val (date, epc,version,type,subversion,identifier,supplier) = it.split(",", ignoreCase = false, limit = 7)
                Logs(date,epc,version,type,subversion,identifier,supplier.trim().removeSurrounding("\""))
            }.toList()
    }
}

operator fun <T> List<T>.component6() = this[5]
operator fun <T> List<T>.component7() = this[6]
