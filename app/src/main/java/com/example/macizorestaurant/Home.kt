package com.example.macizorestaurant

import android.content.Intent
import android.media.metrics.Event
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.GridLayoutManager
import com.example.macizorestaurant.Decoration.SpaceItemDecoration
import com.example.macizorestaurant.Interface.CartloadListener
import com.example.macizorestaurant.Interface.MenuLoadListener
import com.example.macizorestaurant.adapter.MyMenuAdapter
import com.example.macizorestaurant.databinding.ActivityHomeBinding
import com.example.macizorestaurant.eventbus.UpdateCartEvent
import com.example.macizorestaurant.model.CartModel
import com.example.macizorestaurant.model.MenuModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class Home : AppCompatActivity(), MenuLoadListener, CartloadListener {
    lateinit var menuLoadListener: MenuLoadListener
    lateinit var cartLoadListener: CartloadListener
    private  lateinit var  binding: ActivityHomeBinding
    private lateinit var  actionBar: ActionBar
    private  lateinit var firebaseAuth: FirebaseAuth

    override fun onStart(){
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop(){
        super.onStop()
        if(EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode =  ThreadMode.MAIN, sticky = true)
     fun onUpdateCartEvent(event: UpdateCartEvent){
        countCartFromFirebase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        chekuser()

        binding.btnlogout.setOnClickListener {
            firebaseAuth.signOut()
            chekuser()


        }
        init()
        LoadMenuFromfirebase()
        countCartFromFirebase()
    }

    private fun countCartFromFirebase(){
        val cartModels: MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(cartSnapshot in snapshot.children){
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener.onLoadCartSucces(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener.onLoadCartFailed(error.message)
                }
            })
    }

    //LOGIN
    private fun chekuser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser!= null) {
            val email = firebaseUser.email
            binding.txtemail.text = email

        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
    //DATBASE REAL TIME
    private fun LoadMenuFromfirebase() {

        val menuModels: MutableList<MenuModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Menu")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                   if (snapshot.exists()){

                       for (menusnapshot in snapshot.children){
                           val menuModel = menusnapshot.getValue(MenuModel::class.java)
                           menuModel!!.key = menusnapshot.key
                           menuModels.add(menuModel)
                       }
                       menuLoadListener.onMenuLoadSuccess(menuModels)
                   }else{
                       menuLoadListener.onMenuLoadFailed("Product no exist")
                   }
                }

                override fun onCancelled(error: DatabaseError) {
                    menuLoadListener.onMenuLoadFailed(error.message)
                }

            })
    }

    private  fun init(){
        menuLoadListener = this
        cartLoadListener = this

        val gridLayoutManager = GridLayoutManager(this, 2)
        recycler_menu.layoutManager = gridLayoutManager
        recycler_menu.addItemDecoration(SpaceItemDecoration())

        btnshop.setOnClickListener { startActivity(Intent(this, CartActivity::class.java)) }
    }



    //METODS DATA BASE REAL TIME

    override fun onMenuLoadSuccess(menuModelList: List<MenuModel>?) {
    val adapter = MyMenuAdapter(this, menuModelList!!, cartLoadListener)
        recycler_menu.adapter = adapter
    }

    override fun onMenuLoadFailed(message: String?) {

      Toast.makeText(getApplicationContext(), "Item added to cart", Toast.LENGTH_SHORT).show();


    }

    override fun onLoadCartSucces(cartModelist: List<CartModel>) {
        var cartSum = 0
        for(cartModel in cartModelist!!) cartSum += cartModel!!.quantity
        binding.ntbadge!!.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {
        Toast.makeText(getApplicationContext(), "Item added to cart", Toast.LENGTH_SHORT).show();
    }


}