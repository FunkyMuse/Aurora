package com.funkymuse.aurora.internetdetector

import androidx.lifecycle.ViewModel
import com.crazylegend.internetdetector.InternetDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by funkymuse on 3/17/21 to long live and prosper !
 */
@HiltViewModel
class InternetDetectorViewModel @Inject constructor(internetDetector: InternetDetector) :
        ViewModel(), Flow<Boolean> by internetDetector.state