package com.app.mytasks.viemodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mytasks.data.entities.Task
import com.app.mytasks.data.dao.TaskDao
import com.app.mytasks.domain.repository.TaskRepository
import com.app.mytasks.util.FileUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * TaskViewModel
 *
 * ViewModel for managing tasks.
 *
 * @author Stephin
 * @date 2025-03-12
 */
data class Person(var name: String = "", var age: Int = 0)

val person = Person()
val description = with(person) {
    name = "Alice"
    age = 25
    "Person's name is $name and age is $age"
}

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    @ApplicationContext
    private val context: Context, // ✅ injected context
    private val gson: Gson

) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    var tasks: StateFlow<List<Task>> = _tasks

    private val _pendingActions = MutableStateFlow<List<PendingAction>>(emptyList())


    init {

        //sample data for testing
        viewModelScope.launch {
            if (repository.getTaskCount() == 0) { // Insert only if DB is empty
                val json = FileUtil().loadJSONFromAssets(context, "data.json")
                val taskList: List<Task> =
                    gson.fromJson(json, object : TypeToken<List<Task>>() {}.type)
                repository.insertAllTask(taskList)
            }
            loadTasks()

        }
    }


    private fun loadTasks() {
        viewModelScope.launch {
            _tasks.value = repository.getAllTasks()
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
            loadTasks()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
            loadTasks()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            loadTasks()
            _pendingActions.value = _pendingActions.value + PendingAction.Delete(task)
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = true)
            repository.updateTask(updatedTask)
            loadTasks()
            _pendingActions.value = _pendingActions.value + PendingAction.Complete(task)
        }
    }

    fun undoDelete(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
            loadTasks()
            _pendingActions.value = _pendingActions.value.filterNot { it.task == task }
        }
    }

    fun undoComplete(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = false)
            repository.updateTask(updatedTask)
            loadTasks()
            _pendingActions.value = _pendingActions.value.filterNot { it.task == task }
        }
    }

    fun clearPending(action: PendingAction) {
        _pendingActions.value = _pendingActions.value.filterNot { it == action }
    }

    sealed class PendingAction(val task: Task) {
        class Delete(task: Task) : PendingAction(task)
        class Complete(task: Task) : PendingAction(task)
    }

    fun bubbleSort(
        list: List<Task>,
        comparator: Comparator<Task>
    ): List<Task> {
        val sortedList = list.toMutableList()
        val n = sortedList.size
        for (i in 0 until n - 1) {
            for (j in 0 until n - i - 1) {
                if (comparator.compare(sortedList[j], sortedList[j + 1]) > 0) {
                    // Swap sortedList[j] and sortedList[j + 1]
                    val temp = sortedList[j]
                    sortedList[j] = sortedList[j + 1]
                    sortedList[j + 1] = temp
                }
            }
        }
        return sortedList
    }
} 

