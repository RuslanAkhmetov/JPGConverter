package ru.geekbrain.android.jpgconverter.model

import android.net.Uri

interface Converter {
    fun convertJPGToPNG(jpgFileUri: Uri?) : String?
}