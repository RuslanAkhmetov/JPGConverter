package ru.geekbrain.android.jpgconverter.ui

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import moxy.MvpPresenter
import moxy.MvpView
import ru.geekbrain.android.jpgconverter.model.ConverterImpl


class Presenter(private val view: MvpView) :
    MvpPresenter<MainJPGConverterContact.MainView>(),
    MainJPGConverterContact.MainPresenter {

    private val converter = ConverterImpl()

    lateinit var disposable: Disposable

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun converterObservable(jpgImageUri: Uri?) : Single<String?> = Single.fromCallable {
        converter.convertJPGToPNG(jpgImageUri)?.let {
            return@let it
        }
    } as Single<String?>


    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()

    }


}