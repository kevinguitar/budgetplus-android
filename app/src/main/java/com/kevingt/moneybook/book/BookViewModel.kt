package com.kevingt.moneybook.book

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevingt.moneybook.R
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.data.remote.ExceedFreeBooksLimitException
import com.kevingt.moneybook.data.remote.ExceedPremiumBooksLimitException
import com.kevingt.moneybook.data.remote.JoinLinkExpiredException
import com.kevingt.moneybook.utils.NavigationFlow
import com.kevingt.moneybook.utils.NavigationInfo
import com.kevingt.moneybook.utils.Toaster
import com.kevingt.moneybook.welcome.WelcomeActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val navigation = NavigationFlow()

    init {
        if (bookRepo.currentBookId != null) {
            bookRepo.bookState
                .onEach { book ->
                    if (book == null) {
                        val nav = NavigationInfo(destination = WelcomeActivity::class)
                        navigation.tryEmit(nav)
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun handleIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        if (uri.pathSegments.firstOrNull() == "join") {
            bookRepo.setPendingJoinRequest(uri)
        }
    }

    fun handleJoinRequest() {
        if (!bookRepo.hasPendingJoinRequest) return

        viewModelScope.launch {
            try {
                val bookName = bookRepo.handlePendingJoinRequest()
                toaster.showMessage(context.getString(R.string.book_join_success, bookName))
            } catch (e: Exception) {
                when (e) {
                    is JoinLinkExpiredException -> toaster.showMessage(R.string.book_join_link_expired)
                    is ExceedFreeBooksLimitException -> toaster.showMessage(R.string.book_join_exceed_free_limit)
                    is ExceedPremiumBooksLimitException -> toaster.showMessage(R.string.book_exceed_maximum)
                    else -> toaster.showError(e)
                }
            }
        }
    }

}