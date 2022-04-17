package com.example.macizorestaurant.Interface

import com.example.macizorestaurant.model.CartModel

interface CartloadListener   {
        fun onLoadCartSucces(cartModelist:List<CartModel>)
        fun onLoadCartFailed(message:String?)

}