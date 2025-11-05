package com.app.mytasks.util
import kotlinx.coroutines.*

/**
 * Test
 *
 * @author stephingeorge
 * @date 05/05/2025
 */
class ViewModelClass() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        scope.launch {
            println("Custom scope long-running task...")
            delay(500)
            println("Custom scope Task finished!")
        }
    }
    init {

    }
    // ViewModel implementation
}
fun main() {
   // val viewModelClass = ViewModelClass()
    mainFunction()
 /*   mainException()
    val taskWorker = TaskWorker
    taskWorker.startTask()*/


    /*   val test = Test()
       println(test.twoSum(intArrayOf(2, 7, 11, 15), 13))
       println(mergeSort(intArrayOf(55,22,13,54,23,67,89,12)).toString())
       println(test.test())*/
}
object TaskWorker {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun startTask() {
        scope.launch {
            println("Executing long-running task...")
            delay(2000)
            println("Task finished!")
        }
    }
}





inline fun <reified T> checkType(value: Any) {
    if (value is T) {
        println("It’s a ${T::class.simpleName}")
    } else {
        println("Not a ${T::class.simpleName}")
    }
}

fun mainException() = runBlocking {
    try {
        coroutineScope {
            async {
                delay(200)
                throw Exception("Error in child 1")
            }

            launch {
                try {
                    repeat(5) {
                        println("Child 2 working...")
                        delay(300)
                    }
                } finally {
                    println("Child 2 cancelled due to sibling failure")
                }
            }
        }
    } catch (e: Exception) {
        println("Caught in parent: ${e.message}")
    }
}
fun mainFunction() { // creates a coroutine scope

    checkType<Int>(333)
    checkType<String>(333)
    println("Main scope started")

    val scope = CoroutineScope(Dispatchers.IO)
    scope.async {

        println("Global scope started on ${Thread.currentThread().name}")
        fetchUserData() // suspending function
        println("Global scope resumed after fetchUserData()")

        withContext(context = Dispatchers.IO) {
            println("with Context started on ${Thread.currentThread().name}")

            delay(300)
            println("with Context ended on ${Thread.currentThread().name}")
        }
    }
    println("Main scope ended")
}

// suspending function that simulates a network call
suspend fun fetchUserData() {
    println("Fetching user data...")
    delay(500) // suspends for 2 seconds (non-blocking)
    println("Data fetched successfully!") // may not print if canceled
}



fun mergeSort(arr: IntArray): IntArray {
    if (arr.size <= 1) return arr

    val mid = arr.size / 2
    val left = mergeSort(arr.sliceArray(0 until mid))
    val right = mergeSort(arr.sliceArray(mid until arr.size))

    return merge(left, right)
}

fun merge(left: IntArray, right: IntArray): IntArray {
    val result = IntArray(left.size + right.size)
    var i = 0
    var j = 0
    var k = 0

    while (i < left.size && j < right.size) {
        if (left[i] <= right[j]) {
            result[k++] = left[i++]
        } else {
            result[k++] = right[j++]
        }
    }

    while (i < left.size) {
        result[k++] = left[i++]
    }

    while (j < right.size) {
        result[k++] = right[j++]
    }

    return result
}

class Test {

    val array = listOf("a", "b", "c", "c", "b", "a", "z", "w")
    fun twoSum(nums: IntArray, target: Int): IntArray {

        for (i in nums.indices) {
            for (j in i + 1 until nums.size) {
                if (nums[i] + nums[j] == target) {

                    return intArrayOf(i, j)
                }
            }
        }

        return emptyArray<Int>().toIntArray()
    }

    fun test(): List<String> {
        val list = array.toMutableList().apply {
            val temp = this[0]
            this[0] = this.last()
            this[this.lastIndex] = temp
        }
        return list
    }

    fun findMedianSortedArrays(nums1: IntArray, nums2: IntArray): Double {
        var array = nums1 + nums2
        for (i in array.indices) {
            for (j in i + 1 until array.size) {
                if (array[i] > array[j]) {
                    val temp = array[i]
                    array[i] = array[j]
                    array[j] = temp
                }
            }
        }
        val n = array.size
        return if (n % 2 == 0) {
            (array[n / 2 - 1] + array[n / 2]) / 2.0
        } else {
            array[n / 2].toDouble()
        }

    }


}