package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.io.StringReader

class snapsActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    var snapsListView :ListView ?=null
    var emails =ArrayList<String>()
    var snaps= ArrayList<DataSnapshot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)

        snapsListView=findViewById(R.id.snapsListView)
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, emails)
        snapsListView?.adapter = arrayAdapter

        FirebaseDatabase.getInstance().getReference().child("users").child(auth.currentUser!!.uid).child("snaps").addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(
                p0: DataSnapshot,
                p1: String?
            ) {   //p0 has given us the object and p0.child("from") give us the email of the user jitho snap aai aa
                emails.add(p0.child("from").value as String)
                snaps.add(p0)
                arrayAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(
                p0: DataSnapshot,
                p1: String?
            ) {
            }    //all these are not of any use to us only the onchildadded is

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {
                var index=0
                for (snap: DataSnapshot in snaps) {
                    if (snap.key == p0.key){
                        snaps.removeAt(index)
                        emails.removeAt(index)

                    }
                    index++
                }
                arrayAdapter.notifyDataSetChanged()
            }


        })

        snapsListView?.onItemClickListener=AdapterView.OnItemClickListener { adapterView, view, position, id ->
            val snapshot=snaps.get(position)
            val intent =Intent(this, ViewSnapActivity::class.java)
            intent.putExtra("imageName",snapshot.child("imagename").value as String)
            intent.putExtra("imageUrl",snapshot.child("imageURL").value as String)
            intent.putExtra("message",snapshot.child("message").value as String)
            intent.putExtra("snapKey",snapshot.key)


            startActivity(intent)
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater=menuInflater
        inflater.inflate(R.menu.snaps,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item?.itemId==R.id.snap){
            val intent=Intent(this,createSnapActivity::class.java)
            startActivity(intent)

        }else if(item?.itemId==R.id.logout){
            auth.signOut()
            finish()   //finish the activity
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        auth.signOut()
    }
}
