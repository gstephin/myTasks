package com.app.mytasks.viemodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mytasks.data.Task
import com.app.mytasks.data.TaskDao
import com.app.mytasks.util.FileUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


/**
 * TaskViewModel
 *
 * ViewModel for managing tasks.
 *
 * @author Stephin
 * @date 2025-03-12
 */

class TaskViewModel(private val dao: TaskDao, application: Application) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    var tasks: StateFlow<List<Task>> = _tasks

    private val _pendingActions = MutableStateFlow<List<PendingAction>>(emptyList())

    private val gson = Gson()

    init {

        //sample data for testing
        viewModelScope.launch {
            if (dao.getTaskCount() == 0) { // Insert only if DB is empty
                val json = FileUtil().loadJSONFromAssets(application, "data.json")
                val taskList: List<Task> =
                    gson.fromJson(json, object : TypeToken<List<Task>>() {}.type)
                dao.insertAll(taskList)
            }
            loadTasks()

        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _tasks.value = dao.getAllTasks()
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            dao.insert(task)
            loadTasks()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            dao.update(task)
            loadTasks()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.delete(task)
            loadTasks()
            _pendingActions.value = _pendingActions.value + PendingAction.Delete(task)
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = true)
            dao.update(updatedTask)
            loadTasks()
            _pendingActions.value = _pendingActions.value + PendingAction.Complete(task)
        }
    }

    fun undoDelete(task: Task) {
        viewModelScope.launch {
            dao.insert(task)
            loadTasks()
            _pendingActions.value = _pendingActions.value.filterNot { it.task == task }
        }
    }

    fun undoComplete(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = false)
            dao.update(updatedTask)
            loadTasks()
            _pendingActions.value = _pendingActions.value.filterNot { it.task == task }
        }
    }

    fun clearPendingAction(action: PendingAction) {
        _pendingActions.value = _pendingActions.value.filterNot { it == action }
    }

    sealed class PendingAction(val task: Task) {
        class Delete(task: Task) : PendingAction(task)
        class Complete(task: Task) : PendingAction(task)
    }
}

