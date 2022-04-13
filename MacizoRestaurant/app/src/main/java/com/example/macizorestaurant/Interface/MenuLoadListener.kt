package com.example.macizorestaurant.Interface

import com.example.macizorestaurant.model.MenuModel

interface MenuLoadListener {
 fun onMenuLoadSuccess(menuModelList:List<MenuModel>?)
 fun onMenuLoadFailed(message: String?)

}