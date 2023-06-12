package com.checkpoint.rfid_raw_material.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.checkpoint.rfid_raw_material.source.model.Logs
import com.checkpoint.rfid_raw_material.source.model.TagsLogs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
         pathApplication = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/raw-materials/logs/"
        isFileExists=false
    }

    fun createLog(typeCSV:String, date: String, epc:String, version:String, type:String,
                  subversion:String, identifier:String,supplier:String){
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateFormatter: String = df.format(Date())
        var fileName = "${typeCSV}_$dateFormatter.csv"
        var fullPath= "$pathApplication/$fileName"

        var initialFile: File
        var targetStream: InputStream
        var list= listOf<Logs>()
        CoroutineScope(Dispatchers.IO).launch {
            createFile(fileName)
        }


        if(isFileExists) {
            initialFile = File("$pathApplication/$fileName")
            targetStream = FileInputStream(initialFile)
            list = readCsv(targetStream)
        }

        FileOutputStream(fullPath).apply {
            writeCsv(list,date,epc,version,type,subversion,identifier,supplier) }
    }

    fun <T: Any> createLog(typeCSV:String, list: List<T>){
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH_mm_ss")
        val dateFormatter: String = df.format(Date())
        var fileName = "${typeCSV}_$dateFormatter.csv"
        var fullPath= "$pathApplication/$fileName"


        createFile(fileName)

        FileOutputStream(fullPath).apply { writeCsvTags(list as List<TagsLogs>) }

        Toast.makeText(context, "Log file create in $fullPath", Toast.LENGTH_LONG).show()
    }

    fun createFile(fileName:String){
        val directory= File(pathApplication)
        if(!directory.exists()){
            val dir1 = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/raw-materials/"
            val dir2 = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/raw-materials/logs/"
            val directory1= File(dir1)
            val directory2= File(dir2)
            directory1.mkdirs()
            directory2.mkdirs()
        }
        val file = File(pathApplication, fileName)
         val file2 = File(
            Environment.getExternalStorageDirectory()
                .toString() + "/" + Environment.DIRECTORY_DOCUMENTS + "/raw-materials/logs/$fileName"
        )
        if (!file2.exists()) {
            file2.createNewFile()
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

    private fun OutputStream.writeCsvTags(list: List<TagsLogs>) {
        val writer= bufferedWriter()
        writer.write("""Date,EPC,Version,Type,Subversion,Identifier,Supplier""")
        writer.newLine()

        list.forEach {
            writer.write("${it.timestamp},${it.epc},${it.version},${it.type},${it.subversion},${it.piece},${it.provider}")
            writer.newLine()
        }
        writer.flush()
        writer.close()
    }


    fun readCsv(inputStream: InputStream): List<Logs> {
        val reader = inputStream.bufferedReader()
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