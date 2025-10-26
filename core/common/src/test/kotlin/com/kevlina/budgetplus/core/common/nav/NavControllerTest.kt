package com.kevlina.budgetplus.core.common.nav

import androidx.navigation3.runtime.NavKey
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NavControllerTest {

    private sealed interface TestKey : NavKey {
        data object RootA : TestKey
        data object RootB : TestKey
        data object RootC : TestKey
        data object Screen1 : TestKey
        data object Screen2 : TestKey
    }

    @Test
    fun `Initial state verification`() {
        val startRoot = TestKey.RootA
        val navController = NavController(startRoot)
        assertThat(navController.backStack).containsExactly(startRoot)
        assertThat(navController.rootStack).containsExactly(startRoot)
    }

    @Test
    fun `Initial state current root check`() {
        val startRoot = TestKey.RootA
        val navController = NavController<TestKey>(startRoot)
        // Indirectly check currentRoot. Navigation should happen on the startRoot.
        navController.navigate(TestKey.Screen1)
        assertThat(navController.backStack).containsExactly(startRoot, TestKey.Screen1).inOrder()
    }

    @Test
    fun `navigate  Basic navigation on start root`() {
        val startRoot = TestKey.RootA
        val navController = NavController<TestKey>(startRoot)
        val newKey = TestKey.Screen1
        navController.navigate(newKey)
        assertThat(navController.backStack).containsExactly(startRoot, newKey).inOrder()
    }

    @Test
    fun `navigate  Multiple navigations`() {
        val startRoot = TestKey.RootA
        val navController = NavController<TestKey>(startRoot)
        val key1 = TestKey.Screen1
        val key2 = TestKey.Screen2

        navController.navigate(key1)
        navController.navigate(key2)

        assertThat(navController.backStack).containsExactly(startRoot, key1, key2).inOrder()
    }

    @Test
    fun `navigateUp  Basic navigation up`() {
        val startRoot = TestKey.RootA
        val navController = NavController<TestKey>(startRoot)
        navController.navigate(TestKey.Screen1)
        navController.navigateUp()
        assertThat(navController.backStack).containsExactly(startRoot)
    }

    @Test
    fun `navigateUp  Emptying the current root s stack`() {
        val startRoot = TestKey.RootA
        val navController = NavController<TestKey>(startRoot)
        navController.navigate(TestKey.Screen1)

        navController.navigateUp() // Removes Screen1
        assertThat(navController.backStack).containsExactly(startRoot)
        assertThat(navController.rootStack).containsExactly(startRoot)

        navController.navigateUp() // Removes RootA
        assertThat(navController.backStack).isEmpty()
        assertThat(navController.rootStack).isEmpty()
    }

    @Test
    fun `navigateUp  On initial state`() {
        val startRoot = TestKey.RootA
        val navController = NavController(startRoot)
        navController.navigateUp()
        assertThat(navController.backStack).isEmpty()
        assertThat(navController.rootStack).isEmpty()
    }

    @Test
    fun `navigateUp  On an already empty back stack`() {
        val startRoot = TestKey.RootA
        val navController = NavController(startRoot)
        navController.navigateUp() // first up, empties the stack
        navController.navigateUp() // second up, should not crash
        assertThat(navController.backStack).isEmpty()
        assertThat(navController.rootStack).isEmpty()
    }

    @Test
    fun `selectRoot  Selecting a new root`() {
        val startRoot = TestKey.RootA
        val newRoot = TestKey.RootB
        val navController = NavController<TestKey>(startRoot)
        navController.selectRoot(newRoot)
        assertThat(navController.rootStack).containsExactly(startRoot, newRoot).inOrder()
        assertThat(navController.backStack).containsExactly(startRoot, newRoot).inOrder()
    }

    @Test
    fun `selectRoot  Switching back to an existing root`() {
        val startRoot = TestKey.RootA
        val newRoot = TestKey.RootB
        val navController = NavController<TestKey>(startRoot)
        navController.selectRoot(newRoot)
        navController.selectRoot(startRoot)
        assertThat(navController.rootStack).containsExactly(startRoot)
        assertThat(navController.backStack).containsExactly(startRoot)
    }

    @Test
    fun `selectRoot  Switching to a non top existing root`() {
        val r1 = TestKey.RootA
        val r2 = TestKey.RootB
        val r3 = TestKey.RootC
        val navController = NavController<TestKey>(r1)

        navController.selectRoot(r2)
        navController.selectRoot(r3)
        assertThat(navController.rootStack).containsExactly(r1, r2, r3).inOrder()

        navController.selectRoot(r1)
        assertThat(navController.rootStack).containsExactly(r1)
        assertThat(navController.backStack).containsExactly(r1)
    }

    @Test
    fun `selectRoot  Selecting the current root`() {
        val r1 = TestKey.RootA
        val r2 = TestKey.RootB
        val navController = NavController<TestKey>(r1)
        navController.selectRoot(r2)

        val rootStackBefore = navController.rootStack.toList()
        val backStackBefore = navController.backStack.toList()

        navController.selectRoot(r2)

        assertThat(navController.rootStack).isEqualTo(rootStackBefore)
        assertThat(navController.backStack).isEqualTo(backStackBefore)
    }

    @Test
    fun `Complex scenario  Navigation within a non start root`() {
        val startRoot = TestKey.RootA
        val newRoot = TestKey.RootB
        val navController = NavController<TestKey>(startRoot)
        navController.selectRoot(newRoot)
        navController.navigate(TestKey.Screen1)
        navController.navigate(TestKey.Screen2)

        assertThat(navController.backStack).containsExactly(startRoot, newRoot, TestKey.Screen1, TestKey.Screen2).inOrder()
        assertThat(navController.rootStack).containsExactly(startRoot, newRoot).inOrder()
    }

    @Test
    fun `Complex scenario  State preservation after switching roots`() {
        val r1 = TestKey.RootA
        val n1 = TestKey.Screen1
        val r2 = TestKey.RootB
        val navController = NavController<TestKey>(r1)

        navController.navigate(n1)
        navController.selectRoot(r2)
        navController.selectRoot(r1)

        assertThat(navController.backStack).containsExactly(r1, n1).inOrder()
        assertThat(navController.rootStack).containsExactly(r1)
    }

    @Test
    fun `Complex scenario  navigateUp within a deep root stack`() {
        val r1 = TestKey.RootA
        val n1 = TestKey.Screen1
        val r2 = TestKey.RootB
        val n2 = TestKey.Screen2
        val navController = NavController<TestKey>(r1)

        navController.navigate(n1)
        navController.selectRoot(r2)
        navController.navigate(n2)
        navController.navigateUp()

        assertThat(navController.backStack).containsExactly(r1, n1, r2).inOrder()
        assertThat(navController.rootStack).containsExactly(r1, r2).inOrder()
    }

    @Test
    fun `Complex scenario  navigateUp to pop a root`() {
        val r1 = TestKey.RootA
        val r2 = TestKey.RootB
        val navController = NavController<TestKey>(r1)

        navController.selectRoot(r2)
        navController.navigateUp()

        assertThat(navController.backStack).containsExactly(r1)
        assertThat(navController.rootStack).containsExactly(r1)
    }

    @Test
    fun `getBackStack  Direct modification attempt`() {
        val startRoot = TestKey.RootA
        val navController = NavController<TestKey>(startRoot)
        // Direct modification of the returned list can lead to inconsistent states.
        navController.backStack.add(TestKey.Screen1)
        assertThat(navController.backStack).containsExactly(startRoot, TestKey.Screen1).inOrder()

        // A subsequent navigation call will fix the stack.
        navController.navigate(TestKey.Screen2)
        assertThat(navController.backStack).containsExactly(startRoot, TestKey.Screen2).inOrder()
    }

    @Test
    fun `getRootStack  Direct modification attempt`() {
        val startRoot = TestKey.RootA
        val navController = NavController<TestKey>(startRoot)
        // Direct modification can corrupt the internal state.
        navController.rootStack.add(TestKey.RootB)
        assertThat(navController.rootStack).containsExactly(startRoot, TestKey.RootB).inOrder()

        // backStack will be out of sync until updateBackStack() is triggered.
        assertThat(navController.backStack).containsExactly(startRoot)
    }
}