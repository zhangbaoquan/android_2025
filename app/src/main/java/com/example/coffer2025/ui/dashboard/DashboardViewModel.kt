package com.example.coffer2025.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffer2025.http.RetrofitHelper
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    // 创建StateFlow 管理 UI 状态，并设置初始值
    private val _uiState = MutableStateFlow("")
    val uiState: StateFlow<String> = _uiState

    // SharedFlow 实战应用：单次事件通知
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    private fun updateText(newValue: String){
        _uiState.value = newValue
    }

    fun getData(){
        viewModelScope.launch {
            updateText("加载中...")
            val task = async {
                delay(2000)
                RetrofitHelper.apiService.getBannerData()
            }
            task.await()
            updateText("加载完成")
            _toastEvent.emit("请求完成")
        }
    }
}