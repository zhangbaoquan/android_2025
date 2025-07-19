package com.example.coffer2025

import com.example.coffer2025.ui.coroutine.NetFetcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * author       : coffer
 * date         : 2025/7/19
 * description  :
 */

class UserRepository {
    val handler = CoroutineExceptionHandler{ _,exception ->
        println("捕获到异常：$exception")
    }
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + handler)

    fun loadUserData() {
        scope.launch {
            val user = NetFetcher().getUserInfo()
            val orders = NetFetcher().getOrders()
            println("User: $user, Orders: $orders")
        }



        fun cancelAll() {
            scope.cancel()
        }
    }
}