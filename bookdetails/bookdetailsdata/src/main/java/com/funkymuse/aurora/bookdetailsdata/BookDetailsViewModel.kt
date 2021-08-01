package com.funkymuse.aurora.bookdetailsdata

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazylegend.retrofit.retrofitStateInitialLoading
import com.funkymuse.aurora.bookdetailsdestination.BookDetailsDestination.BOOK_ID_PARAM
import com.funkymuse.aurora.favoritebookdb.db.FavoritesDAO
import com.funkymuse.aurora.favoritebookmodel.FavoriteBook
import com.funkymuse.aurora.libgenapi.LibgenAPI
import com.funkymuse.aurora.navigator.Navigator
import com.funkymuse.bookdetails.bookdetailsmodel.DetailedBookModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by FunkyMuse, date 2/28/21
 */
@HiltViewModel
class BookDetailsViewModel @Inject constructor(
        private val savedStateHandle: SavedStateHandle,
        private val libgenAPI: LibgenAPI,
        private val favoritesDAO: FavoritesDAO,
        private val navigator: Navigator
) : ViewModel(), Navigator by navigator {

    val id
        get() = savedStateHandle.get<String>(BOOK_ID_PARAM)
                ?: throw IllegalStateException("Parameter book ID must not be null!")

    private val bookData = retrofitStateInitialLoading<List<DetailedBookModel>>()
    val book = bookData.asStateFlow()

    private val favoriteBookData: MutableStateFlow<FavoriteBook?> = MutableStateFlow(null)
    val favoriteBook = favoriteBookData.asStateFlow()

    init {
        loadBook()
    }

    private fun loadBook() {
        viewModelScope.launch {
            val detailedBook = async { libgenAPI.getDetailedBook(id) }
            val favorite = async { favoritesDAO.getFavoriteById(id) }
            favorite.await().onEach { favoriteBookData.value = it }.launchIn(viewModelScope)
            bookData.value = detailedBook.await()
        }
    }

    fun addToFavorites(favoriteBook: FavoriteBook) {
        viewModelScope.launch { favoritesDAO.insertIntoFavorites(favoriteBook) }
    }

    fun removeFromFavorites(favoriteBookID: Int) {
        viewModelScope.launch { favoritesDAO.deleteFromFavoritesByID(favoriteBookID) }
    }

    fun retry() {
        loadBook()
    }

}