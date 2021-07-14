package com.funkymuse.aurora.searchresultdata

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.funkymuse.aurora.dispatchers.IoDispatcher
import com.funkymuse.aurora.navigator.Navigator
import com.funkymuse.aurora.paging.data.PagingDataProvider
import com.funkymuse.aurora.paging.data.PagingDataSourceHandle
import com.funkymuse.aurora.paging.stateHandleArgument
import com.funkymuse.aurora.paging.stateHandleDelegate
import com.funkymuse.aurora.searchresultdestination.SearchResultDestination.SEARCH_IN_FIELDS_PARAM
import com.funkymuse.aurora.searchresultdestination.SearchResultDestination.SEARCH_PARAM
import com.funkymuse.aurora.searchresultdestination.SearchResultDestination.SEARCH_WITH_MASK_WORD_PARAM
import com.funkymuse.aurora.serverconstants.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by funkymuse on 3/8/21 to long live and prosper !
 */

@HiltViewModel
class SearchResultHandleDataViewModel @Inject constructor(
    application: Application,
    override val savedStateHandle: SavedStateHandle,
    dataProvider: PagingDataProvider,
    private val navigator: Navigator,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val searchResultDataSourceFactory: SearchResultDataSource.SearchResultDataSourceFactory
) : AndroidViewModel(application), PagingDataSourceHandle, Navigator by navigator {

    private companion object {
        private const val SEARCH_IN_FIELDS_CHECKED_POSITION_KEY = "searchInFieldsCheckedPosition"
        private const val SEARCH_WITH_MASKED_WORD_KEY = "searchWithMaskWord"
        private const val SORT_QUERY_KEY = "sortQuery"
        private const val SORT_TYPE_KEY = "sortType"
    }

    private val searchResultDataSource
        get() = searchResultDataSourceFactory.create(
            searchQuery ?: "",
            searchInFieldsPosition ?: searchInFieldsCheckedPosition,
            sortQuery ?: "",
            maskWord ?: searchWithMaskWord,
            sortType ?: ""
        )
    val booksData = dataProvider.providePagingData(viewModelScope, ioDispatcher) { searchResultDataSource }

    private val searchQuery: String? by stateHandleDelegate(SEARCH_PARAM)

    val searchInFieldsCheckedPosition by stateHandleArgument(SEARCH_IN_FIELDS_PARAM, 0)
    val searchWithMaskWord by stateHandleArgument(SEARCH_WITH_MASK_WORD_PARAM, false)


    private var searchInFieldsPosition by stateHandleDelegate<Int>(
        SEARCH_IN_FIELDS_CHECKED_POSITION_KEY
    )

    private var maskWord by stateHandleDelegate<Boolean>(SEARCH_WITH_MASKED_WORD_KEY)

    private var sortType by stateHandleDelegate<String>(SORT_TYPE_KEY)

    private var sortQuery by stateHandleDelegate<String>(SORT_QUERY_KEY)


    private fun resetOnSort() {
        sortQuery = ""
        sortType = ""
        searchResultDataSource.canLoadMore = true
    }

    private fun sortByYearDESC() {
        resetOnSort()
        sortQuery = SORT_YEAR_CONST
        sortType = SORT_TYPE_DESC
    }

    private fun sortByYearASC() {
        resetOnSort()
        sortQuery = SORT_YEAR_CONST
        sortType = SORT_TYPE_ASC
    }

    private fun sortByDefault() {
        resetOnSort()
    }

    private fun sortBySizeDESC() {
        resetOnSort()
        sortQuery = SORT_SIZE
        sortType = SORT_TYPE_DESC
    }

    private fun sortBySizeASC() {
        resetOnSort()
        sortQuery = SORT_SIZE
        sortType = SORT_TYPE_ASC
    }

    fun refresh() {
        resetOnSort()
    }

    /**
     *  Pair(0, R.string.default_sort),
    Pair(1, R.string.year_asc),
    Pair(2, R.string.year_desc),
    Pair(3, R.string.size_asc),
    Pair(4, R.string.size_desc),
    Pair(5, R.string.author_asc),
    Pair(6, R.string.author_desc),
    Pair(7, R.string.title_asc),
    Pair(8, R.string.title_desc),
    Pair(9, R.string.extension_asc),
    Pair(10, R.string.extension_desc),
    Pair(11, R.string.publisher_asc),
    Pair(12, R.string.publisher_desc),
     * @param position Int
     */
    fun sortByPosition(position: Int) {
        when (position) {
            0 -> sortByDefault()
            1 -> sortByYearASC()
            2 -> sortByYearDESC()
            3 -> sortBySizeASC()
            4 -> sortBySizeDESC()
            5 -> sortByAuthorASC()
            6 -> sortByAuthorDESC()
            7 -> sortByTitleASC()
            8 -> sortByTitleDESC()
            9 -> sortByExtensionASC()
            10 -> sortByExtensionDESC()
            11 -> sortByPublisherASC()
            12 -> sortByPublisherDESC()
        }
    }

    private fun sortByPublisherASC() {
        resetOnSort()
        sortQuery = SORT_PUBLISHER
        sortType = SORT_TYPE_ASC
    }

    private fun sortByPublisherDESC() {
        resetOnSort()
        sortQuery = SORT_PUBLISHER
        sortType = SORT_TYPE_DESC
    }

    private fun sortByExtensionASC() {
        resetOnSort()
        sortQuery = SORT_EXTENSION
        sortType = SORT_TYPE_ASC
    }

    private fun sortByExtensionDESC() {
        resetOnSort()
        sortQuery = SORT_EXTENSION
        sortType = SORT_TYPE_DESC
    }

    private fun sortByTitleASC() {
        resetOnSort()
        sortQuery = SORT_TITLE
        sortType = SORT_TYPE_ASC
    }

    private fun sortByTitleDESC() {
        resetOnSort()
        sortQuery = SORT_TITLE
        sortType = SORT_TYPE_DESC
    }

    private fun sortByAuthorDESC() {
        resetOnSort()
        sortQuery = SORT_AUTHOR
        sortType = SORT_TYPE_DESC
    }


    private fun sortByAuthorASC() {
        resetOnSort()
        sortQuery = SORT_AUTHOR
        sortType = SORT_TYPE_ASC
    }

    fun searchWithMaskedWord(maskedWord: Boolean) {
        resetOnSort()
        maskWord = maskedWord
    }

    fun searchInFieldsByPosition(position: Int) {
        resetOnSort()
        searchInFieldsPosition = position
    }


}