package com.funkymuse.aurora.bookDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazylegend.retrofit.retrofitStateInitialLoading
import com.funkymuse.aurora.api.LibgenAPI
import com.funkymuse.aurora.dto.DetailedBookModel
import com.funkymuse.aurora.dto.FavoriteBook
import com.funkymuse.aurora.favorites.db.FavoritesDAO
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
) : ViewModel() {

    private val id get() = savedStateHandle.get<Int>(BOOK_ID_PARAM)!!

    private val booksData = retrofitStateInitialLoading<List<DetailedBookModel>>()
    val book = booksData.asStateFlow()

    private val favoriteBookData: MutableStateFlow<FavoriteBook?> = MutableStateFlow(null)
    val favoriteBook = favoriteBookData.asStateFlow()

    init {
        loadBook()
    }

    private fun loadBook() {
        viewModelScope.launch {
            val books = async { libgenAPI.getDetailedBook(id) }
            val favorite = async { favoritesDAO.getFavoriteById(id) }
            favorite.await().onEach { favoriteBookData.value = it }.launchIn(viewModelScope)
            booksData.value = books.await()
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