package com.funkymuse.aurora.favoritebookdb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funkymuse.aurora.dispatchers.IoDispatcher
import com.funkymuse.aurora.favoritebookdb.db.FavoritesDAO
import com.funkymuse.aurora.navigator.AuroraNavigator
import com.funkymuse.aurora.paging.data.PagingDataProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.map

/**
 * Created by funkymuse, date 3/5/21
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
        private val favoritesDAO: FavoritesDAO,
        pagingDataProvider: PagingDataProvider,
        private val auroraNavigator: AuroraNavigator,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel(), AuroraNavigator by auroraNavigator {

    val favoritesData =
            pagingDataProvider.providePagingData(viewModelScope, ioDispatcher) { favoritesDAO.getAllFavorites() }

    val count = favoritesDAO.favoriteItemsCount()
        .map {
            it == 0
        }

    fun removeFromFavorites(id: String) {
        viewModelScope.launch(ioDispatcher) { favoritesDAO.deleteFromFavoritesByID(id) }
    }
}