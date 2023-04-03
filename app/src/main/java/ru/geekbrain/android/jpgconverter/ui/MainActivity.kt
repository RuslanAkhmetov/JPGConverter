package ru.geekbrain.android.jpgconverter.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.geekbrain.android.jpgconverter.databinding.ActivityMainBinding

const val GALLERY_REQUEST = 1
const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045

class MainActivity : AppCompatActivity(), MainJPGConverterContact.MainView {

    lateinit var binding: ActivityMainBinding
    val TAG = "MainActivity"
    var presenter = Presenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (!haveWritePermission()) {
            requestPermission()
        }

        binding.convertImageButton.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*.jpg"
            startActivityForResult(photoPickerIntent, GALLERY_REQUEST)
        }
    }


    private fun requestPermission() {
        val permission = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(this, permission, READ_EXTERNAL_STORAGE_REQUEST)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult: ${haveWritePermission()}")
                    binding.convertImageButton.isEnabled = true
                } else {
                    val showRationale =
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )

                    if (showRationale) {
                        binding.convertImageButton.isEnabled = false
                    }

                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GALLERY_REQUEST ->
                if (resultCode == RESULT_OK) {

                    val selectedImage = data?.data
                    Log.i("TAG", "onActivityResult:$selectedImage \n ----- ${data?.data?.path}")
                    try {
                        presenter.converterObservable(selectedImage)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ imagePath ->
                                onSuccess(imagePath)
                            }, {
                                Toast.makeText(
                                    this,
                                    "Error : ${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                it.printStackTrace()
                            })
                        //presenter.onConvert(selectedImage)

                    } catch (t: Throwable) {
                        Log.i(TAG, "onActivityResult: Error")
                        t.printStackTrace()
                    }
                }

        }

    }


    fun haveWritePermission() =
        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PERMISSION_GRANTED

    override fun onSuccess(convertedImagePath: String?) {
        Log.i(TAG, "onSuccess: ${convertedImagePath}")
        Log.i(TAG, "onSuccess: ${Uri.parse(convertedImagePath).path}")
        Uri.parse(convertedImagePath)?.let {
            binding.convertedImageView.setImageURI(null)
            binding.convertedImageView.setImageURI(it)
        }
    }


}