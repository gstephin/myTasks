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
import kotlinx.coroutines.flow.update
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

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _pendingActions = MutableStateFlow<List<PendingAction>>(emptyList())
    val pendingActions: StateFlow<List<PendingAction>> = _pendingActions

    init {
        viewModelScope.launch {
            if (repository.getTaskCount() == 0) {
                val json = FileUtil().loadJSONFromAssets(context, "data.json")
                val taskList: List<Task> =
                    gson.fromJson(json, object : TypeToken<List<Task>>() {}.type)
                repository.insertAllTask(taskList)
            }

            // Collect DB updates reactively
            repository.getAllTasksFlow().collect { _tasks.value = it }
        }
    }

    fun addTask(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
        _pendingActions.update { it + PendingAction.Delete(task) }
    }

    fun completeTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task.copy(isCompleted = true))
        _pendingActions.update { it + PendingAction.Complete(task) }
    }

    fun undoDelete(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
        _pendingActions.update { it.filterNot { a -> a.task == task } }
    }

    fun undoComplete(task: Task) = viewModelScope.launch {
        repository.updateTask(task.copy(isCompleted = false))
        _pendingActions.update { it.filterNot { a -> a.task == task } }
    }

    fun clearPending(action: PendingAction) {
        _pendingActions.update { it.filterNot { it == action } }
    }

    sealed class PendingAction(val task: Task) {
        class Delete(task: Task) : PendingAction(task)
        class Complete(task: Task) : PendingAction(task)
    }
}

