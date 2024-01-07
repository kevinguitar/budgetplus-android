package com.kevlina.budgetplus.feature.edit.category

import com.google.common.truth.Truth.assertThat
import com.kevlina.budgetplus.core.common.impl.FakeStringProvider
import com.kevlina.budgetplus.core.data.CategoryRenameEvent
import com.kevlina.budgetplus.core.data.local.FakePreferenceHolder
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class EditCategoryViewModelTest {

    @Test
    fun `WHEN rename categories THEN sanitize the events`() = runTest {
        val model = createModel()
        model.onCategoryRenamed("Food", "Restaurant")
        model.onCategoryRenamed("Sport", "Exercise")
        model.onCategoryRenamed("Util", "Utility")
        model.onCategoryRenamed("Exercise", "Health")
        model.onCategoryRenamed("Restaurant", "Food")

        assertThat(model.categoryRenameEvents).containsExactly(
            CategoryRenameEvent(from = "Sport", "Health"),
            CategoryRenameEvent(from = "Util", "Utility")
        )
    }

    @Test
    fun `WHEN delete a category THEN drop it from the rename events`() = runTest {
        val model = createModel()
        model.onCategoryRenamed("Food", "Restaurant")
        model.onCategoryDeleted("Restaurant")

        assertThat(model.categoryRenameEvents).isEmpty()
    }

    private fun createModel() = EditCategoryViewModel(
        bookRepo = mockk(),
        recordRepo = mockk(),
        bubbleRepo = mockk(),
        toaster = mockk(),
        stringProvider = FakeStringProvider(),
        preferenceHolder = FakePreferenceHolder {
            put("isEditHintBubbleShown", false)
            put("isSaveBubbleShown", false)
        }
    )
}