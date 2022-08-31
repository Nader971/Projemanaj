package na.learn.projemanag.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import na.learn.projemanag.R
import na.learn.projemanag.firebase.FirestoreClass
import na.learn.projemanag.models.User

class SignInActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        auth = FirebaseAuth.getInstance()


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {

            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        btn_sign_in.setOnClickListener {
            signInRegisteredUser()
        }

        setupActionBar()

    }

    fun signInSuccess(user: User) {
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()

    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_sign_in_activity)

        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_sign_in_activity.setNavigationOnClickListener { onBackPressed() }

    }

    private fun signInRegisteredUser() {

        val email: String = sign_in_et_email.text.toString().trim() {it <= ' '}
        val password: String = sign_in_et_password.text.toString().trim() {it <= ' '}

        if(validateForm(email, password)) {

            showProgressDialog(resources.getString(R.string.please_wait))

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("SignIn", "signInWithEmail:success")

                        FirestoreClass().loadUserDate(this)

                        startActivity(Intent(this, MainActivity::class.java))

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SignIn", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()

                    }
                }

        }


    }

    private fun validateForm(email: String, password: String): Boolean {

        return when {
            TextUtils.isEmpty(email) ->{
                showErrorSnackBar("Please enter an email address")
                false
            }

            TextUtils.isEmpty(password) ->{
                showErrorSnackBar("Please enter a password")
                false
            }

            else -> {
                true
            }

        }

    }

}