package com.funkymuse.aurora.navigator

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by funkymuse on 6/26/21 to long live and prosper !
 */
@HiltViewModel
class NavigatorViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel(), Navigator by navigator