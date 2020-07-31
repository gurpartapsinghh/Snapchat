package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class chooseUserActivity : AppCompatActivity() {

    var chooseUsersListView: ListView? = null
    var emails = ArrayList<String>()
    var keys = ArrayList<String>()    //auto generated keys for each user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        chooseUsersListView = findViewById(R.id.chooseusersListView)
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, emails)
        chooseUsersListView?.adapter = arrayAdapter

        FirebaseDatabase.getInstance().getReference().child("users")
            .addChildEventListener(object : ChildEventListener {

                override fun onChildAdded(
                    p0: DataSnapshot,
                    p1: String?
                ) {   //p0 has given us the while id and .child("emails") give us the email of that user
                    val email = p0.child("email").value as String
                    emails.add(email)
                    keys.add(p0.key.toString())
                    arrayAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(p0: DatabaseError) {}
                override fun onChildMoved(p0: DataSnapshot, p1: String?) {}    //all these are not of any use to us only the onchildadded is
                override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
                override fun onChildRemoved(p0: DataSnapshot) {}


            })

        chooseUsersListView?.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, position, id ->
                //eg gp send a snap to nick then all we wan to do is that add a new snap to the field "snaps(check all fields of the snap)" of nick in database

                val snapMap: Map<String,String> = mapOf("from" to FirebaseAuth.getInstance().currentUser!!.email!!,"imagename" to intent.getStringExtra("imageName"), "imageURL" to intent.getStringExtra("imageUrl"), "message" to intent.getStringExtra("message"))
                FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(position)).child("snaps").push().setValue(snapMap)

                val intent = Intent(this,snapsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)    // so that when user presses back button it shouldn't go back to select user screen..it'll be a bad user experience
                startActivity(intent)
            }
    }
}
