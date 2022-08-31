package na.learn.projemanag.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_my_profile.*
import na.learn.projemanag.R
import na.learn.projemanag.firebase.FirestoreClass
import na.learn.projemanag.models.User
import na.learn.projemanag.utils.Constants
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserDetails: User
    private var mProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setupActionBar()

        FirestoreClass().loadUserDate(this)

        iv_profile_user_image.setOnClickListener {

            if(ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE )
            == PackageManager.PERMISSION_GRANTED) {

                showImageChooser()

            } else {

                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                    )

            }

        }

        btn_update.setOnClickListener {

            if (mSelectedImageFileUri != null) {
                uploadUserImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileDate()
            }

        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()            }

        } else {

            Toast.makeText(this,
                "Oops, you just denied the permission for storage. You can allow it from settings.",
                Toast.LENGTH_SHORT
                ).show()

        }

    }

    @SuppressLint("LogConditional")
    private fun uploadUserImage() {

        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {

            val sRef: StorageReference = FirebaseStorage.
            getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis()
                    + "." + getFileExtension(mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                TaskSnapshot ->

                Log.i("Firebase image url",
                    TaskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                TaskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->

                    Log.i("Downloadable image URL", uri.toString())

                    mProfileImageURL = uri.toString()

                    updateUserProfileDate()

                }

            }.addOnFailureListener{
                exception ->

                Toast.makeText(
                    this,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()

                hideProgressDialog()
            }

        }

    }

    private fun getFileExtension(uri: Uri?) : String? {

        return MimeTypeMap.getSingleton().
        getExtensionFromMimeType(contentResolver.getType(uri!!))

    }

    private fun showImageChooser() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        resultLauncher.launch(galleryIntent)


        //startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE )

    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            mSelectedImageFileUri = data!!.data


            try {
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(iv_profile_user_image)
            }catch (e: IOException) {
                e.printStackTrace()
            }



        }
    }



    private fun setupActionBar() {
        setSupportActionBar(toolbar_my_profile_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)

        }

        toolbar_my_profile_activity.setNavigationOnClickListener { onBackPressed() }

    }

    private fun updateUserProfileDate() {
        val userHashMap = HashMap<String, Any>()

        if (mProfileImageURL.isNotEmpty() &&
            mProfileImageURL != mUserDetails.image) {

            userHashMap[Constants.IMAGE] = mProfileImageURL
        }

        if (my_profile_name.text.toString() != mUserDetails.name) {

            userHashMap[Constants.NAME] = my_profile_name.text.toString()

        }

        if (profile_et_mobile.text.toString()
            != mUserDetails.mobile.toString()) {

            if (profile_et_mobile.text.toString() == "") {
                userHashMap[Constants.MOBILE] = 0L
            } else {

                userHashMap[Constants.MOBILE] = profile_et_mobile.text.toString().toLong()

            }

        }

        FirestoreClass().updateUserProfileDate(this, userHashMap)


    }

    fun setUserdataUI(user: User) {

        mUserDetails = user

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_profile_user_image)

        my_profile_name.setText(user.name)
        my_profile_email.setText(user.email)
        if(user.mobile != 0L) {
            profile_et_mobile.setText(user.mobile.toString())

        }

    }

    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()

    }


}

