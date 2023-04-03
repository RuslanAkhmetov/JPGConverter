package ru.geekbrain.android.jpgconverter.ui

import android.net.Uri

class MainJPGConverterContact {

    interface MainView{
        fun onSuccess(convertedImageString: String?)
        fun showProgress(show:Boolean)
        fun showMessage(s: String)
    }

    interface MainPresenter{
        fun  converterObservable(jpgImageUri: Uri?)
        fun cancelConvertation()
    }
}