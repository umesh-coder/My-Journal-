package com.example.jounrnal.Adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jounrnal.JournalModel


import com.example.jounrnal.databinding.JournalRowBinding

class JournalCustomAdapter (val context: Context, private val journalList: List<JournalModel>) : RecyclerView.Adapter<JournalCustomAdapter.JournalViewModel>(){

    lateinit var binding: JournalRowBinding


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
        holder.bind(journal)
    }

    override fun getItemCount(): Int = journalList.size



}


