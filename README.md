# android_2025 
# 一、协程知识梳理（第一讲）
## 基本知识
* 1、线程 ：操作系统分配资源的基本单元，占用大内存，切换开销大。
* 2、协程 ：用户级轻量线程，调度切换非常快，可以成千上万个。
* 3、launch：启动一个协程，不返回结果。
* 4、async ：启动并返回 Deferred 结果，可用 await() 获取。
* 5、runBlocking ：阻塞当前线程，常用于测试或 main 函数。
* 6、delay(ms) ：挂起当前协程，非阻塞。
* 7、withContext ：切换到指定 Dispatcher，常用于 IO 操作。
* 8、Dispatchers.IO ：用于数据库、文件、网络。
* 9、Dispatchers.Default ：CPU 密集型工作。
* 10、Dispatchers.Main ：UI 更新（Android 中）。
* 11、launch ：无返回值，不挂起，主要用于UI 触发任务。
* 12、async ： 返回 Deferred<T>，用 await() 获取，不挂起，主要用于并发计算、并发网络请求。
## 关于挂起函数的理解
* 1、挂起函数是 Kotlin 协程中的一种可以被挂起并恢复执行的函数，由关键字 suspend 声明。
* 2、可以理解支持“暂停 → 等待异步结果 → 恢复执行”的函数。
* 3、不会阻塞当前线程。
* 4、必须在协程中或另一个挂起函数中调用。
* 5、挂起函数让你可以用同步的写法，实现异步的效果。
### 简单理解
suspend 关键字的作用是允许该函数“挂起自己”，并让出线程资源给其他协程执行。
### 深入理解
* 编译器会把 suspend fun 转换成带有 状态机（State Machine） 的函数；
* 每次挂起点（如 delay()、withContext()），协程会保存当前上下文，等异步操作完成后再从断点继续执行；
* 协程不会阻塞线程，而是挂起“协程”本身，达到高并发而低资源占用的目的。
* **注意 ：** suspend 不能单独调用，必须在：协程作用域中（如 launch、async）或另一个 suspend 函数中调用。


# 二、协程知识梳理（第二讲）
## 理解 CoroutineScope 与结构化并发
### 什么是 CoroutineScope？
协程必须运行在某个作用域内，协程的“生命周期”和作用域绑定。
* 1、GlobalScope ：生命周期是永久存在（不推荐使用） 。使用场景是非生命周期感知的后台任务。
* 2、CoroutineScope()；生命周期是手动管理 Job 生命周期，使用场景是自定义组件或工具类。
* 3、viewModelScope ；生命周期是随 ViewModel 自动销毁， 使用场景是Android 推荐方式。
* 4、lifecycleScope；生命周期上与 Fragment/Activity 生命周期绑定 ，使用场景是自动管理 UI 协程。
**注意**：父协程（runBlocking）会等待所有子协程完成后再退出。