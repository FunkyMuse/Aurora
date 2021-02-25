/*
package com.crazylegend.aurora.repositories

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.crazylegend.aurora.consts.*
import com.crazylegend.aurora.dto.Book
import com.crazylegend.kotlinextensions.context.isOnline
import com.crazylegend.kotlinextensions.retrofit.*
import com.crazylegend.kotlinextensions.retrofit.throwables.NoConnectionException
import com.crazylegend.kotlinextensions.rx.ioThreadScheduler
import com.crazylegend.kotlinextensions.rx.mainThreadScheduler
import com.crazylegend.kotlinextensions.rx.singleFrom
import com.funkymuse.aurora.consts.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


*/
/**
 * Created by FunkyMuse on 10/21/19 to long live and prosper !
 *//*


class SearchForBookRepo(
        private val compositeDisposable: CompositeDisposable, private val searchQuery: String,
        private val retrofitResult: MutableLiveData<RetrofitResult<List<Book>>>,
        private val application: Application,
        private val sortQuery: String = "",
        private val sortType: String = ""
) {

    private var page = 1
    private var canLoadMore = true
    private val adapterList: ArrayList<Book> = ArrayList()

    private val jsoup by lazy {
        Jsoup.connect(SEARCH_BASE_URL)
                .timeout(DEFAULT_API_TIMEOUT)
                .data(REQ_CONST, searchQuery)
                .data(VIEW_QUERY, VIEW_QUERY_PARAM)
                .data(COLUM_QUERY, COLUMN_QUERY_PARAM)
                .data(RES_CONST, PAGE_SIZE)
                .data(SORT_QUERY, sortQuery)
                .data(SORT_TYPE, sortType)
                .data(PAGE_CONST, page.toString())
    }

    init {
        searchForBook()
    }

    fun searchForBook() {
        if (canLoadMore) {
            retrofitResult.loading()
            singleFrom {
                if (!application.isOnline)
                    throw NoConnectionException()
                jsoup.get()
            }.subscribeOn(ioThreadScheduler)
                    .observeOn(mainThreadScheduler)
                    .subscribe({
                        if (it == null) {
                            retrofitResult.emptyData()
                        } else {
                            val list = processDocument(it)
                            if (list.isNullOrEmpty()) {
                                retrofitResult.emptyData()
                            } else {
                                adapterList += list
                                retrofitResult.success(adapterList)
                            }
                        }
                    }, {
                        retrofitResult.callErrorPost(it)
                        it.printStackTrace()
                    }).addTo(compositeDisposable)
        }
    }


    private fun processDocument(doc: Document?): List<Book>? {
        return doc?.let { document ->

            val trs = document.select("table")[2].select("tr")
            trs.removeAt(0)

            if (trs.size < PAGE_SIZE.toInt()) {
                //cant load more
                canLoadMore = false
            } else {
                page++
                //has more pages
            }

            return@let trs.map {
                return@map Book(it)
            }
        }
    }

}

*/
