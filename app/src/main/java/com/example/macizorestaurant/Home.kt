package com.example.macizorestaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.GridLayoutManager
import com.example.macizorestaurant.Decoration.SpaceItemDecoration
import com.example.macizorestaurant.Interface.MenuLoadListener
import com.example.macizorestaurant.adapter.MyMenuAdapter
import com.example.macizorestaurant.databinding.ActivityHomeBinding
import com.example.macizorestaurant.model.MenuModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_home.*

class Home : AppCompatActivity(), MenuLoadListener {
    lateinit var menuLoadListener: MenuLoadListener
    private  lateinit var  binding: ActivityHomeBinding
    private lateinit var  actionBar: ActionBar
    private  lateinit var firebaseAuth: FirebaseAuth
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
        Loadfirebase()
    }
//DATBASE REAL TIME
    private fun Loadfirebase() {

        val MenuModels: MutableList<MenuModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Menu")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                   if (snapshot.exists()){

                       for (menusnapshot in snapshot.children){
                           val menuModel = menusnapshot.getValue(MenuModel::class.java)
                           menuModel!!.key = menusnapshot.key
                           MenuModels.add(menuModel)
                       }
                       menuLoadListener.onMenuLoadSuccess(MenuModels)
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

        val gridLayoutManager = GridLayoutManager(this, 2)
        recycler.layoutManager = gridLayoutManager
        recycler.addItemDecoration(SpaceItemDecoration())
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

    //METODS DATA BASE REAL TIME

    override fun onMenuLoadSuccess(menuModelList: List<MenuModel>?) {
val adapter = MyMenuAdapter(this, menuModelList!!)
        recycler.adapter = adapter
    }

    override fun onMenuLoadFailed(message: String?) {

      Toast.makeText(getApplicationContext(), "Toast por defecto", Toast.LENGTH_SHORT).show();


    }


}