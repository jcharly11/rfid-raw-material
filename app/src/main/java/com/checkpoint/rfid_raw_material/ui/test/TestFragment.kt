package com.checkpoint.rfid_raw_material.ui.test

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.checkpoint.rfid_raw_material.adapter.ItemsAdapter
import com.checkpoint.rfid_raw_material.databinding.FragmentTestBinding
import com.checkpoint.rfid_raw_material.source.db.tblItem
import com.checkpoint.rfid_raw_material.utils.LogCreator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class TestFragment : Fragment() {

    private lateinit var viewModel: TestViewModel
    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!
    var pathApplication = ""
    var isFileExists: Boolean = false

    companion object {
        internal const val REQUEST_CODE_PERMISSIONS = 2

        @SuppressLint("InlinedApi")
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[TestViewModel::class.java]
        _binding = FragmentTestBinding.inflate(inflater, container, false)

        //pathApplication= context?.filesDir?.absolutePath //almacenamiento app
        pathApplication = "${context?.getExternalFilesDir(null)}/logs" //almacenamiento interno
        isFileExists=false

        getItems()

        binding.btnAddItem.setOnClickListener {
            var newItem = CoroutineScope(Dispatchers.Main).launch {
                viewModel.newItem("test")
            }
            getItems()
        }

        binding.btnCsv.setOnClickListener {
            createLog()
        }

        binding.btnReadCsv.setOnClickListener {
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            val dateFormatter: String = df.format(Date())
            var fileName = "write_$dateFormatter.csv"

            val initialFile = File("$pathApplication/$fileName")
            val targetStream: InputStream = FileInputStream(initialFile)
            val list = readCsv(targetStream)

            var a: String = ""
        }

        return binding.root
    }

    private fun createLog() {
        CoroutineScope(Dispatchers.Main).launch {
            checkPermission()
            val log= LogCreator(requireContext())

            

//            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
//            val dateFormatter: String = df.format(Date())
//            var fileName = "write_$dateFormatter.csv"
//
//            val initialFile = File("$pathApplication/$fileName")
//            val targetStream: InputStream = FileInputStream(initialFile)
//            val list = readCsv(targetStream)
//
//
//            checkPermission()
//            createFile(fileName)
//
//            FileOutputStream("$pathApplication/$fileName").apply { writeCsv( list,fileName) }
            //Toast.makeText(context, "File Created", Toast.LENGTH_SHORT).show()
        }
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
            Log.e("file", "$file is created successfully.")
        } else {
            Log.e("file", "$file already exists.")
        }
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
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

    fun getItems() {
        val listView = _binding?.listItems
        listView?.layoutManager = LinearLayoutManager(context)
        CoroutineScope(Dispatchers.Main).launch {
            val list = viewModel.getItemsList()
            if (list.isNotEmpty()) {
                listView?.adapter = ItemsAdapter(list)
            }
        }
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


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }
}