package ru.geekbrain.android.jpgconverter.ui

import android.net.Uri
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

class MainJPGConverterContact {

    @StateStrategyType (AddToEndSingleStrategy::class)
    interface MainView : MvpView{
        fun onSuccess(convertedImageString: String?)
        fun showProgress(show:Boolean)
        fun showMessage(s: String)
    }

    interface MainPresenter{
        fun  converterObservable(jpgImageUri: Uri?)
        fun cancelConvertation()
    }
}