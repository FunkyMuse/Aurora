package com.funkymuse.aurora.extensions

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingSource
import androidx.paging.compose.LazyPagingItems
import androidx.savedstate.SavedStateRegistryOwner
import com.bumptech.glide.request.RequestOptions
import com.funkymuse.aurora.dto.Book
import com.funkymuse.composed.core.context
import com.funkymuse.composed.core.savedStateRegistryOwner
import com.funkymuse.composed.core.stateWhenStarted


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


@Composable
inline fun loadPicture(
    url: String,
    requestOptions: RequestOptions.() -> Unit = {}
): GlideImageState {
    val target = remember { GlideFlowTarget() }
    GlideApp.with(context)
        .applyDefaultRequestOptions(RequestOptions().also { it.requestOptions() })
        .asBitmap()
        .load(url)
        .into(target)

    val state by stateWhenStarted(flow = target.imageState, initial = GlideImageState.Loading)
    return state
}


fun canNotLoadMoreBooks(): PagingSource.LoadResult.Page<Int, Book> =
    PagingSource.LoadResult.Page(emptyList(), null, null)


val LazyPagingItems<*>.appendState get() = loadState.append
val LazyPagingItems<*>.refreshState get() = loadState.refresh
val LazyPagingItems<*>.prependState get() = loadState.prepend