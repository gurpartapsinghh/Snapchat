package com.example.snapchat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*
import com.google.android.gms.tasks.OnSuccessListener
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class createSnapActivity : AppCompatActivity() {

    var createsnapImageView:ImageView ?=null
    var messageEditText: EditText?=null
    val imageName=UUID.randomUUID().toString()+".jpg"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        createsnapImageView=findViewById(R.id.imageView)
        messageEditText=findViewById(R.id.messageEditText)

    }

    fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1)
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {    //result of the intent started above...to check if we have the image
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data!!.data
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                createsnapImageView?.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    fun nextClicked(view: View){

        //first conerting image to a byte array
        createsnapImageView?.isDrawingCacheEnabled = true
        createsnapImageView?.buildDrawingCache()
        val bitmap = (createsnapImageView?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()


        var uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data)      //FirebaseStorage.getInstance().getReference() gets us to the storage page of our firebase and .child("images") create a folder named images and .child(imageName) create an image in the image folder with a image name of imageName

        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this,"Upload failed",Toast.LENGTH_SHORT).show()
        }
        uploadTask.addOnSuccessListener { taskSnapshot ->
            if (taskSnapshot.metadata != null) {
                if (taskSnapshot.metadata!!.reference != null) {
                    val result = taskSnapshot.storage.downloadUrl
                    result.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()   // we have got the url to our image
                        Log.i("Url: ",imageUrl)
                        val intent=Intent(this,chooseUserActivity::class.java)
                        intent.putExtra("imageUrl",imageUrl)
                        intent.putExtra("imageName",imageName)
                        intent.putExtra("message",messageEditText?.text.toString())
                        startActivity(intent)
                    }
                }
            }
        }
    }
    fun createsnap(view: View)
    {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            getPhoto()
        }

    }
}
