package com.example.macizorestaurant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.macizorestaurant.Interface.CartloadListener
import com.example.macizorestaurant.Interface.RecyclerListener
import com.example.macizorestaurant.R
import com.example.macizorestaurant.eventbus.UpdateCartEvent
import com.example.macizorestaurant.model.CartModel
import com.example.macizorestaurant.model.MenuModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

class MyMenuAdapter( private  val context: Context, private val list: List<MenuModel>, private val cartListener:CartloadListener):
    RecyclerView.Adapter<MyMenuAdapter.MyMenuViewHolder>() {

    class MyMenuViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
       var imageview:ImageView?=null
      var  txtname: TextView?=null
       var txtprice: TextView?=null

        private var clickListener: RecyclerListener? = null

        fun setClickListener(clickListener: RecyclerListener){
            this.clickListener = clickListener;
        }

      init {
          imageview = itemView.findViewById(R.id.imageview) as ImageView;
          txtname = itemView.findViewById(R.id.txtname) as TextView;
          txtprice = itemView.findViewById(R.id.txtprice) as TextView;

          itemView.setOnClickListener(this)
      }

        override fun onClick(v: View?) {
            clickListener!!.onItemClickListener(v, adapterPosition)
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

        holder.setClickListener(object :RecyclerListener{
            override fun onItemClickListener(view: View?, position: Int) {
                addtoCart(list[position])
            }

        })
    }

    private fun addtoCart(MenuModel: MenuModel) {
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
        userCart.child(MenuModel.key!!)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        val updateData: MutableMap<String, Any> = HashMap()
                        cartModel!!.quantity = cartModel!!.quantity+1;
                        updateData["quantity"] = cartModel!!.quantity
                        updateData["TotalPrice"] = cartModel!!.quantity * cartModel.price!!.toFloat()
                        userCart.child(MenuModel.key!!)
                            .updateChildren(updateData)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("Succes add to cart") }
                            .addOnFailureListener{e-> cartListener.onLoadCartFailed(e.message)}
                    }
                    else{
                        val cartModel = CartModel()
                        cartModel.key = MenuModel.key
                        cartModel.name = MenuModel.name
                        cartModel.image = MenuModel.image
                        cartModel.price = MenuModel.price
                        cartModel.quantity = 1
                        cartModel.totalPrice = MenuModel.price!!.toFloat()

                        userCart.child(MenuModel.key!!)
                            .setValue(cartModel)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("Succes add to cart") }
                            .addOnFailureListener{ e-> cartListener.onLoadCartFailed(e.message)}

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    cartListener.onLoadCartFailed(error.message)
                }
            })
    }

    override fun getItemCount(): Int {
        return list.size;
    }
}