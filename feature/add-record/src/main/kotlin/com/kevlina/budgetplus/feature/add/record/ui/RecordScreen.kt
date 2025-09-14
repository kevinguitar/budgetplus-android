package com.kevlina.budgetplus.feature.add.record.ui

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
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.ui.AdaptiveScreen
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.MenuAction
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.feature.add.record.RecordViewModel
import com.kevlina.budgetplus.feature.category.pills.toState
import kotlinx.coroutines.flow.collect

@Composable
fun RecordScreen(navController: NavController) {

    val vm = hiltViewModel<RecordViewModel>()

    var isRequestingReview by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = vm) {
        vm.requestReviewEvent
            .consumeEach { isRequestingReview = true }
            .collect()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        TopBar(
            title = null,
            titleContent = { BookSelector(navController) },
            menuActions = {
                MenuAction(
                    imageVector = Icons.Rounded.GroupAdd,
                    description = stringResource(id = R.string.cta_invite),
                    onClick = vm::shareJoinLink,
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
                        navController.navigate(AddDest.Settings())
                    }
                )
            }
        )

        AdaptiveScreen(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .weight(1F),
            regularContent = {
                val recordInfoState = vm.toState(
                    navController = navController,
                    scrollable = true,
                )
                RecordContentRegular(recordInfoState)
            },
            wideContent = {
                val recordInfoState = vm.toState(
                    navController = navController,
                    scrollable = true,
                )
                RecordContentWide(recordInfoState)
            },
            packedContent = {
                val recordInfoState = vm.toState(
                    navController = navController,
                    scrollable = false,
                )
                RecordContentPacked(recordInfoState)
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
                    vm.launchReviewFlow()
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
internal class RecordContentState(
    val recordInfoState: RecordInfoState,
    val calculatorState: CalculatorState,
) {
    companion object {
        val preview = RecordContentState(
            recordInfoState = RecordInfoState.preview,
            calculatorState = CalculatorState.preview,
        )
    }
}

private fun RecordViewModel.toState(
    navController: NavController,
    scrollable: Boolean,
) = RecordContentState(
    recordInfoState = RecordInfoState(
        type = type,
        note = note,
        setType = ::setType,
        scrollable = scrollable,
        categoriesGridState = categoriesVm.toState(
            type = type,
            onEditClicked = if (bookRepo.canEdit) {
                { navController.navigate(AddDest.EditCategory(type.value)) }
            } else null,
        ),
        dateAndPricingState = DateAndPricingState(
            recordDate = recordDate,
            currencySymbol = bookRepo.currencySymbol,
            priceText = calculatorVm.priceText,
            scrollable = scrollable,
            setDate = ::setDate,
            editCurrency = {
                if (bookRepo.canEdit) {
                    navController.navigate(route = AddDest.CurrencyPicker)
                }
            }
        ),
    ),
    calculatorState = calculatorVm.toState(),
)