package com.kevingt.moneybook.book

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.kevingt.moneybook.data.remote.BookRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepo: BookRepo,
) : ViewModel() {

    fun handleIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        if (uri.pathSegments.firstOrNull() == "join") {
            bookRepo.setPendingJoinRequest(uri)
        }
    }

}