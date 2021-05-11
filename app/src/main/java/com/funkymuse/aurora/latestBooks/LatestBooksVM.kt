package com.funkymuse.aurora.latestBooks

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.crazylegend.kotlinextensions.livedata.context
import com.funkymuse.aurora.abstracts.AbstractPagingSourceViewModel
import com.funkymuse.aurora.consts.SORT_SIZE
import com.funkymuse.aurora.consts.SORT_TYPE_ASC
import com.funkymuse.aurora.consts.SORT_TYPE_DESC
import com.funkymuse.aurora.consts.SORT_YEAR_CONST
import com.funkymuse.aurora.stateHandleDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LatestBooksVM @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AbstractPagingSourceViewModel(application, savedStateHandle) {

    private companion object {
        private const val SORT_QUERY_KEY = "sortQuery"
        private const val SORT_TYPE_KEY = "sortType"
    }

    private val latestBooksDataSource get() = LatestBooksDataSource(context)

    val pagingData = providePagingData { latestBooksDataSource }

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