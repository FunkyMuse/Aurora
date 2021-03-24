package com.funkymuse.aurora.internetDetector

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.crazylegend.kotlinextensions.internetdetector.InternetDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by funkymuse on 3/17/21 to long live and prosper !
 */
@HiltViewModel
class InternetDetectorViewModel @Inject constructor(
    application: Application,
    internetDetector: InternetDetector
) : AndroidViewModel(application) {

    val internetConnection = internetDetector.state

}