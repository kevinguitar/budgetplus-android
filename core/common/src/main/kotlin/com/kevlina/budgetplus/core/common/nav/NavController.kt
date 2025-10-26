package com.kevlina.budgetplus.core.common.nav

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import timber.log.Timber

/**
 * A navigation controller that has a back stack for complex navigation trees like bottom navigation.
 *
 * @param startRoot which root of navigation tree that is initially displayed.
 */
class NavController<T : NavKey>(private val startRoot: T) {

    private val rootBackStacks = hashMapOf<T, SnapshotStateList<T>>(
        startRoot to mutableStateListOf(startRoot)
    )

    val backStack = mutableStateListOf(startRoot)
    val rootStack = mutableStateListOf(startRoot)

    private val currentRoot: T
        get() = rootStack.lastOrNull() ?: startRoot

    fun selectRoot(rootKey: T) {
        if (rootKey in rootStack) {
            // If the root was in the stack before, pops all the way to let the rootKey be on the top.
            val index = rootStack.indexOf(rootKey)
            while (true) {
                val root = rootStack.getOrNull(index + 1) ?: break
                rootBackStacks.remove(root)
                rootStack.remove(root)
            }
        } else {
            // If the root was never added before, simply add it
            rootStack.add(rootKey)
            rootBackStacks[rootKey] = mutableStateListOf(rootKey)
        }
        updateBackStack()
    }

    fun navigate(key: T) {
        rootBackStacks[currentRoot]?.add(key)
        updateBackStack()
    }

    fun navigateUp() {
        val currentStack = rootBackStacks[currentRoot] ?: return
        currentStack.removeLastOrNull()

        // Move on to the previous root if the current stack is empty
        if (currentStack.isEmpty()) {
            rootStack.removeLastOrNull()
        }
        updateBackStack()
    }

    private fun updateBackStack() {
        backStack.clear()
        rootStack.forEach { root ->
            val stack = rootBackStacks[root] ?: return@forEach
            backStack.addAll(stack)
        }
        Timber.d("NavController: BackStack=${backStack.toList()}")
    }

    companion object {
        val preview = NavController(BottomNavTab.Add.root)
    }
}