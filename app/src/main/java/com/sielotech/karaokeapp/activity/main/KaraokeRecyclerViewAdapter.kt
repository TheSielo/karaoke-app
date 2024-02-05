package com.sielotech.karaokeapp.activity.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sielotech.karaokeapp.R

internal class KaraokeRecyclerViewAdapter internal constructor(private var lyrics: Lyrics,
                                                               private val onClickListener: View.OnClickListener) :
    RecyclerView.Adapter<KaraokeRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val rootView: View = itemView.findViewById(R.id.root_view)
        internal val japLine: TextView = itemView.findViewById(R.id.jap_line)
        internal val transLine: TextView = itemView.findViewById(R.id.trans_line)
    }

    override fun getItemCount(): Int {
        return lyrics.japaneseLines.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.karaoke_recycler_view_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.japLine.text = lyrics.japaneseLines[position]
        holder.transLine.text = lyrics.translatedLines[position]
        holder.rootView.tag = position
        holder.rootView.setOnClickListener(onClickListener)
    }
}

