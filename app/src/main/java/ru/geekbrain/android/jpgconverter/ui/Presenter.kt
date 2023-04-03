package ru.geekbrain.android.jpgconverter.ui

import android.net.Uri
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import ru.geekbrain.android.jpgconverter.model.ConverterImpl


class Presenter(private val view: MainJPGConverterContact.MainView,
                private val observerScheduler: Scheduler) :
    MvpPresenter<MainJPGConverterContact.MainView>(),
    MainJPGConverterContact.MainPresenter {

    private val converter = ConverterImpl()

    var disposable: Disposable? = null

    override fun converterObservable(jpgImageUri: Uri?) {
        try {
            view.showProgress(true)
            disposable = Single.fromCallable {
                converter.convertJPGToPNG(jpgImageUri)?.let {
                    return@let it
                }
            }
                .subscribeOn(Schedulers.computation())
                .observeOn(observerScheduler)
                .subscribe({
                    view.showProgress(false)
                    if(it.isNotEmpty()){
                        view.onSuccess(it)
                    } else {
                        view.showMessage("Can't convert ${jpgImageUri?.path}")
                    }
                },
                    {
                        view.showProgress(false)
                        view.showMessage("Error: ${it.message}")
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