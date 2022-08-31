package na.learn.projemanag.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_progress.*
import na.learn.projemanag.R

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    private lateinit var mProgressDialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)


    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        /* Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen
         */
        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.tv_progress_text.text = text

        //Start the dialog and display it on screen
        mProgressDialog.show()


    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun getCurrentUserId(): String {

        return FirebaseAuth.getInstance().currentUser!!.uid

    }


    fun doubleBackToExit() {
        if(doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)

    }

    fun showErrorSnackBar(message: String) {

        val snackBar = Snackbar.make(findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
            )
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,
            R.color.snackbar_error_color))

        snackBar.show()

    }


}