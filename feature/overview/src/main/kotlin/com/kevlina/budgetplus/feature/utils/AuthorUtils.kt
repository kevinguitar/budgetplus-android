package com.kevlina.budgetplus.feature.utils

import com.kevlina.budgetplus.core.data.UserRepo
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.toAuthor

internal fun UserRepo.resolveAuthor(record: Record): Record {
    val author = record.author?.id?.let(::getUser)?.toAuthor()
    return record.copy(author = author ?: record.author)
}