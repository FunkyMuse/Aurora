package com.funkymuse.aurora.bookdetailsdata

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
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
import com.funkymuse.aurora.scrapermodel.ScraperResult
import com.funkymuse.bookdetails.bookdetailsmodel.DetailedBookModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
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
    private val bookDownloadScheduler: BookDownloadScheduler
) : ViewModel(), Navigator by navigator {

    private val id
        get() = savedStateHandle.get<String>(BOOK_ID_PARAM) ?: throw IllegalStateException("Parameter book ID must not be null!")

    private val bookData = retrofitStateInitialLoading<List<DetailedBookModel>>()
    val book = bookData.asStateFlow()

    private val favoriteBookData: MutableStateFlow<FavoriteBook?> = MutableStateFlow(null)
    val favoriteBook = favoriteBookData.asStateFlow()

    private val extractLinkEvent: Channel<ScraperResult> = Channel(Channel.BUFFERED)
    val extractLink = extractLinkEvent.receiveAsFlow()

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

    fun downloadBook(link: String, extension: String, bookName: String) {
        extractLinkEvent.trySend(ScraperResult.Loading)
        viewModelScope.launch(dispatcher) {
            val url = downloadLinksExtractor.extract(link)
            extractLinkEvent.send(url)
            if (url is ScraperResult.Success) {
                bookDownloadScheduler.scheduleDownload(url.link, id, extension, bookName)
            }
        }
    }

}