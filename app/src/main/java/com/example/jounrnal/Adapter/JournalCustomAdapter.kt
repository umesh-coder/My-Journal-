package com.example.jounrnal.Adapter


import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater

import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.jounrnal.JournalModel
import java.util.*;


import com.example.jounrnal.databinding.JournalRowBinding
import com.google.firebase.firestore.FirebaseFirestore

class JournalCustomAdapter (val context: Context, private val journalList: MutableList<JournalModel>) : RecyclerView.Adapter<JournalCustomAdapter.JournalViewModel>(){

    lateinit var binding: JournalRowBinding
    private val firestore = FirebaseFirestore.getInstance()


    class JournalViewModel(var binding: JournalRowBinding)
        :RecyclerView.ViewHolder(binding.root) {
        fun bind (journal: JournalModel){
            binding.journal = journal

        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewModel {

        binding = JournalRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return JournalViewModel(binding)
    }

    override fun onBindViewHolder(holder: JournalViewModel, position: Int) {
        val journal = journalList[position]

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(FitCenter(), RoundedCorners(16))

        Glide.with(context)
            .load(journal.imageUrl)
            .apply(requestOptions)
            .skipMemoryCache(true)
            .into(holder.binding.journalImageList)

        holder.binding.journalRowShareButton.setOnClickListener(){

            shareJournal(journal)
        }

        holder.binding.journalRowDeleteButton.setOnClickListener(){

            deleteJournal(journal, position)

        }

        holder.bind(journal)
    }

    private fun shareJournal(journal: JournalModel) {
        val i : Intent = Intent(Intent.ACTION_SEND)

        i.setType("text/plain")

        i.putExtra(Intent.EXTRA_SUBJECT,"Sharing Journal")
        i.putExtra(Intent.EXTRA_TEXT,"Hey Check out this journal \n Title : "+journal.title+"\nThoughts:"+journal.thoughts )
        Log.d("Share data",""+journal.title)



        context.startActivity(Intent.createChooser(i,"Choose Platform"))
    }


    private fun deleteJournal(journal: JournalModel, position: Int) {
        firestore.collection("Journal").document(journal.title)
            .delete()
            .addOnSuccessListener {
                journalList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, journalList.size)
                Toast.makeText(context, "Journal deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("Delete data", "Error deleting document", e)
                Toast.makeText(context, "Error deleting journal", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount(): Int = journalList.size



}


