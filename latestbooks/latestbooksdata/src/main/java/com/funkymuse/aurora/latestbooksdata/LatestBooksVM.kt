package com.funkymuse.aurora.latestbooksdata

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.funkymuse.aurora.dispatchers.IoDispatcher
import com.funkymuse.aurora.navigator.Navigator
import com.funkymuse.aurora.paging.data.PagingDataProvider
import com.funkymuse.aurora.paging.data.PagingDataSourceHandle
import com.funkymuse.aurora.paging.stateHandleDelegate
import com.funkymuse.aurora.serverconstants.SORT_SIZE
import com.funkymuse.aurora.serverconstants.SORT_TYPE_ASC
import com.funkymuse.aurora.serverconstants.SORT_TYPE_DESC
import com.funkymuse.aurora.serverconstants.SORT_YEAR_CONST
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class LatestBooksVM @Inject constructor(
        application: Application,
        override val savedStateHandle: SavedStateHandle,
        dataProvider: PagingDataProvider,
        private val navigator: Navigator,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AndroidViewModel(application), PagingDataSourceHandle, Navigator by navigator {

    private companion object {
        private const val SORT_QUERY_KEY = "sortQuery"
        private const val SORT_TYPE_KEY = "sortType"
    }

    private val latestBooksDataSource get() = LatestBooksDataSource(getApplication())

    val pagingData = dataProvider.providePagingData(viewModelScope, ioDispatcher) { latestBooksDataSource }

    private var sortType by stateHandleDelegate<String>(SORT_TYPE_KEY)
    private var sortQuery by stateHandleDelegate<String>(SORT_QUERY_KEY)

    private fun resetOnSort() {
        sortType = ""
        sortQuery = ""
        latestBooksDataSource.canLoadMore = true
    }

    fun sortByYearDESC() {
        resetOnSort()
        sortType = SORT_YEAR_CONST
        sortQuery = SORT_TYPE_DESC
    }

    fun sortByYearASC() {
        resetOnSort()
        sortType = SORT_YEAR_CONST
        sortQuery = SORT_TYPE_ASC
    }

    fun sortByDefault() {
        resetOnSort()
    }

    fun sortBySizeDESC() {
        resetOnSort()
        sortType = SORT_SIZE
        sortQuery = SORT_TYPE_DESC
    }

    fun sortBySizeASC() {
        resetOnSort()
        sortType = SORT_SIZE
        sortQuery = SORT_TYPE_ASC
    }

    fun refresh() {
        resetOnSort()
    }

}