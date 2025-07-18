package com.example.coffer2025.ui.coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * author       : coffer
 * date         : 2025/7/18
 * description  :
 */

class NetFetcher {

    suspend fun fetcherDate1() : String{
       return withContext(Dispatchers.IO){
            delay(1000)
           "协程回来啦"
        }
    }
}