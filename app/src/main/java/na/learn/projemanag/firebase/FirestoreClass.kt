package na.learn.projemanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import na.learn.projemanag.activities.*
import na.learn.projemanag.models.Board
import na.learn.projemanag.models.User
import na.learn.projemanag.utils.Constants

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity: SignUpActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }

    }

    fun createBoard(activity: CreateBoardActivity, board: Board) {
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board created successfully" )

                Toast.makeText(activity,
                    "Board created successfully",
                    Toast.LENGTH_LONG
                    ).show()

                activity.boardCreatedSuccessfully()
            }.addOnFailureListener{
                e->

                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while creating the board",e )

                Toast.makeText(activity,
                    "Error while creating the board",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    fun updateUserProfileDate(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile data updated successfully! ")
                Toast.makeText(activity, "Profile data updated successfully!",
                    Toast.LENGTH_LONG
                    ).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener{
                e ->
                activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName, "Error! ", e)
                Toast.makeText(activity, "Updated Fail!",
                    Toast.LENGTH_LONG
                ).show()

            }
    }

    fun loadUserDate(activity: Activity) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!

                when(activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)

                    }

                    is MainActivity -> {

                        activity.updateNavigationUserDetails(loggedInUser)

                    }

                    is MyProfileActivity -> {
                        activity.setUserdataUI(loggedInUser )
                    }

                }


            }.addOnFailureListener {
                    e->
                when(activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()

                    }

                    is MainActivity -> {

                        activity.hideProgressDialog()

                    }

                }

                Log.d("SignInUser", "Error writing document", e)
            }

    }

    fun getCurrentUserID():String {

        var currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""

        if(currentUser != null) {

            currentUserID = currentUser.uid

        }

        return currentUserID
    }


}