package com.funkymuse.aurora.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.funkymuse.aurora.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Hristijan, date 4/14/21
 */
@HiltViewModel
class SearchViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    val searchInFieldEntries
        get() = listOf(
            RadioButtonEntries(R.string.default_column),
            RadioButtonEntries(R.string.title),
            RadioButtonEntries(R.string.author),
            RadioButtonEntries(R.string.series),
            RadioButtonEntries(R.string.publisher),
            RadioButtonEntries(R.string.year),
            RadioButtonEntries(R.string.isbn),
            RadioButtonEntries(R.string.language),
            RadioButtonEntries(R.string.md5),
            RadioButtonEntries(R.string.tags),
            RadioButtonEntries(R.string.extension),
        )

    val sortList
        get() = listOf(
            Pair(0, R.string.default_sort),
            Pair(1, R.string.year_asc),
            Pair(2, R.string.year_desc),
            Pair(3, R.string.size_asc),
            Pair(4, R.string.size_desc),
            Pair(5, R.string.author_asc),
            Pair(6, R.string.author_desc),
            Pair(7, R.string.title_asc),
            Pair(8, R.string.title_desc),
            Pair(9, R.string.extension_asc),
            Pair(10, R.string.extension_desc),
            Pair(11, R.string.publisher_asc),
            Pair(12, R.string.publisher_desc),
        )
}