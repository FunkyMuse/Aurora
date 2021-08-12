package com.funkymuse.aurora.downloadsdata

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

/**
 * Created by funkymuse on 8/12/21 to long live and prosper !
 */
class CreateFileContract : ActivityResultContract<Pair<String, String>, Uri?>() {

    /**
     *
     * @param context Context
     * @param input Pair<String, String>, first is the type and second is the title
     * @return Intent
     */
    override fun createIntent(context: Context, input: Pair<String, String>): Intent {
        return Intent(Intent.ACTION_CREATE_DOCUMENT)
            .setType(input.first)
            .putExtra(Intent.EXTRA_TITLE, input.second)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
    }
}