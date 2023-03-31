package ru.geekbrain.android.jpgconverter.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GALLERY_REQUEST ->
                if (resultCode == RESULT_OK) {
                    val selectedImage = data?.data
                    try {
                        presenter.converterObservable(selectedImage)
                            .subscribe({ uri ->
                                onSuccess(uri)
                            }, {
                                Toast.makeText(
                                    this,
                                    "Error : ${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                        //presenter.onConvert(selectedImage, ::onSuccess)

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

    override fun onSuccess(convertedImageUri: Uri?) {
        Log.i(TAG, "onSuccess: ${convertedImageUri?.path}")
        convertedImageUri?.let {
            binding.convertedImageView.setImageURI(null)
            binding.convertedImageView.setImageURI(it)
        }
    }


}