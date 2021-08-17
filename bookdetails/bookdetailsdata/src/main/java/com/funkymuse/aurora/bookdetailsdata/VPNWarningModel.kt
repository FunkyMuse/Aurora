package com.funkymuse.aurora.bookdetailsdata

/**
 * Created by funkymuse on 8/17/21 to long live and prosper !
 */
sealed class VPNWarningModel {
    object Idle : VPNWarningModel()
    data class DownloadBook(val id:String, val extension:String, val title:String) : VPNWarningModel()
    object Dismiss : VPNWarningModel()
}
