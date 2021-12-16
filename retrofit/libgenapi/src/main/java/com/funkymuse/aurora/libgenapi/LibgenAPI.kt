package com.funkymuse.aurora.libgenapi

import com.crazylegend.retrofit.apiresult.ApiResult
import com.funkymuse.aurora.serverconstants.FIELDS_QUERY
import com.funkymuse.aurora.serverconstants.FIELDS_QUERY_CONST
import com.funkymuse.aurora.serverconstants.IDS_QUERY_CONST
import com.funkymuse.aurora.serverconstants.JSON_PHP_PAGE_CONST
import com.funkymuse.bookdetails.bookdetailsmodel.DetailedBookModel
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by hristijan on 10/22/19 to long live and prosper !
 */
interface LibgenAPI {

    @GET(JSON_PHP_PAGE_CONST)
    suspend fun getDetailedBook(
        @Query(IDS_QUERY_CONST) id: String,
        @Query(FIELDS_QUERY_CONST) fields: String = FIELDS_QUERY
    ): ApiResult<List<DetailedBookModel>>
}