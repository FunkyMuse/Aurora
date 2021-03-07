package com.funkymuse.aurora.searchResult

import com.crazylegend.kotlinextensions.internetdetector.InternetDetector
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Created by funkymuse on 3/8/21 to long live and prosper !
 */

class SearchResultVM @AssistedInject constructor(
    @Assisted private val query: String,
    internetDetector: InternetDetector
) {
    @AssistedFactory
    interface SearchResultVMF {
        fun create(movieName: String, language: String, languageFullName: String): SearchResultVM
    }

    val internetConnection = internetDetector.state

}