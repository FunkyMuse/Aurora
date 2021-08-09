package com.funkymuse.aurora.bookdetailsdata

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.crazylegend.retrofit.retrofitStateInitialLoading
import com.funkymuse.aurora.bookdetailsdestination.BookDetailsDestination.BOOK_ID_PARAM
import com.funkymuse.aurora.bookdownloader.BookDownloadScheduler
import com.funkymuse.aurora.dispatchers.IoDispatcher
import com.funkymuse.aurora.favoritebookdb.db.FavoritesDAO
import com.funkymuse.aurora.favoritebookmodel.FavoriteBook
import com.funkymuse.aurora.libgenapi.LibgenAPI
import com.funkymuse.aurora.navigator.Navigator
import com.funkymuse.aurora.skraper.DownloadLinksExtractor
import com.funkymuse.bookdetails.bookdetailsmodel.DetailedBookModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
    private val navigator: Navigator,
    private val downloadLinksExtractor: DownloadLinksExtractor,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    application: Application,
    private val bookDownloadScheduler : BookDownloadScheduler
) : AndroidViewModel(application), Navigator by navigator {

    private val id
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
            val detailedBook = async(dispatcher) { libgenAPI.getDetailedBook(id) }
            val favorite = async(dispatcher) { favoritesDAO.getFavoriteById(id) }
            favorite.await().onEach { favoriteBookData.value = it }.launchIn(viewModelScope)
            bookData.value = detailedBook.await()
        }
    }

    fun addToFavorites(favoriteBook: FavoriteBook) {
        viewModelScope.launch(dispatcher) { favoritesDAO.insertIntoFavorites(favoriteBook) }
    }

    fun removeFromFavorites(favoriteBookID: String) {
        viewModelScope.launch(dispatcher) { favoritesDAO.deleteFromFavoritesByID(favoriteBookID) }
    }

    fun retry() {
        loadBook()
    }

    fun downloadBook(link: String, extension:String, bookName:String) {

        try {
            viewModelScope.launch(dispatcher) {

                val url = downloadLinksExtractor.extract(link) ?: return@launch
                bookDownloadScheduler.scheduleDownload(url, id, extension, bookName)
                Log.d("LINK EXTRACTED", url)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

}