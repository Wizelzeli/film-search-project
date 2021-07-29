package com.example.filmsearchproject


import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsearchproject.databinding.RecyclerViewElementBinding
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList


class RVAdapter: RecyclerView.Adapter<RVAdapter.RVHolder>() {


    var listOfElements = ArrayList<RVElement>()
    var oldListOfElements = ArrayList<RVElement>()

    fun addElement(element: RVElement) {
        listOfElements.add(element)
        oldListOfElements.add(element)
        notifyDataSetChanged()
    }

    fun filterRV(filteredRV: ArrayList<RVElement>) {
        listOfElements = filteredRV
        notifyDataSetChanged()
    }

    class RVHolder(item: View): RecyclerView.ViewHolder(item) {
        private val binding = RecyclerViewElementBinding.bind(item)
        fun bind(element: RVElement) {
            val uriImg: Uri = Uri.parse("https://image.tmdb.org/t/p/w500" + element.image)
            Picasso.get().load(uriImg).error(R.mipmap.ic_launcher).into(binding.imageViewCover)
            binding.textViewTitle.text = element.title
            binding.textViewDescription.text = element.description
            binding.textViewDate.text = element.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_view_element,
            parent,
            false
        )
        return RVHolder(view)
    }

    override fun onBindViewHolder(holder: RVHolder, position: Int) {
        holder.bind(listOfElements[position])
    }

    override fun getItemCount(): Int {
        return listOfElements.size
    }
}