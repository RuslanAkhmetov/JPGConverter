package ru.geekbrain.android.jpgconverter.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import moxy.MvpView
import ru.geekbrain.android.jpgconverter.App
import java.io.File
import java.io.FileOutputStream
import java.util.logging.Handler


class Presenter(private val view: MvpView) :
    MvpPresenter<MainJPGConverterContact.MainView>(),
    MainJPGConverterContact.MainPresenter {

    private val TAG = "Presenter"
    private val CHILD_DIR = "images"
    private val CONVERTED_FILE_NAME = "converted.png"

    lateinit var disposable: Disposable

    fun converterObservable(jpgImageUri: Uri?) : Single<Uri> = Single.fromCallable {
        convertImageFileToPNG(jpgImageUri)?.let {
            return@let it
        }
    }

    fun onConvert(jpgImageUri: Uri?, callback: (Uri)->Unit){

        convertImageFileToPNG(jpgImageUri)?.let { callback(it) }
    }

    override fun onConvert(jpgImageUri: Uri?){

        viewState.onSuccess(convertImageFileToPNG(jpgImageUri))
    }

   /* override fun onConvert(jpgImageUri: Uri?, callback: (Uri)->Unit) {
        disposable =  Single.fromCallable {
            convertImageFileToPNG(jpgImageUri)?.let {
                return@fromCallable it
            }
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ uri ->
                uri?.let {
                    Log.i(TAG, "onConvert: onNext : ${uri.path}")
                        callback
                }
            }, {
                Toast.makeText(
                    App.instance.getAppContext(),
                    "Error : ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            })

    }*/

    private fun convertImageFileToPNG(imageUri: Uri?): Uri? {

        var bitmap: Bitmap?
        var fos: FileOutputStream?

        val cacheHolder = File(App.instance.applicationContext.cacheDir, CHILD_DIR)
        if (!cacheHolder.exists()) {
            cacheHolder.mkdir()
        }

        imageUri?.let {

            bitmap = MediaStore.Images.Media.getBitmap(
                App.instance.getAppContext().contentResolver,
                imageUri
            )

            val file = File(cacheHolder.path + "/" + CONVERTED_FILE_NAME)
            if (!file.exists()) {
                file.createNewFile()
            }
            fos = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos?.flush()
            fos?.close()
            return Uri.parse(file.path)
        }
        return null

    }

    private fun setImage(uri: Uri){
        Log.i(TAG, "setImage: ")

        viewState.onSuccess(uri)
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()

    }


}