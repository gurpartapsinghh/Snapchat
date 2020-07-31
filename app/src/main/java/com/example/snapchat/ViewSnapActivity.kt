package com.example.snapchat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.net.HttpURLConnection
import java.net.URL

class ViewSnapActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    var msgTextView: TextView? = null
    var receivedImageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)

        msgTextView = findViewById(R.id.msgTextView)
        receivedImageView = findViewById(R.id.recievedImageView)

        msgTextView?.text = intent.getStringExtra("message")

        val downloader = imageDownloader()
        val myimage: Bitmap

        try {
            myimage = downloader.execute(intent.getStringExtra("imageUrl")).get()
            receivedImageView?.setImageBitmap(myimage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    inner class imageDownloader : AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg strings: String): Bitmap? {
            try {
                val url = URL(strings[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val `in` = connection.inputStream
                return BitmapFactory.decodeStream(`in`)

            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //we want to delete "snaps" from database and that particular image from storage and of course also update the listView

        FirebaseDatabase.getInstance().getReference().child("users").child(auth.currentUser!!.uid).child("snaps").child(intent.getStringExtra("snapKey")).removeValue()   //"snaps" deleted from data base for the particular user

        FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("imageName")).delete()



    }
}
