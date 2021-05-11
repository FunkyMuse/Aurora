package com.funkymuse.aurora.paging

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by funkymuse on 5/11/21 to long live and prosper !
 */
@HiltViewModel
class PagingProviderViewModel @Inject constructor(
    private val pagingUIProvider: PagingUIProvider
) : PagingUIProviderContract by pagingUIProvider, ViewModel()