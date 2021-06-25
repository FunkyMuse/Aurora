package com.funkymuse.aurora.extensions

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingSource
import androidx.savedstate.SavedStateRegistryOwner
import com.funkymuse.aurora.dto.Book
import com.funkymuse.composed.core.savedStateRegistryOwner


@PublishedApi
internal inline fun <reified T : ViewModel> createAssistedViewModel(
    arguments: Bundle? = null,
    owner: SavedStateRegistryOwner,
    crossinline viewModelProducer: (SavedStateHandle) -> T,
) =
    object : AbstractSavedStateViewModelFactory(owner, arguments) {
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ) =
            viewModelProducer(handle) as T
    }


@Composable
inline fun <reified T : ViewModel> assistedViewModel(
    arguments: Bundle? = null,
    crossinline viewModelProducer: (SavedStateHandle) -> T,
): T =
    viewModel(factory = createAssistedViewModel(
        arguments = arguments,
        owner = savedStateRegistryOwner
    ) {
        viewModelProducer(it)
    })



fun canNotLoadMoreBooks(): PagingSource.LoadResult.Page<Int, Book> =
    PagingSource.LoadResult.Page(emptyList(), null, null)