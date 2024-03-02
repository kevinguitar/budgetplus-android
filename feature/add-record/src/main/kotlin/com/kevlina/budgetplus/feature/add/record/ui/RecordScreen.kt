package com.kevlina.budgetplus.feature.add.record.ui

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.AdaptiveScreen
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.MenuAction
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.feature.add.record.RecordViewModel
import com.kevlina.budgetplus.feature.category.pills.toUiState
import kotlinx.coroutines.flow.collect

@Composable
fun RecordScreen(navigator: Navigator) {

    val vm = hiltViewModel<RecordViewModel>()

    val context = LocalContext.current

    var isRequestingReview by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = vm) {
        vm.requestReviewEvent
            .consumeEach { isRequestingReview = true }
            .collect()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        TopBar(
            title = null,
            titleContent = { BookSelector(navigator) },
            menuActions = {
                MenuAction(
                    imageVector = Icons.Rounded.GroupAdd,
                    description = stringResource(id = R.string.cta_invite),
                    onClick = { vm.shareJoinLink(context) },
                    modifier = Modifier.onPlaced {
                        vm.highlightInviteButton(
                            BubbleDest.Invite(
                                size = it.size,
                                offset = it.positionInRoot()
                            )
                        )
                    }
                )

                MenuAction(
                    imageVector = Icons.Rounded.Settings,
                    description = stringResource(id = R.string.settings_description),
                    onClick = {
                        navigator.navigate(AddDest.Settings.route)
                    }
                )
            }
        )

        AdaptiveScreen(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .weight(1F),
            regularContent = {
                val recordInfoUiState = vm.toUiState(
                    navigator = navigator,
                    scrollable = true,
                    adaptiveCalculatorButton = false
                )
                RecordContentRegular(recordInfoUiState)
            },
            wideContent = {
                val recordInfoUiState = vm.toUiState(
                    navigator = navigator,
                    scrollable = true,
                    adaptiveCalculatorButton = true
                )
                RecordContentWide(recordInfoUiState)
            },
            packedContent = {
                val recordInfoUiState = vm.toUiState(
                    navigator = navigator,
                    scrollable = false,
                    adaptiveCalculatorButton = false
                )
                RecordContentPacked(recordInfoUiState)
            },
            extraContent = {
                DoneAnimator(eventTrigger = vm.recordEvent)
            }
        )

        if (isRequestingReview) {
            ConfirmDialog(
                message = stringResource(id = R.string.review_request_message),
                confirmText = stringResource(id = R.string.review_request_yes),
                cancelText = stringResource(id = R.string.review_request_no),
                onConfirm = {
                    vm.launchReviewFlow(context as Activity)
                    isRequestingReview = false
                },
                onDismiss = {
                    vm.rejectReview()
                    isRequestingReview = false
                }
            )
        }
    }

    // Handle the runtime notification permission request
    NotificationPermissionHandler()
}

@Stable
internal class RecordContentUiState(
    val recordInfoUiState: RecordInfoUiState,
    val calculatorUiState: CalculatorUiState,
) {
    companion object {
        val preview = RecordContentUiState(
            recordInfoUiState = RecordInfoUiState.preview,
            calculatorUiState = CalculatorUiState.preview,
        )
    }
}

private fun RecordViewModel.toUiState(
    navigator: Navigator,
    scrollable: Boolean,
    adaptiveCalculatorButton: Boolean,
) = RecordContentUiState(
    recordInfoUiState = RecordInfoUiState(
        type = type,
        note = note,
        setType = ::setType,
        setNote = ::setNote,
        scrollable = scrollable,
        categoriesGridUiState = categoriesVm.toUiState(
            type = type,
            onEditClicked = {
                navigator.navigate(route = "${AddDest.EditCategory.route}/${type.value}")
            },
        ),
        dateAndPricingUiState = DateAndPricingUiState(
            recordDate = recordDate,
            priceText = calculatorVm.priceText,
            scrollable = scrollable,
            setDate = ::setDate
        ),
    ),
    calculatorUiState = calculatorVm.toUiState(adaptiveButton = adaptiveCalculatorButton),
)