package com.funkymuse.aurora.paging.data

import androidx.paging.*
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by funkymuse on 6/18/21 to long live and prosper !
 */
@ViewModelScoped
class PagingDataProvider @Inject constructor() {

    @PublishedApi
    internal val pagingConfig = PagingConfig(pageSize = 20, enablePlaceholders = true)

    inline fun <T : Any> providePagingData(
        viewModelScope: CoroutineScope,
        crossinline function: () -> PagingSource<Int, T>
    ): Flow<PagingData<T>> =
        Pager(pagingConfig) {
            function()
        }.flow.cachedIn(viewModelScope)
}