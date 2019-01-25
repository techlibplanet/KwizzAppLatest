package kwizzapp.com.kwizzapp.helper

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by Madhu on 15-Jun-2017.
 */

inline fun <T> Observable<T>.processRequest(crossinline onNext: (result: T) -> Unit, crossinline onError: (message: String?) -> Unit): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    { result ->
                        onNext(result)
                    },
                    { err ->
                        val message = ProcessThrowable.getMessage(err)
                        Timber.e(err)
                        onError(message)
                    }
            )
}

inline fun <T> Single<T>.processRequest(crossinline onSuccess: (result: T) -> Unit, crossinline onError: (message: String?) -> Unit): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    { result ->
                        onSuccess(result)
                    },
                    { err ->
                        val message = ProcessThrowable.getMessage(err)
                        Timber.e(err)
                        onError(message)
                    }
            )
}

inline fun <T> Single<T>.processRequest(crossinline onSuccess: (result: T) -> Unit): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    { result ->
                        onSuccess(result)
                    }
            )
}

fun <T> Single<T>.processRequest(): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
}

inline fun <T> Flowable<T>.processRequest(crossinline onNext: (result: T) -> Unit, crossinline onError: (message: String?) -> Unit): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    { result ->
                        onNext(result)
                    },
                    { err ->
                        val message = ProcessThrowable.getMessage(err)
                        Timber.e(err)
                        onError(message)
                    }
            )
}

inline fun <T> Flowable<T>.processRequest(crossinline onNext: (result: T) -> Unit): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    { result ->
                        onNext(result)
                    }
            )
}

fun <T> Flowable<T>.processRequest(): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
}