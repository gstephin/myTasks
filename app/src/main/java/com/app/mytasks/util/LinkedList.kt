package com.app.mytasks.util

/**
 * LinkedList.kt
 *
 * @author stephingeorge
 * @date 22/08/2025
 */

fun main(args: Array<String>) {
    print("Hello")
    val list1 = arrayToLinkedList(listOf(1, 2, 4))
    val list2 = arrayToLinkedList(listOf(1, 3, 4))
    val mergedList = mergeTwoLists(list1, list2)

}
class ListNode(var `val`: Int) {
    var next: ListNode? = null
}

fun arrayToLinkedList(arr: List<Int>): ListNode? {
    val dummy = ListNode(0)
    var current = dummy
    for (num in arr) {
        current.next = ListNode(num)
        current = current.next!!
    }
    return dummy.next
}
fun mergeTwoLists(list1: ListNode?, list2: ListNode?): ListNode? {
    val dummy = ListNode(0)
    var current = dummy
    var l1 = list1
    var l2 = list2

    while (l1 != null && l2!= null){
        if (l1.`val` < l2.`val`){
            current.next = l1
            l1 = l1.next
        } else {
            current.next = l2
            l2 = l2.next
        }
        current = current.next!!

    }
    return  dummy.next
}
