package com.funkymuse.aurora

import com.funkymuse.aurora.abstracts.AbstractPagingSourceViewModel
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> stateHandleDelegate(key: String) =
    object : ReadWriteProperty<AbstractPagingSourceViewModel, T?> {
        override fun getValue(thisRef: AbstractPagingSourceViewModel, property: KProperty<*>): T? =
            thisRef.savedStateHandle[key]

        override fun setValue(
            thisRef: AbstractPagingSourceViewModel,
            property: KProperty<*>,
            value: T?
        ) {
            thisRef.savedStateHandle[key] = value
        }
    }