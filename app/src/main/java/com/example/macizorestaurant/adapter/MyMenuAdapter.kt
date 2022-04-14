package com.example.macizorestaurant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.macizorestaurant.R
import com.example.macizorestaurant.model.MenuModel
import java.lang.StringBuilder

class MyMenuAdapter( private  val context: Context, private val list: List<MenuModel>):
    RecyclerView.Adapter<MyMenuAdapter.MyMenuViewHolder>() {

    class MyMenuViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
       var imageview:ImageView?=null
      var  txtname: TextView?=null
       var txtprice: TextView?=null

      init {
          imageview = itemView.findViewById(R.id.imageview) as ImageView;
          txtname = itemView.findViewById(R.id.txtname) as TextView;
          txtprice = itemView.findViewById(R.id.txtprice) as TextView;
      }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMenuViewHolder {
        return MyMenuViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.activity_container_products, parent, false))
    }

    override fun onBindViewHolder(holder: MyMenuViewHolder, position: Int) {
       Glide.with(context)
           .load(list[position].image)
           .into(holder.imageview!!)
        holder.txtname!!.text = StringBuilder().append(list[position].name)
        holder.txtprice!!.text = StringBuilder("$ ").append(list[position].price)
    }

    override fun getItemCount(): Int {
        return list.size;
    }
}