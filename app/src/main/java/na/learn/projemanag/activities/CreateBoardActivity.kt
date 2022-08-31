package na.learn.projemanag.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import na.learn.projemanag.R
import na.learn.projemanag.models.User
import na.learn.projemanag.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        //private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserDetails: User
    private var mCreateBoardImageURL: String = ""

    private lateinit var mUserName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        if(intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }

        setupActionBar()

        iv_create_board_image.setOnClickListener {
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
    }

    fun boardCreatedSuccessfully() {
        hideProgressDialog()
        finish()
    }


    private fun setupActionBar() {
        setSupportActionBar(toolbar_create_board_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)

        }

        toolbar_create_board_activity.setNavigationOnClickListener { onBackPressed() }

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
                    .into(iv_create_board_image)
            }catch (e: IOException) {
                e.printStackTrace()
            }



        }
    }







}