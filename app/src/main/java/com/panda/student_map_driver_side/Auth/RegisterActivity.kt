package com.panda.student_map_driver_side.Auth

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import carbon.view.View
import carbon.widget.Button
import carbon.widget.EditText
import carbon.widget.ProgressBar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.panda.student_map_driver_side.R
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {
    private lateinit var drive_Name: EditText
    private lateinit var location: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var drive_phone: EditText
    private lateinit var registerButton: Button
    private lateinit var drive_drivingLicense: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var dbRef: DatabaseReference
    private lateinit var progress: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dbRef = database.reference
        drive_Name = findViewById(R.id.drive_Name)
        drive_drivingLicense = findViewById(R.id.drive_drivingLicense)
        location = findViewById(R.id.drive_latitude)
        progress = findViewById(R.id.regProgressBar)
        email = findViewById(R.id.drive_Mail)
        password = findViewById(R.id.drive_Password)
        registerButton = findViewById(R.id.driver_btn)
        drive_phone = findViewById(R.id.drive_phone)
        registerButton.setOnClickListener {



            try {

                if (location.text.isNullOrBlank() || drive_drivingLicense.text.isNullOrBlank() || drive_phone.text.isNullOrBlank() || drive_phone.text.isNullOrBlank() || email.text.isNullOrBlank() || password.text.isNullOrBlank()) {
                    Toast.makeText(this, "Pleas Fill All Data Fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                else {
                    progress.visibility = View.VISIBLE
                    auth.createUserWithEmailAndPassword(email.text.toString().trim(), password.text.toString().trim())
                        .addOnSuccessListener {


                                progress.visibility = View.INVISIBLE
                                val userId = auth.currentUser?.uid
                                Log.v("Qnqn", "USerID$userId")

                                val user = User(
                                    drive_Name.text.toString(),
                                    email.text.toString()
                                    ,
                                    password.text.toString(),
                                    drive_drivingLicense.text.toString(),
                                    location.text.toString(),
                                    drive_phone.text.toString(),
                                    userId
                                )
                                dbRef.child("Drivers").child("dr$userId").setValue(user)
                                    .addOnCompleteListener {

                                        if (it.isSuccessful) {
                                            Toast.makeText(this, "Created With Success...", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                            finish()
                                        } else
                                        {
                                            it.exception?.printStackTrace()
                                            Toast.makeText(this, "Failed To Create USer Data...", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } .addOnFailureListener{
                            progress.visibility = View.INVISIBLE
                            Toast.makeText(this, "Failed To Create Account", Toast.LENGTH_SHORT).show()
                        }
                        }//else

            }catch (e:Exception)
            {
                Toast.makeText(this, "Faild To Create Account Pleas Try Again", Toast.LENGTH_SHORT).show()

                e.printStackTrace()
            }


        }
    }
}