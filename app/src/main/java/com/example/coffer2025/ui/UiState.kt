package com.example.coffer2025.ui

/**
 * author       : coffer
 * date         : 2025/7/20
 * description  : 定义 UI 状态类
 */

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<String>) : UiState()
    data class Error(val message : String) : UiState()
}