package com.example.coffer2025.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.coffer2025.databinding.FragmentHomeBinding
import com.example.coffer2025.ui.coroutine.NetFetcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val netFetcher = NetFetcher()
    private lateinit var textView: TextView
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        textView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        textView.setOnClickListener {
            homeViewModel.sendData()
        }
        binding.btn1.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                textView.text = netFetcher.fetcherDate1()
            }
        }

        binding.btn2.setOnClickListener {
            // 并行计算
            parallel()
        }

        binding.btn3.setOnClickListener {
            // 串行计算
            serial()
        }

        binding.btn4.setOnClickListener {
            customScope()
        }
        binding.btn5.setOnClickListener {
//            useDispatchersV1()
            useDispatchersV2()
        }
        return root
    }

    /**
     * 协程并行计算
     */
    @SuppressLint("SetTextI18n")
    private fun parallel() {
        lifecycleScope.launch {
            val t1 = System.currentTimeMillis()
            // async 会立即启动协程，不会阻塞
            val deferred1 = async { netFetcher.getUserInfo() }
            val deferred2 = async { netFetcher.getOrders() }
            println("Waiting...")

            val result1 = deferred1.await()
            val result2 = deferred2.await()
            textView.text =
                "Done: $result1 + $result2 + 耗时 ： ${System.currentTimeMillis() - t1}"
        }
    }

    /**
     * 串行计算
     */
    @SuppressLint("SetTextI18n")
    private fun serial() {
        lifecycleScope.launch {
            val t1 = System.currentTimeMillis()
            val str1 = netFetcher.getUserInfo()
            val str2 = netFetcher.getOrders()
            textView.text =
                "Done: $str1 + $str2 + 耗时 ： ${System.currentTimeMillis() - t1}"
        }
    }

    private fun customScope() {
        runBlocking {
            val job = launch {
                delay(1000)
                println("hahahah")
            }

            launch {
                delay(500)
                println("lalalla")
                job.cancel()
            }
            println("done")
        }
    }

    private fun useDispatchersV1() {
        // 这里的写法是lifecycleScope 默认运行在Default 上，然后调用loadDataV2，让协程运行在IO 上，
        // 最后把结果运行Default 上。没有真正利用 Dispatchers.Default 做数据处理逻辑
        lifecycleScope.launch(Dispatchers.Default) {
            println(loadDataV2())
        }
    }

    private fun useDispatchersV2() {
        // 两个协程切换练习
        lifecycleScope.launch {
           val time = measureTimeMillis {
                // 第 1 阶段：在 IO Dispatcher 中读取文件
                val res1 = withContext(Dispatchers.IO) {
                    delay(1000)
                    "hahah"
                }
                // 第 2 阶段：在 Default Dispatcher 中处理内容
                val res2 = withContext(Dispatchers.Default) {
                    res1.uppercase()
                }
                println("最终数据: $res2")
            }
            println("耗时 ： $time")
        }
    }

    private suspend fun loadDataV2(): String {
        return withContext(Dispatchers.IO) {
            delay(1000)
            "jajaj"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}