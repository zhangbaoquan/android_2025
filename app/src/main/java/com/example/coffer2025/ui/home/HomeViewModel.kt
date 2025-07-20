package com.example.coffer2025.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffer2025.http.RetrofitHelper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val tag = "HomeViewModel_tag"

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val handler = CoroutineExceptionHandler{_,exception ->
        Log.e(tag, "捕获到异常 ： $exception")
    }

    fun sendData() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(5000)
            Log.i(tag, "发协程消息啦,当前的线程是 ： ${Thread.currentThread().name}")
        }
        Log.i(tag, "sendData，当前的线程是 ： ${Thread.currentThread().name}")
    }

    fun getData1() {
        Log.i("hahah", "getData1")
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    RetrofitHelper.apiService.getInfo()
                }
                Log.i(tag, "result : $result")
                _text.value = result.toString()
            } catch (e: Exception) {
                Log.e(tag, e.toString())
            }
        }
    }

    /**
     * 并发请求两个接口，最后合并两个接口数据
     * async + await
     * 用于启动多个协程任务，并等待其结果，适合并发请求多个接口
     *
     */
    fun getData2() {
        viewModelScope.launch(handler) {
            try {
                val bannerDeferred = async(Dispatchers.IO) {
                    RetrofitHelper.apiService.getBannerData()
                }

                val infoDeferred = async(Dispatchers.IO) {
                    RetrofitHelper.apiService.getInfo()
                }
                // 并发执行，等待两个任务都完成
                val banner = bannerDeferred.await()
                val info = infoDeferred.await()
                Log.i(tag, "合并结果 ；\n banner : $banner ，\n info : $info")
            } catch (e: Exception) {
                Log.e(tag, e.toString())
            }
        }
    }

    /**
     * 使用coroutineScope 实现结构化并发。
     * 确保所有子协程都完成才继续向下执行,如果其中一个 async 抛异常，
     * 整个 coroutineScope 会取消，其他协程也会被取消。适合多个请求之间有 “全成或全败” 的业务逻辑
     */
    fun getData3() {
        viewModelScope.launch {
            try {
                coroutineScope {
                    val bannerDeferred = async {
                        RetrofitHelper.apiService.getBannerData()
                    }
                    val infoDeferred = async {
                        RetrofitHelper.apiService.getInfo()
                    }
                    // 并发执行，等待两个任务都完成
                    val banner = bannerDeferred.await()
                    val info = infoDeferred.await()
                    Log.i(tag, "使用viewModelScope 实现结构化并发，合并结果 ；\n banner : $banner ，\n info : $info")
                }
            } catch (e: Exception) {
                Log.e(tag, e.toString())
            }
        }
    }

    /**
     * 并发请求两个接口，最后合并两个接口数据
     * async + await
     * 用于启动多个协程任务，并等待其结果，适合并发请求多个接口
     * 下面为每个async 任务里加了try catch 是保证即使一个协程出现问题，也不能影响其他协程
     */
    fun getData4() {
        viewModelScope.launch {
            supervisorScope {
                val bannerDeferred = async(Dispatchers.IO) {
                    try {
                        RetrofitHelper.apiService.getBannerData()
//                        throw RuntimeException("测试异常")
                    } catch (e: Exception) {
                        Log.e(tag, e.toString())
                    }
                }

                val infoDeferred = async(Dispatchers.IO) {
                    try {
                        RetrofitHelper.apiService.getInfo()
                    } catch (e: Exception) {
                        Log.e(tag, e.toString())
                    }
                }
                // 并发执行，等待两个任务都完成
                val banner = bannerDeferred.await()
                val info = infoDeferred.await()
                Log.i(tag, "合并结果 ；\n banner : $banner ，\n info : $info")
            }
        }

    }
}