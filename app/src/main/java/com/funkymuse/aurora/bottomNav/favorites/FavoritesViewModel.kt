package com.funkymuse.aurora.bottomNav.favorites

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funkymuse.aurora.abstracts.AbstractPagingViewModel
import com.funkymuse.aurora.bottomNav.favorites.db.FavoritesDAO
import com.funkymuse.aurora.mirrorsDB.MirrorsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Hristijan, date 3/5/21
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(private val favoritesDAO: FavoritesDAO, private val mirrorsRepository: MirrorsRepository) :
    AbstractPagingViewModel() {

    val favoritesData = provideDatabaseData { favoritesDAO.getAllFavorites() }

    fun saveMirrorsForBookId(id: Int, mirrors: List<String>?) {
        viewModelScope.launch {
            mirrorsRepository.saveMirrorsForBookId(id.toString(), mirrors)
        }
    }

    fun removeFromFavorites(id: Int) {
        viewModelScope.launch { favoritesDAO.deleteFromFavoritesByID(id) }
    }
}