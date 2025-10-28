package com.app.mytasks.ui.screens
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.app.mytasks.R
import com.app.mytasks.data.entities.Task
import com.app.mytasks.ui.components.CircularProgressbar
import com.app.mytasks.ui.components.TaskItem
import com.app.mytasks.viemodel.TaskViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * TaskListScreen
 *
 *
 * Tasks screen with task list and options.
 *
 * @author Stephin
 * @date 2025-03-12
 */
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

    val originalTasks by viewModel.tasks.collectAsState()  // Keep original list
    var displayedTasks by remember { mutableStateOf(originalTasks) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    // Apply filtering and sorting **only for display**
    LaunchedEffect(originalTasks, sortBy, filter) {
        val filteredTasks = originalTasks.filter { task ->
            when (filter) {
                "Completed" -> task.isCompleted
                "Pending" -> !task.isCompleted
                else -> true
            }
        }
        displayedTasks = when (sortBy) {
            "dueDate" -> filteredTasks.sortedBy { it.dueDate ?: Long.MAX_VALUE }
            "alphabetically" -> filteredTasks.sortedBy { it.title }
            else -> filteredTasks.sortedBy { it.priority.ordinal }
        }
    }
    val completedTasks = originalTasks.count { it.isCompleted }
    val totalTasks = originalTasks.size
    val progressValue = completedTasks.toFloat()
    val maxProgressValue = totalTasks.toFloat().coerceAtLeast(1f)

    val onMove = { fromIndex: Int, toIndex: Int ->
        val mutableOriginalTasks = displayedTasks.toMutableList()
        val movedTask = mutableOriginalTasks.removeAt(fromIndex)
        mutableOriginalTasks.add(toIndex, movedTask)
        displayedTasks = mutableOriginalTasks
    }
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    "Tasks",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }, actions = {
                Row(
                    modifier = Modifier.padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterDropdown(filter = filter, onFilterChange = { filter = it })
                    Spacer(modifier = Modifier.width(4.dp)) // Reduce spacing
                    SortDropdown(sortBy = sortBy, onSortChange = { sortBy = it })
                    Spacer(modifier = Modifier.width(4.dp)) // Reduce spacing

                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(36.dp) // Increase touch target
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(30.dp) // Increase icon size
                        )
                    }
                }
            })


        },
        floatingActionButton = { BouncyFAB(onAddTask) },
        snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->

        val stateList = rememberLazyListState()

        var showListView by remember { mutableStateOf(true) }
        var draggingItemIndex: Int? by remember {
            mutableStateOf(null)
        }
        var delta: Float by remember {
            mutableFloatStateOf(0f)
        }

        var draggingItem: LazyListItemInfo? by remember {
            mutableStateOf(null)
        }
        val scrollChannel = Channel<Float>()
        LaunchedEffect(stateList) {
            while (true) {
                val diff = scrollChannel.receive()
                stateList.scrollBy(diff)
            }
        }

        val isEmpty = displayedTasks.isEmpty()
        if (isEmpty) {

            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(200.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Task Progress",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                CircularProgressbar(
                    currentValue = progressValue,
                    maxValue = maxProgressValue,
                    progressBackgroundColor = Color.White,
                    progressIndicatorColor = MaterialTheme.colorScheme.primary,
                    completedColor = Color.Green,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Task List",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Total Tasks: $totalTasks",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Completed: $completedTasks",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        IconButton(
                            onClick = { showListView = true },
                            modifier = Modifier.size(36.dp) // Increase touch target
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.list),
                                contentDescription = "List",
                                modifier = Modifier.size(30.dp),
                                tint = if (showListView) MaterialTheme.colorScheme.primary else LocalContentColor.current

                            )
                        }
                        IconButton(
                            onClick = { showListView = false },
                            modifier = Modifier.size(36.dp) // Increase touch target
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.menu),
                                contentDescription = "Grid",
                                modifier = Modifier.size(20.dp),// Increase icon size
                                tint = if (!showListView) MaterialTheme.colorScheme.primary else LocalContentColor.current

                            )
                        }
                    }
                }

                if (!showListView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(20) { index ->
                            Text(
                                "Grid item #$index",
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                } else LazyColumn(
                    modifier = Modifier.pointerInput(key1 = stateList) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { offset ->
                                stateList.layoutInfo.visibleItemsInfo.firstOrNull { item ->
                                    offset.y.toInt() in item.offset..(item.offset + item.size)
                                }
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
                                        middleOffset.toInt() in item.offset..item.offset +
                                                item.size && currentDraggingItem.index != item.index
                                                && item.contentType is DraggableItem
                                    }

                                if (targetItem != null) {
                                    val targetIndex =
                                        (targetItem.contentType as DraggableItem).index
                                    onMove(currentDraggingItemIndex, targetIndex)
                                    draggingItemIndex = targetIndex
                                    delta += currentDraggingItem.offset - targetItem.offset
                                    draggingItem = targetItem
                                } else {
                                    val startOffsetToTop =
                                        startOffset - stateList.layoutInfo.viewportStartOffset
                                    val endOffsetToBottom =
                                        endOffset - stateList.layoutInfo.viewportEndOffset
                                    val scroll = when {
                                        startOffsetToTop < 0 -> startOffsetToTop.coerceAtMost(
                                            0f
                                        )

                                        endOffsetToBottom > 0 -> endOffsetToBottom.coerceAtLeast(
                                            0f
                                        )

                                        else -> 0f
                                    }
                                    val canScrollDown =
                                        currentDraggingItemIndex != displayedTasks.size - 1 && endOffsetToBottom > 0
                                    val canScrollUp =
                                        currentDraggingItemIndex != 0 && startOffsetToTop < 0
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
                ) {

                    itemsIndexed(
                        items = displayedTasks,
                        key = { task, id -> id.id },
                        contentType = { index, _ -> DraggableItem(index = index) }) { index, task ->
                        AnimatedVisibility(
                            visible = displayedTasks.contains(task),
                            enter = slideInVertically(initialOffsetY = { -it }),
                            exit = slideOutVertically(targetOffsetY = { -it })
                        ) {
                            var offsetX by remember { mutableStateOf(0f) }
                            val modifier = if (draggingItemIndex == index) {
                                Modifier
                                    .zIndex(1f)
                                    .graphicsLayer {
                                        translationY = delta
                                    }
                                    .offset(x = offsetX.dp)

                            } else {
                                Modifier
                            }

                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { dismissValue ->
                                    when (dismissValue) {
                                        SwipeToDismissBoxValue.EndToStart -> {
                                            scope.launch {
                                                viewModel.deleteTask(task)
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "Task deleted",
                                                    actionLabel = "Undo",
                                                    duration = SnackbarDuration.Short
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    viewModel.undoDelete(task)
                                                }
                                            }
                                            true
                                        }

                                        SwipeToDismissBoxValue.StartToEnd -> {
                                            scope.launch {
                                                viewModel.completeTask(task)
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "Task Completed",
                                                    actionLabel = "Undo",
                                                    duration = SnackbarDuration.Short
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    viewModel.undoComplete(task)
                                                }
                                            }
                                            false
                                        }

                                        else -> false
                                    }

                                })
                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = true,
                                enableDismissFromEndToStart = true,
                                backgroundContent = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp, vertical = 4.dp)
                                    )
                                },
                            ) {
                                // Apply swipe offset to move the item while swiping
                                Box(
                                    modifier = Modifier
                                        .offset(x = offsetX.dp) // Moves the item visually
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(4.dp)
                                ) {
                                    TaskItem(
                                        task = task,
                                        onClick = { onTaskClick(task) },
                                        onDelete = { viewModel.deleteTask(task) },
                                        modifier = modifier
                                    )
                                }

                            }


                        }

                    }

                }
            }
        }
    }

}

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
        }, modifier = Modifier.scale(scale) // Apply animation
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Task")
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
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("All", "Completed", "Pending").forEach {
                DropdownMenuItem(text = { Text(it) }, onClick = {
                    onFilterChange(it)
                    expanded = false
                })
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
                DropdownMenuItem(text = { Text(it) }, onClick = {
                    onSortChange(it)
                    expanded = false
                })
            }
        }
    }
}