package ru.geekbrain.android.jpgconverter.ui

import android.net.Uri
import io.reactivex.rxjava3.core.Single
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

class MainJPGConverterContact {

    @StateStrategyType (AddToEndSingleStrategy::class)
    interface MainView : MvpView{
        fun onSuccess(convertedImageString: String?)
        fun showProgress(show:Boolean)
    }

    interface MainPresenter{
        fun  converterObservable(jpgImageUri: Uri?)  //: Single<String?>
        fun cancelConvertation()
    }
}