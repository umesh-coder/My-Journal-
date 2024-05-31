package com.example.jounrnal

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jounrnal.Adapter.JournalCustomAdapter

import com.example.jounrnal.databinding.ActivityJournalListBinding
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference


class JournalList : AppCompatActivity() {
    lateinit var binding: ActivityJournalListBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    var db = FirebaseFirestore.getInstance()

    lateinit var storageReference: StorageReference
    var collectionReference = db.collection("Journal")

    lateinit var journalList: MutableList<JournalModel>
    lateinit var journalAdapter: JournalCustomAdapter

    lateinit var noPostTextView : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_journal_list)

        //firebase auth
        firebaseAuth = Firebase.auth
        firebaseUser = firebaseAuth.currentUser!!

        //recyclerview
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        //posts array list
        journalList = arrayListOf<JournalModel>()


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add -> if(firebaseUser != null && firebaseAuth!=null){
                startActivity(Intent(this, AddJournalActivity::class.java))
            }

            R.id.action_signout ->{
               if(firebaseUser != null && firebaseAuth!=null){

                   firebaseAuth.signOut()
                   startActivity(Intent(this, JournalList::class.java))
               }
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        collectionReference.whereEqualTo("userId",firebaseUser.uid)
            .get()
            .addOnSuccessListener {

                if (!it.isEmpty) {

//                    it.forEach{
//                    //convert snapshots to journal
//                        var journal = it.toObject(JournalModel::class.java)
//                        journalList.add(journal)
//
//                    }

                    for(document in it){
                        var journal =JournalModel(
                            document.data.get("title").toString(),
                            document.data.get("thoughts").toString(),
                            document.data.get("imageUrl").toString(),
                            document.data.get("userId").toString(),
                            document.data.get("username").toString(),
                            document.data.get("timeAdded") as Timestamp,
                        )

                        journalList.add(journal)
                    }

                    //setup adapter
                    journalAdapter = JournalCustomAdapter(this, journalList)
                    binding.recyclerView.setAdapter(journalAdapter)
                    journalAdapter.notifyDataSetChanged()
                }else{

                    binding.listNoPosts.visibility = View.VISIBLE

                }

            }.addOnFailureListener{

                Toast.makeText(this,"Oops! something went wrong",Toast.LENGTH_SHORT).show()
            }

    }

}