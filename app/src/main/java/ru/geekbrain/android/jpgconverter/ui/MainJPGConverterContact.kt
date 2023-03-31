package ru.geekbrain.android.jpgconverter.ui

import android.net.Uri
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

class MainJPGConverterContact {

    @StateStrategyType(AddToEndSingleStrategy::class)
    interface MainView : MvpView{
        fun onSuccess(convertedImageUri: Uri?)
    }

    interface MainPresenter{
        fun onConvert(jpgImageUri: Uri?)
    }
}