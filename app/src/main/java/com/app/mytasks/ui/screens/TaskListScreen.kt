package com.app.mytasks.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.app.mytasks.data.Task
import com.app.mytasks.ui.components.TaskItem
import com.app.mytasks.viemodel.TaskViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onTaskClick: (Task) -> Unit,
    onAddTask: () -> Unit,
    onSettingsClick: () -> Unit // Add this
) {
    var sortBy by remember { mutableStateOf("priority") }
    var filter by remember { mutableStateOf("All") }
    val tasks by viewModel.tasks.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    var filteredTasks = when (filter) {
        "Completed" -> tasks.filter { it.isCompleted }
        "Pending" -> tasks.filter { !it.isCompleted }
        else -> tasks
    }.sortedWith(
        when (sortBy) {
            "dueDate" -> compareBy { it.dueDate ?: Long.MAX_VALUE }
            "alphabetically" -> compareBy { it.title }
            else -> compareBy { it.priority.ordinal }
        })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks", fontSize = TextUnit.Unspecified) },
                actions = {
                    Row(modifier = Modifier.padding(end = 8.dp)) {
                        FilterDropdown(filter = filter, onFilterChange = { filter = it })
                        Spacer(modifier = Modifier.width(8.dp))
                        SortDropdown(sortBy = sortBy, onSortChange = { sortBy = it })
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onSettingsClick, modifier = Modifier.width(9.dp)) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                }
            )
        },
        floatingActionButton = { BouncyFAB(onAddTask) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->


        val stateList = rememberLazyListState()

        var draggingItemIndex: Int? by remember {
            mutableStateOf(null)
        }

        var delta: Float by remember {
            mutableFloatStateOf(0f)
        }

        var draggingItem: LazyListItemInfo? by remember {
            mutableStateOf(null)
        }

        val onMove = { fromIndex: Int, toIndex: Int ->
            filteredTasks = filteredTasks.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
        }

        val scrollChannel = Channel<Float>()

        LaunchedEffect(stateList) {
            while (true) {
                val diff = scrollChannel.receive()
                stateList.scrollBy(diff)
            }
        }
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(
                items = filteredTasks,
                key = { task -> task.id }
            ) { task ->
                var offsetX by remember { mutableStateOf(0f) }
                AnimatedVisibility(
                    visible = filteredTasks.contains(task),
                    enter = slideInVertically(initialOffsetY = { -it }),
                    exit = slideOutVertically(targetOffsetY = { -it })
                ) {
                    TaskItem(
                        task = task,
                        onClick = { onTaskClick(task) },
                        onDelete = { viewModel.deleteTask(task) },
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                    onDragEnd = {
                                        if (offsetX > 100f) { // Swipe right to complete
                                            viewModel.completeTask(task)
                                            scope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "Task completed",
                                                    actionLabel = "Undo",
                                                    duration = SnackbarDuration.Short
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    viewModel.undoComplete(task)
                                                } else {
                                                    viewModel.clearPendingAction(
                                                        TaskViewModel.PendingAction.Complete(
                                                            task
                                                        )
                                                    )
                                                }
                                            }
                                        } else if (offsetX < -100f) { // Swipe left to delete
                                            viewModel.deleteTask(task)
                                            scope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "Task deleted",
                                                    actionLabel = "Undo",
                                                    duration = SnackbarDuration.Short
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    viewModel.undoDelete(task)
                                                } else {
                                                    viewModel.clearPendingAction(
                                                        TaskViewModel.PendingAction.Delete(
                                                            task
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                        offsetX = 0f // Reset offset
                                    },
                                    onHorizontalDrag = { change, dragAmount ->
                                        offsetX += dragAmount
                                        change.consume()
                                    }
                                )
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { offset ->
                                        stateList.layoutInfo.visibleItemsInfo
                                            .firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
                                            ?.also {
                                                (it.contentType as? DraggableItem)?.let { draggableItem ->
                                                    draggingItem = it
                                                    draggingItemIndex = draggableItem.index
                                                }
                                            }
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        delta += dragAmount.y

                                        val currentDraggingItemIndex =
                                            draggingItemIndex ?: return@detectDragGesturesAfterLongPress
                                        val currentDraggingItem =
                                            draggingItem ?: return@detectDragGesturesAfterLongPress

                                        val startOffset = currentDraggingItem.offset + delta
                                        val endOffset =
                                            currentDraggingItem.offset + currentDraggingItem.size + delta
                                        val middleOffset = startOffset + (endOffset - startOffset) / 2

                                        val targetItem =
                                            stateList.layoutInfo.visibleItemsInfo.find { item ->
                                                middleOffset.toInt() in item.offset..item.offset + item.size &&
                                                        currentDraggingItem.index != item.index &&
                                                        item.contentType is DraggableItem
                                            }

                                        if (targetItem != null) {
                                            val targetIndex = (targetItem.contentType as DraggableItem).index
                                            onMove(currentDraggingItemIndex, targetIndex)
                                            draggingItemIndex = targetIndex
                                            delta += currentDraggingItem.offset - targetItem.offset
                                            draggingItem = targetItem
                                        } else {
                                            val startOffsetToTop =
                                                startOffset - stateList.layoutInfo.viewportStartOffset
                                            val endOffsetToBottom =
                                                endOffset - stateList.layoutInfo.viewportEndOffset
                                            val scroll =
                                                when {
                                                    startOffsetToTop < 0 -> startOffsetToTop.coerceAtMost(0f)
                                                    endOffsetToBottom > 0 -> endOffsetToBottom.coerceAtLeast(0f)
                                                    else -> 0f
                                                }
                                            val canScrollDown =
                                                currentDraggingItemIndex != filteredTasks.size - 1 && endOffsetToBottom > 0
                                            val canScrollUp = currentDraggingItemIndex != 0 && startOffsetToTop < 0
                                            if (scroll != 0f && (canScrollUp || canScrollDown)) {
                                                scrollChannel.trySend(scroll)
                                            }
                                        }
                                    },
                                    onDragEnd = {
                                        draggingItem = null
                                        draggingItemIndex = null
                                        delta = 0f
                                    },
                                    onDragCancel = {
                                        draggingItem = null
                                        draggingItemIndex = null
                                        delta = 0f
                                    },
                                )
                            }
                            
                            .offset(x = offsetX.dp) // Visual feedback during swipe
                    )
                }
            }
        }
    }


}

