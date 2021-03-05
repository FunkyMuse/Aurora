package com.funkymuse.aurora.abstracts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import kotlinx.coroutines.flow.Flow

/**
 * Created by Hristijan, date 3/5/21
 */
abstract class AbstractPagingViewModel : ViewModel() {

    @PublishedApi
    internal val pagingConfig = PagingConfig(pageSize = 20, enablePlaceholders = true)

    inline fun <T : Any> provideDatabaseData(crossinline function: () -> PagingSource<Int, T>): Flow<PagingData<T>> =
         Pager(pagingConfig) {
            function()
        }.flow.cachedIn(viewModelScope)

}