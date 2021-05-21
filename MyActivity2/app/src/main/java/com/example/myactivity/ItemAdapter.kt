package com.example.myactivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_row.view.*

class ItemAdapter(val context: Context, val items: ArrayList<EmpModelClass>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llMain      = view.llMain
        val txtKegiatan = view.txtKegiatan
        val txtTanggal  = view.txtTanggal
        val txtWaktu    = view.txtWaktu
        val ivDetails   = view.ivDetails
        val ivEdit      = view.ivEdit
        val ivDelete    = view.ivDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_row, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)

        holder.txtKegiatan.text     = item.kegiatan
        holder.txtTanggal.text    = item.tanggal
        holder.txtWaktu.text  = item.waktu

        if(position % 2 ==  0){
            holder.llMain.setBackgroundColor(ContextCompat.getColor(context,R.color.lightGrey))
        }else{
            holder.llMain.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }

        holder.ivDelete.setOnClickListener{ view ->
            if(context is MainActivity){
                context.deleteRecordAlertDialog(item)
            }
        }

        holder.ivEdit.setOnClickListener { view ->
            if(context is MainActivity){
                context.updateRecordDialog(item)
            }
        }
        holder.ivDetails.setOnClickListener { view ->
            if(context is MainActivity){
                context.detailsRecordDialog(item)
            }
        }
    }
}