//FAB
@Composable
fun BouncyFAB(onAddTask: () -> Unit) {
    var isClicked by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isClicked) 0.85f else 1f, // Shrinks slightly on click
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        finishedListener = { isClicked = false } // Reset after animation
    )

    FloatingActionButton(
        onClick = {
            isClicked = true
            onAddTask()
        },
        modifier = Modifier.scale(scale) // Apply animation
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Task")
    }
}


@Composable
fun MyList() {
    var list1 by remember { mutableStateOf(List(20) { it }) }
    val stateList = rememberLazyListState()

    var draggingItemIndex: Int? by remember {
        mutableStateOf(null)
    }

    var delta: Float by remember {
        mutableFloatStateOf(0f)
    }

    var draggingItem: LazyListItemInfo? by remember {
        mutableStateOf(null)
    }

    val onMove = { fromIndex: Int, toIndex: Int ->
        list1 = list1.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
    }

    val scrollChannel = Channel<Float>()

    LaunchedEffect(stateList) {
        while (true) {
            val diff = scrollChannel.receive()
            stateList.scrollBy(diff)
        }
    }

    LazyColumn(
        modifier = Modifier
            .pointerInput(key1 = stateList) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        stateList.layoutInfo.visibleItemsInfo
                            .firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
                            ?.also {
                                (it.contentType as? DraggableItem)?.let { draggableItem ->
                                    draggingItem = it
                                    draggingItemIndex = draggableItem.index
                                }
                            }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        delta += dragAmount.y

                        val currentDraggingItemIndex =
                            draggingItemIndex ?: return@detectDragGesturesAfterLongPress
                        val currentDraggingItem =
                            draggingItem ?: return@detectDragGesturesAfterLongPress

                        val startOffset = currentDraggingItem.offset + delta
                        val endOffset =
                            currentDraggingItem.offset + currentDraggingItem.size + delta
                        val middleOffset = startOffset + (endOffset - startOffset) / 2

                        val targetItem =
                            stateList.layoutInfo.visibleItemsInfo.find { item ->
                                middleOffset.toInt() in item.offset..item.offset + item.size &&
                                        currentDraggingItem.index != item.index &&
                                        item.contentType is DraggableItem
                            }

                        if (targetItem != null) {
                            val targetIndex = (targetItem.contentType as DraggableItem).index
                            onMove(currentDraggingItemIndex, targetIndex)
                            draggingItemIndex = targetIndex
                            delta += currentDraggingItem.offset - targetItem.offset
                            draggingItem = targetItem
                        } else {
                            val startOffsetToTop =
                                startOffset - stateList.layoutInfo.viewportStartOffset
                            val endOffsetToBottom =
                                endOffset - stateList.layoutInfo.viewportEndOffset
                            val scroll =
                                when {
                                    startOffsetToTop < 0 -> startOffsetToTop.coerceAtMost(0f)
                                    endOffsetToBottom > 0 -> endOffsetToBottom.coerceAtLeast(0f)
                                    else -> 0f
                                }
                            val canScrollDown =
                                currentDraggingItemIndex != list1.size - 1 && endOffsetToBottom > 0
                            val canScrollUp = currentDraggingItemIndex != 0 && startOffsetToTop < 0
                            if (scroll != 0f && (canScrollUp || canScrollDown)) {
                                scrollChannel.trySend(scroll)
                            }
                        }
                    },
                    onDragEnd = {
                        draggingItem = null
                        draggingItemIndex = null
                        delta = 0f
                    },
                    onDragCancel = {
                        draggingItem = null
                        draggingItemIndex = null
                        delta = 0f
                    },
                )
            },
        state = stateList,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Title 1", fontSize = 30.sp)
        }

        itemsIndexed(
            items = list1,
            contentType = { index, _ -> DraggableItem(index = index) }) { index, item ->
            val modifier = if (draggingItemIndex == index) {
                Modifier
                    .zIndex(1f)
                    .graphicsLayer {
                        translationY = delta
                    }
            } else {
                Modifier
            }
            Item(
                modifier = modifier,
                index = item,
            )
        }

        item {
            Text(text = "Title 2", fontSize = 30.sp)
        }


    }
}


@Composable
private fun Item(modifier: Modifier = Modifier, index: Int) {
    Card(
        modifier = modifier
    ) {
        Text(
            "Item $index",
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
    }
}

data class DraggableItem(val index: Int)
// Updated FilterDropdown
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(filter: String, onFilterChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.widthIn(max = 120.dp) // Limit width
    ) {
        TextField(
            value = filter,
            onValueChange = {},
            readOnly = true,
            label = { Text("Filter") },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("All", "Completed", "Pending").forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onFilterChange(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Updated SortDropdown
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortDropdown(sortBy: String, onSortChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.widthIn(max = 120.dp) // Limit width
    ) {
        TextField(
            value = sortBy,
            onValueChange = {},
            readOnly = true,
            label = { Text("Sort") },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("priority", "dueDate", "alphabetically").forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSortChange(it)
                        expanded = false
                    }
                )
            }
        }
    }
}