package com.kevingt.moneybook.welcome

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.BookActivity
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.data.remote.ExceedFreeBooksLimitException
import com.kevingt.moneybook.data.remote.ExceedPremiumBooksLimitException
import com.kevingt.moneybook.data.remote.JoinLinkExpiredException
import com.kevingt.moneybook.utils.NavigationFlow
import com.kevingt.moneybook.utils.NavigationInfo
import com.kevingt.moneybook.utils.Toaster
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val navigation = NavigationFlow()

    fun createBook(name: String) {
        viewModelScope.launch {
            try {
                bookRepo.createBook(name)
                toaster.showMessage(context.getString(R.string.book_create_success, name))

                val navInfo = NavigationInfo(destination = BookActivity::class)
                navigation.tryEmit(navInfo)
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun handleJoinRequest() {
        if (!bookRepo.hasPendingJoinRequest) return

        viewModelScope.launch {
            try {
                val bookName = bookRepo.handlePendingJoinRequest()
                toaster.showMessage(context.getString(R.string.book_join_success, bookName))

                val navInfo = NavigationInfo(destination = BookActivity::class)
                navigation.tryEmit(navInfo)
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