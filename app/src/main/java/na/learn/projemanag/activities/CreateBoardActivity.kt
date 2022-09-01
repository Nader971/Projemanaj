package na.learn.projemanag.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import na.learn.projemanag.R
import na.learn.projemanag.firebase.FirestoreClass
import na.learn.projemanag.models.Board
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

        btn_create_board.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadBoardImage()
            }else if(my_create_board_name.text.isNullOrEmpty()) {
                Toast.makeText(this,
                    "Please type Board name",
                    Toast.LENGTH_SHORT
                    ).show()
            }else {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    createBoard()
            }

        }

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

    private fun createBoard() {
        val assignedUserArrayList: ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentUserId())

        var board = Board(
            my_create_board_name.text.toString(),
            mCreateBoardImageURL,
            mUserName,
            assignedUserArrayList

        )

        FirestoreClass().createBoard(this, board)

    }

    fun boardCreatedSuccessfully() {
        hideProgressDialog()
        finish()
    }

    private fun uploadBoardImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {

            val sRef: StorageReference = FirebaseStorage.
            getInstance().reference.child(
                "BOARD_IMAGE" + System.currentTimeMillis()
                        + "." + Constants.getFileExtension(this, mSelectedImageFileUri!!))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    TaskSnapshot ->

                Log.e("Firebase image url",
                    TaskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                TaskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->

                    Log.e("Downloadable image URL", uri.toString())

                    mCreateBoardImageURL = uri.toString()

                    createBoard()

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