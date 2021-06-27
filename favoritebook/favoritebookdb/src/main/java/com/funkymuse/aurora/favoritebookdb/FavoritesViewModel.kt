package com.funkymuse.aurora.favoritebookdb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funkymuse.aurora.favoritebookdb.db.FavoritesDAO
import com.funkymuse.aurora.navigator.Navigator
import com.funkymuse.aurora.paging.data.PagingDataProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Hristijan, date 3/5/21
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
        private val favoritesDAO: FavoritesDAO,
        pagingDataProvider: PagingDataProvider,
        private val navigator: Navigator
) : ViewModel(), Navigator by navigator {

    val favoritesData =
            pagingDataProvider.providePagingData(viewModelScope) { favoritesDAO.getAllFavorites() }

    fun removeFromFavorites(id: Int) {
        viewModelScope.launch { favoritesDAO.deleteFromFavoritesByID(id) }
    }
}