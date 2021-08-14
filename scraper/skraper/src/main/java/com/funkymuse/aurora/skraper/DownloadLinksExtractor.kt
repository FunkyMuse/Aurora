package com.funkymuse.aurora.skraper

import android.content.Context
import com.crazylegend.common.isOnline
import com.funkymuse.aurora.dispatchers.IoDispatcher
import com.funkymuse.aurora.scrapermodel.ScraperResult
import com.funkymuse.aurora.serverconstants.DEFAULT_API_TIMEOUT
import com.funkymuse.aurora.serverconstants.LIBGEN_LC
import com.funkymuse.aurora.serverconstants.LIBRARY_LOL
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.SocketTimeoutException
import javax.inject.Inject

/**
 * Created by funkymuse on 8/9/21 to long live and prosper !
 */
class DownloadLinksExtractor @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) {

    suspend fun extract(url: String): ScraperResult {
        if (!context.isOnline) return ScraperResult.NoConnection

        return when {
            url.contains(LIBRARY_LOL, true) -> extractor { extractLibgenLolDownloadLink(url) }
            url.contains(LIBGEN_LC, true) -> extractor { extractLibgenLCDownloadLink(url) }
            else -> ScraperResult.Idle
        }
    }

    private suspend fun extractor(result: suspend () -> String?): ScraperResult {
        try {
            val url = result() ?: return ScraperResult.UrlNotFound
            return ScraperResult.Success(url)
        } catch (throwable: SocketTimeoutException) {
            throwable.printStackTrace()
            return ScraperResult.TimeOut
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            return ScraperResult.Error
        }
    }

    private suspend fun getDocument(url: String): Document =
        withContext(dispatcher) {
            Jsoup.connect(url).apply {
                timeout(DEFAULT_API_TIMEOUT)
                url(url)
            }.get()
        }


    private suspend fun extractLibgenLCDownloadLink(url: String): String? {
        val document = getDocument(url)
        val link = document.select("a").firstOrNull()?.attr("href")
        link ?: return null
        return LIBGEN_LC + link
    }

    private suspend fun extractLibgenLolDownloadLink(url: String): String? {
        val document = getDocument(url)
        return document.getElementById("download")?.select("a")?.firstOrNull()?.attr("href")
    }


}