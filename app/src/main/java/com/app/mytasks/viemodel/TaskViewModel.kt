package com.app.mytasks.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mytasks.data.Task
import com.app.mytasks.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val dao: TaskDao) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    var tasks: StateFlow<List<Task>> = _tasks

    private val _pendingActions = MutableStateFlow<List<PendingAction>>(emptyList())

    init {
        loadTasks()
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
    fun updateTaskOrder(newList: List<Task>) {
        _tasks.value = newList
      /*  viewModelScope.launch {
            dao.insertAll(newList)

        }*/
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

