package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity(), View.OnKeyListener {

    var emailEditText: EditText?=null
    var passEditText: EditText?=null

    val auth =FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText=findViewById(R.id.emailEditText)
        passEditText=findViewById(R.id.passEditText)

        if(auth.currentUser!=null){
            logIn();
        }
    }



    override fun onKey(v: View, keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode==KeyEvent.KEYCODE_ENTER && event?.action==KeyEvent.ACTION_DOWN ){
            goclicked(v)   // v!! to say that its not nullable....without exclamations it'll say..requied "view" found "view?"
        }
        return false
    }

    fun goclicked(view:View){
        //check if we can log in the user
        auth.signInWithEmailAndPassword(emailEditText?.text.toString(), passEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    logIn()
                } else {

                    // If sign in fails, sign up
                    auth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passEditText?.text.toString()).addOnCompleteListener(this){task ->
                        if(task.isSuccessful){
                            //add to database
                            FirebaseDatabase.getInstance().getReference().child("users").child(task.result?.user!!.uid).child("email").setValue(emailEditText?.text.toString())
                            logIn()
                        }else{
                            Toast.makeText(this,"login failed. Try again",Toast.LENGTH_SHORT).show()

                        }
                    }
                }

            }
    }

    fun logIn(){
        //next activity
        val intent=Intent(this, snapsActivity::class.java)
        startActivity(intent)
    }
}
