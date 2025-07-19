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
### 关于协程调度器Dispatcher
* 1、Dispatchers.Main ，用途是：UI线程，仅在 Android 有效。
* 2、Dispatchers.IO ，用途是：IO密集型操作，如网络/数据库。
* 3、Dispatchers.Default ，用途是：CPU密集型任务（如计算）。
* 4、Dispatchers.Unconfined ，不指定线程，测试时用。
### 协程的异常传播机制（Coroutine Exception Propagation）
**总体原则：** 协程的异常传播，取决于它的作用域（CoroutineScope）以及 Job 的类型。
✅ 常规规则总结：
* 1、launch 启动的协程：异常会自动向上传播，并终止作用域（scope），不会抛到调用方。
* 2、async 启动的协程：异常会在 await() 被调用时才抛出。
* 3、withContext 中的异常：会自动传播到外层协程，并导致其失败。
* 4、launch 中的异常立即传播。
* 5、async 中异常延迟到 await()。
### CoroutineExceptionHandler
这是协程体系中专门处理 launch 类型协程未捕获异常的机制。
**注意：**
* 1、CoroutineExceptionHandler 只能处理 launch 中未捕获的异常。
* 2、对于 async 异常，你仍需通过 try-catch 或 deferred.await() 处理。
### 关于Job vs SupervisorJob 的区别
#### 1、Job (默认)
在子协程异常传播上：**一个失败，全部取消**。
在用途上：只是普通的协程结构
#### 2、SupervisorJob
在子协程异常传播上：**一个失败，不会影响其他子协程**。
在用途上：希望子任务“互不干扰”的管理型任务。

**注意：**
1、SupervisorJob ≠ 异常自动处理。SupervisorJob 的核心是：一个子协程失败时，不会取消兄弟协程，
但：它不会自动帮你“吞掉异常”，你仍需处理异常，否则作用域仍有可能被终止。
2、CoroutineExceptionHandler 只处理 顶级 launch 抛出的 uncaught 异常。
它不会自动传给内部 launch 的协程，这和 try-catch 的语义类似，得“谁抛异常谁处理，或者传给自己的调用者”。
3、launch {} 是异步启动的协程块，异常是在线程里延后抛出的，不是同步函数执行中的异常，所以 try-catch 在这里根本没用。try-catch要放在launch {}里面才行。
**实战建议：**
* 1、如果你希望“所有子任务成败一致”，用默认 Job。
* 2、如果你希望“一个失败不会影响其他子任务”，用 SupervisorJob。
* 3、对 launch 添加 CoroutineExceptionHandler。
* 4、对 async 用 try-catch 或 .await() 处理异常。








