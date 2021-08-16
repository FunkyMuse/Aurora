package com.funkymuse.aurora.internetdetector.connectiontype

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by funkymuse on 8/16/21 to long live and prosper !
 */
@HiltViewModel
class ConnectionTypeViewModel @Inject constructor(typeDetector: ConnectionTypeDetector) :
    ViewModel(), Flow<ConnectionType> by typeDetector.state