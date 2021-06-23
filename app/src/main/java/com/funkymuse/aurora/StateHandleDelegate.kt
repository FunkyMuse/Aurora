package com.funkymuse.aurora

import com.funkymuse.aurora.paging.data.PagingDataSourceHandle
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> stateHandleDelegate(key: String) =
    object : ReadWriteProperty<PagingDataSourceHandle, T?> {
        override fun getValue(thisRef: PagingDataSourceHandle, property: KProperty<*>): T? =
            thisRef.savedStateHandle[key]

        override fun setValue(
            thisRef: PagingDataSourceHandle,
            property: KProperty<*>,
            value: T?
        ) {
            thisRef.savedStateHandle[key] = value
        }
    }


fun <T> stateHandleArgument(key: String, defaultValue: T) =
    ReadOnlyProperty<PagingDataSourceHandle, T> { thisRef, _ ->
        thisRef.savedStateHandle[key] ?: defaultValue
    }