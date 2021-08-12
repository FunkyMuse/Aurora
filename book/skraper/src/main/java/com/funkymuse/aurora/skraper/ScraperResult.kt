package com.funkymuse.aurora.skraper

/**
 * Created by funkymuse on 8/10/21 to long live and prosper !
 */
sealed class ScraperResult {

    data class Success(val link:String) : ScraperResult()
    object Loading : ScraperResult()
    object UrlNotFound : ScraperResult()
    object TimeOut : ScraperResult()
    object Idle : ScraperResult()
    object Error : ScraperResult()
}