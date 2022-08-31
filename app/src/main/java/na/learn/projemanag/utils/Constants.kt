package na.learn.projemanag.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_my_profile.*
import na.learn.projemanag.R
import java.io.IOException

object Constants {

    const val USERS: String = "users"
    const val BOARDS: String = "boards"
    const val NAME: String = "name"
    const val IMAGE: String = "image"
    const val MOBILE: String = "mobile"

    fun getFileExtension(activity: Activity, uri: Uri?) : String? {

        return MimeTypeMap.getSingleton().
        getExtensionFromMimeType(activity.contentResolver.getType(uri!!))

    }



}