package com.checkpoint.rfid_raw_material.enums

enum class LanguageOptions(lang: String) {
    English("en"),
    Spanish("es");

    private var displayLang: String? = null

    // Constructor
    open fun LanguageOptions(lang: String?) {
        displayLang = lang
    }

    open fun getDisplayLang(): String? {
        return this.displayLang
    }

}