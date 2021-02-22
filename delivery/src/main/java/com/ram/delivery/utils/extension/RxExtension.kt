package com.ram.delivery.utils.extension

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.with() = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.with(): Flowable<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.with(): Observable<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
