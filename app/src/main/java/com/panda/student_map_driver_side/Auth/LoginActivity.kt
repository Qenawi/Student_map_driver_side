package com.panda.student_map_driver_side.Auth

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import carbon.widget.Button
import carbon.widget.EditText
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.*
import com.panda.student_map_driver_side.R
import com.panda.student_map_driver_side.data.Failure
import com.panda.student_map_driver_side.data.Result
import com.panda.student_map_driver_side.data.Success
import com.panda.student_map_driver_side.main_screen.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.os.Build
import android.support.annotation.RequiresApi
import carbon.view.View
import carbon.widget.ProgressBar


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var loginButton: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var progress: ProgressBar
    private lateinit var registerButtton: Button
    lateinit var dbRef: DatabaseReference;
    @Throws(IOException::class, Exception::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        progress = findViewById(R.id.regProgressBar)
        dbRef = FirebaseDatabase.getInstance().reference
        email = findViewById(R.id.user_driver) as EditText
        password = findViewById(R.id.password_driver) as EditText
        loginButton = findViewById(R.id.bu_login_driver) as Button
        registerButtton = findViewById(R.id.bu_register_driver) as Button



        registerButtton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            try {
                progress.visibility = View.VISIBLE
                Login()
            } catch (e: Exception) {
                Toast.makeText(this, "Failed To Create USer Data pleas re login Again ..", Toast.LENGTH_SHORT).show()
                progress.visibility = View.INVISIBLE

            }

        }
    }

    @Throws(FirebaseAuthException::class, FirebaseNetworkException::class, IOException::class, Exception::class)
    fun Login() {
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())

            .addOnSuccessListener {
                val uId = it.user.uid
                Check(uId)
            }.addOnFailureListener {
                Toast.makeText(this, "Failed To Create USer Data pleas re login Again ..", Toast.LENGTH_SHORT).show()
            }
    }


    fun Check(uId: String) {


        dbRef.child("Drivers").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChild("dr$uId")) {
                    startMain()
                } else {
                    GlobalScope.launch {
                        val e = AddChild(uId)
                        when (e) {
                            is Success -> {
                                withContext(Dispatchers.Main)
                                {
                                    withContext(Dispatchers.Main)
                                    {

                                        startMain()
                                    }
                                }
                            }
                            is Failure -> {
                                withContext(Dispatchers.Main)
                                {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "pleas re login Again ..",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }//whem
                    }// launch
                }//else
            }//o data Change
        })// add Listners


    }

    suspend fun AddChild(userId: String): Result<Boolean> = suspendCoroutine {

        dbRef.child("Drivers").child("dr$userId")
            .setValue(User("name", "email", "password", "dliciens", "location", "phone", userId))
            .addOnCompleteListener { it2: Task<Void> ->
                if (it2.isSuccessful) {
                    Toast.makeText(this, "Created With Success...", Toast.LENGTH_SHORT).show()
                    it.resume(Success(true))
                } else {
                    it2.exception?.printStackTrace()
                    Toast.makeText(this, "Failed To Create USer Data pleas re login Again ..", Toast.LENGTH_SHORT)
                        .show()
                    it.resume(Failure(TimeoutException("Faild To Log in")))
                }
            }
    }

    fun startMain() {
        progress.visibility = View.INVISIBLE

        Toast.makeText(this@LoginActivity, "Logged in Successfully..", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }


}