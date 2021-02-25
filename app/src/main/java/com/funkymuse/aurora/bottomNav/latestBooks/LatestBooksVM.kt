package com.funkymuse.aurora.bottomNav.latestBooks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazylegend.retrofit.retrofitResult.RetrofitResult
import com.funkymuse.aurora.dto.Book
import com.funkymuse.aurora.repositories.LatestBooksRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LatestBooksVM @Inject constructor(private val latestBooksRepo: LatestBooksRepo) :
    ViewModel() {

    private val latestBooksData: MutableStateFlow<RetrofitResult<List<Book>>> =
        MutableStateFlow(RetrofitResult.EmptyData)
    val booksData = latestBooksData.asStateFlow()

    init {
        latestBooksRepo.searchForBook(viewModelScope, latestBooksData)
    }
}