package na.learn.projemanag.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_intro.*
import na.learn.projemanag.R

class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {

            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }


        btn_sign_in_intro.setOnClickListener {

            startActivity(Intent(this, SignInActivity::class.java))

        }

        btn_sign_up_intro.setOnClickListener {

            startActivity(Intent(this, SignUpActivity::class.java))

        }


    }
}