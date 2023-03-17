package com.checkpoint.rfid_raw_material.utils.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.enums.TypeInventory
import com.checkpoint.rfid_raw_material.utils.interfaces.CustomDialogInventoryInterface
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CustomDialogInventory(val fragment: Fragment, private val typeDialogInventory: TypeInventory
) : Dialog(fragment.requireContext())  {
    private lateinit var customeDialogInventoryInterface: CustomDialogInventoryInterface

    init {
        setCancelable(false)
    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog_inventory)
        val tvDialogTitle =findViewById<TextView>(R.id.tvTitleDialog)
        val tvDialogQuestion =findViewById<TextView>(R.id.tvQuestionDialog)
        val buttonNext = findViewById<Button>(R.id.btnDialogNext)
        val buttonCancel = findViewById<Button>(R.id.btnDialogCancel)
        val buttonCloseDialog = findViewById<FloatingActionButton>(R.id.btnCloseDialogInvent)
        customeDialogInventoryInterface = fragment as CustomDialogInventoryInterface

        when(typeDialogInventory){
            TypeInventory.FINISH_INVENTORY ->{
                tvDialogTitle.text = context.resources.getString(R.string.finish_inventory_title)
                tvDialogQuestion.text = context.resources.getString(R.string.finish_inventory_question)
                buttonNext.setOnClickListener{
                    customeDialogInventoryInterface.finishInventory()
                }
                buttonCancel.setOnClickListener{
                    customeDialogInventoryInterface.closeDialog()
                }
            }
            TypeInventory.PAUSE_INVENTORY -> {
                tvDialogTitle.text = context.resources.getString(R.string.pause_inventory_title)
                tvDialogQuestion.text = context.resources.getString(R.string.pause_inventory_question)
                buttonNext.setOnClickListener{
                    customeDialogInventoryInterface.pauseInventory()
                }
                buttonCancel.setOnClickListener {
                    customeDialogInventoryInterface.closeDialog()
                }
            }
            TypeInventory.START_INVENTORY -> {
                tvDialogTitle.text = context.resources.getString(R.string.start_inventory_title)
                tvDialogQuestion.text = context.resources.getString(R.string.start_inventory_question)
                buttonNext.setOnClickListener{
                    customeDialogInventoryInterface.startInventory()
                }
                buttonCancel.setOnClickListener {
                    customeDialogInventoryInterface.closeDialog()
                }
            }
            else -> {}
        }

        buttonCloseDialog.setOnClickListener {
            customeDialogInventoryInterface.closeDialog()
        }

    }

}