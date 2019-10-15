package com.github.adyenexample.ext

import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions


val ERROR_FUNCTION: Consumer<Throwable> = Consumer { it.printStackTrace() }

fun <T> Maybe<T>.subscribeAndAdd(disposables: CompositeDisposable) {
    this.subscribe(Functions.emptyConsumer(), ERROR_FUNCTION, Functions.EMPTY_ACTION)
        .addToDisposables(disposables)
}

interface SchedulersProvider {
    fun io(): Scheduler
    fun computation(): Scheduler
    fun ui(): Scheduler
}


fun <T> Maybe<T>.async(schedulersProvider: SchedulersProvider): Maybe<T> {
    return this.subscribeOn(schedulersProvider.io())
        .observeOn(schedulersProvider.ui())
}

fun Disposable.addToDisposables(disposables: CompositeDisposable) {
    disposables.add(this)
}


