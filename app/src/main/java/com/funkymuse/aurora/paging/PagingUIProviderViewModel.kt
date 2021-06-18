package com.funkymuse.aurora.paging

import androidx.lifecycle.ViewModel
import com.funkymuse.aurora.paging.ui.PagingUIProvider
import com.funkymuse.aurora.paging.ui.PagingUIProviderContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by funkymuse on 5/11/21 to long live and prosper !
 */
@HiltViewModel
class PagingUIProviderViewModel @Inject constructor(
    private val pagingUIProvider: PagingUIProvider
) : PagingUIProviderContract by pagingUIProvider, ViewModel()