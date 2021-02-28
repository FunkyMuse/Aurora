package com.funkymuse.aurora.bookDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazylegend.retrofit.retrofitResult.RetrofitResult
import com.crazylegend.retrofit.retrofitResult.loading
import com.funkymuse.aurora.api.LibgenAPI
import com.funkymuse.aurora.dto.DetailedBookModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by Hristijan, date 2/28/21
 */
class BookDetailsViewModel @AssistedInject constructor(
    @Assisted private val id: Int,
    private val libgenAPI: LibgenAPI
) : ViewModel() {

    @AssistedFactory
    interface BookDetailsVMF {
        fun create(id: Int): BookDetailsViewModel
    }

    private val booksData: MutableStateFlow<RetrofitResult<List<DetailedBookModel>>> =
        MutableStateFlow(RetrofitResult.EmptyData)
    val book = booksData.asStateFlow()

    init {
        getDetailedBook()
    }

    private fun getDetailedBook() {
        booksData.loading()
        viewModelScope.launch {
            booksData.value = libgenAPI.getDetailedBook(id)
        }
    }
}