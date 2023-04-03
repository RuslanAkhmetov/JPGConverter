package ru.geekbrain.android.jpgconverter.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Looper
import android.provider.MediaStore
import ru.geekbrain.android.jpgconverter.App
import java.io.File
import java.io.FileOutputStream

class ConverterImpl : Converter {

    private val CHILD_DIR = "images"
    private val CONVERTED_FILE_NAME = "converted.png"

    override fun convertJPGToPNG(jpgFileUri: Uri?): String? {
        var bitmap: Bitmap?
        var fos: FileOutputStream?

        val cacheHolder = File(App.instance.applicationContext.cacheDir, CHILD_DIR)

        android.os.Handler(Looper.getMainLooper()).postDelayed({
            if (!cacheHolder.exists()) {
                cacheHolder.mkdir()
            }
        }, 2_000)


        jpgFileUri?.let {

            bitmap = MediaStore.Images.Media.getBitmap(
                App.instance.contentResolver,
                jpgFileUri
            )

            /*           bitmap = MediaStore.Images.Media.getBitmap(         // дает ошибку
                           App.instance.contentResolver,                    //2023-04-03 15:04:25.472 8646-8646/ru.geekbrain.android.jpgconverter W/System.err: java.io.FileNotFoundException: No content provider: /external_primary/images/media/1000003235
                           Uri.parse(jpgFileUri.path)
                       )*/

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