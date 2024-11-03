package com.example.musicplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MyAdapter(
    private val context: Context,
    private val dataList: List<Data>,
    private val onItemClick: (Data) -> Unit
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.each_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentData = dataList[position]

        holder.title.text = currentData.title ?: "Unknown Title"
        holder.artist.text = currentData.artist.name ?: "Unknown Artist"

        Picasso.get()
            .load(currentData.album.cover)
            .error(R.drawable.stor) // Placeholder image for error handling
            .into(holder.image)

        holder.itemView.setOnClickListener { onItemClick(currentData) }
    }

    override fun getItemCount(): Int = dataList.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.musicImage)
        val title: TextView = itemView.findViewById(R.id.musicTitle)
        val artist: TextView = itemView.findViewById(R.id.artistName)
    }
}
