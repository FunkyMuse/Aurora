package com.funkymuse.aurora.favorites

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.funkymuse.aurora.abstracts.AbstractPagingViewModel
import com.funkymuse.aurora.favorites.db.FavoritesDAO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Hristijan, date 3/5/21
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesDAO: FavoritesDAO,
    application: Application
) :
    AbstractPagingViewModel(application) {

    val favoritesData = providePagingData { favoritesDAO.getAllFavorites() }

    fun removeFromFavorites(id: Int) {
        viewModelScope.launch { favoritesDAO.deleteFromFavoritesByID(id) }
    }
}