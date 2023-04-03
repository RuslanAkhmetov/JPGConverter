package ru.geekbrain.android.jpgconverter.ui

import android.net.Uri
import android.os.Looper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import ru.geekbrain.android.jpgconverter.model.ConverterImpl
import java.util.logging.Handler


class Presenter(private val view: MainJPGConverterContact.MainView) :
    MvpPresenter<MainJPGConverterContact.MainView>(),
    MainJPGConverterContact.MainPresenter {

    private val converter = ConverterImpl()

    var disposable: Disposable? = null

    var pngFile = ""

    override fun converterObservable(jpgImageUri: Uri?) {
        try {
            view.showProgress(true)
            disposable = Single.fromCallable {
                converter.convertJPGToPNG(jpgImageUri)?.let {
                    return@let it
                }
            }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.let {
                        view.showProgress(false)
                        view.onSuccess(it)
                    }
                },
                    {
                        it.printStackTrace()
                    })
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    override fun cancelConvertation() {
        disposable?.let{
            it.dispose()
            view.showProgress(false)
        }
    }


    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()

    }

}