package com.checkpoint.rfid_raw_material.utils.dialogs.interfaces

interface CustomDialogProviderInterface {
    fun saveProvider(id: String,idAs: String,name: String)
    fun closeDialog()
}