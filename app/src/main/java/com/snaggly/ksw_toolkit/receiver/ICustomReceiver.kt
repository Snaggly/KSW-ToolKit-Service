package com.snaggly.ksw_toolkit.receiver

interface ICustomReceiver<T> {
    fun setReceiverHandler(handler : (T) -> Unit)
}