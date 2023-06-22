package com.checkpoint.rfid_raw_material.ui.write

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.utils.dialogs.*
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.DialogWriteTagSuccessInterface
import com.google.android.material.textfield.TextInputEditText


class ConfirmWriteActivity : AppCompatActivity(), DialogWriteTagSuccessInterface {
    private var activityMain: MainActivity? = null
    private var epc: String? = null
    private var version: String? = null
    private var subversion: String? = null
    private var type: String? = null
    private var identifier: String? = null
    private var provider: Int? = null
    private var readNumber: Int? = 0
    private var deviceName: String? = null
    private var tid: String? = null
    private var startDevice: Boolean = false

    private var dialogErrorMultipleTags: DialogErrorMultipleTags? = null
    private var dialogWriteTag: CustomDialogWriteTag? = null
    private var dialogLoadingWrite: DialogPrepareTrigger? = null
    private var dialogERRORWriting: DialogErrorWritingTag? = null
    private var dialogWriteTagSuccess: DialogWriteTagSuccess? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_write)
        var edtNewEPC: TextInputEditText= findViewById(R.id.edtNewTagEPC)

        val arguments = intent.extras
        if (arguments != null) {
            epc = arguments?.getString("epc")
            version = arguments?.getString("version")
            subversion = arguments?.getString("subversion")
            type = arguments?.getString("type")
            identifier = arguments?.getString("identifier")
            provider = arguments?.getInt("provider")
            readNumber = arguments?.getInt("readNumber")
            deviceName = arguments?.getString("deviceName")
        }

        activityMain = MainActivity()
        dialogErrorMultipleTags= DialogErrorMultipleTags(this)
        dialogWriteTag = CustomDialogWriteTag(this)
        dialogLoadingWrite = DialogPrepareTrigger(this)
        dialogERRORWriting = DialogErrorWritingTag(this)

        //activityMain!!.lyCreateLog!!.visibility = View.GONE
        activityMain!!.version= version!!
        activityMain!!.subVersion= subversion!!
        activityMain!!.type= type!!
        activityMain!!.identifier= identifier!!
        activityMain!!.provider= provider!!

        edtNewEPC.setText(epc)
        edtNewEPC.isEnabled = false

        var btnCancel:Button= findViewById(R.id.btnCancelWrite)
        btnCancel.setOnClickListener {
            tid=""
            edtNewEPC.setText("")
            startDevice= false
            val bundle = bundleOf(
                "readNumber" to readNumber
            )

            val intent = Intent(this, WriteTagFragment::class.java)
            startActivity(intent,bundle)
        }


        /*activityMain!!.showDialogWritingTag.observe(){
            if(it){
                dialogLoadingWrite!!.show()
            }else{
                dialogLoadingWrite!!.dismiss()
            }
        }

        activityMain!!.showErrorNumberTagsDetected.observe(viewLifecycleOwner){
            if(it){
                if(!dialogErrorMultipleTags!!.isShowing)
                    dialogErrorMultipleTags!!.show()
            }
        }

        activityMain!!.showDialogWritingError.observe(viewLifecycleOwner){
            if (it){
                dialogERRORWriting!!.show()
            }

        }
        activityMain!!.showDialogWritingSuccess.observe(viewLifecycleOwner){
            if(it){
                dialogWriteTagSuccess = DialogWriteTagSuccess(this@ConfirmWriteTagFragment,epc)
                if(dialogERRORWriting!!.isShowing){
                    dialogERRORWriting!!.dismiss()
                }
                dialogWriteTagSuccess!!.show()

            }
        }*/

    }

    override fun successRecording() {
        dialogWriteTagSuccess!!.dismiss()
        activityMain!!.restartWritingFlags()
        val bundle = bundleOf(
            "readNumber" to readNumber
        )

        val intent = Intent(this, WriteTagFragment::class.java)
        startActivity(intent,bundle)
    }

    override fun onStart() {
        super.onStart()
        activityMain!!.stopReadedBarCode()
        activityMain!!.startRFIDReadInstance(true,this.epc!!)
    }
}