package ru.geekbrain.android.jpgconverter.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import ru.geekbrain.android.jpgconverter.App
import java.io.File
import java.io.FileOutputStream

class ConverterImpl: Converter {

    private val TAG = "ConverterImpl"
    private val CHILD_DIR = "images"
    private val CONVERTED_FILE_NAME = "converted.png"

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun convertJPGToPNG(jpgFileUri: Uri?): String? {
        var bitmap: Bitmap?
        var fos: FileOutputStream?

        val cacheHolder = File(App.instance.applicationContext.cacheDir, CHILD_DIR)
        if (!cacheHolder.exists()) {
            cacheHolder.mkdir()
        }

        jpgFileUri?.let {

           // bitmap = BitmapFactory.decodeFile(jpgFilePath)

            Log.i(TAG, "convertJPGToPNG: ${jpgFileUri}")
            bitmap = MediaStore.Images.Media.getBitmap(App.instance.contentResolver,
                    jpgFileUri)

            val file = File(cacheHolder.path + "/" + CONVERTED_FILE_NAME)
            if (!file.exists()) {
                file.createNewFile()
            }
            fos = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)

            fos?.flush()
            fos?.close()

            return file.path
        }
        return null

    }

}