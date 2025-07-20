package com.example.coffer2025.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffer2025.http.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val tag = "HomeViewModel_tag"

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun sendData(){
        viewModelScope.launch (Dispatchers.IO){
            delay(5000)
            Log.i(tag,"发协程消息啦,当前的线程是 ： ${Thread.currentThread().name}")
        }
        Log.i(tag,"sendData，当前的线程是 ： ${Thread.currentThread().name}")
    }

    fun getData1(){
        Log.i("hahah","getData1")
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO){
                    RetrofitHelper.apiService.getInfo()
                }
                Log.i(tag,"result : $result")
                _text.value = result.toString()
            }catch (e:Exception){
                Log.e(tag,e.toString())
            }
        }
    }
}