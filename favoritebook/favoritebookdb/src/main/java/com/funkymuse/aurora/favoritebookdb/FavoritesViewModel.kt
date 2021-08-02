package com.funkymuse.aurora.favoritebookdb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funkymuse.aurora.dispatchers.IoDispatcher
import com.funkymuse.aurora.favoritebookdb.db.FavoritesDAO
import com.funkymuse.aurora.navigator.Navigator
import com.funkymuse.aurora.paging.data.PagingDataProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by funkymuse, date 3/5/21
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
        private val favoritesDAO: FavoritesDAO,
        pagingDataProvider: PagingDataProvider,
        private val navigator: Navigator,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel(), Navigator by navigator {

    val favoritesData =
            pagingDataProvider.providePagingData(viewModelScope, ioDispatcher) { favoritesDAO.getAllFavorites() }

    val count = favoritesDAO.favoriteItemsCount()

    fun removeFromFavorites(id: String) {
        viewModelScope.launch(ioDispatcher) { favoritesDAO.deleteFromFavoritesByID(id) }
    }
}