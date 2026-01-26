package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.remote.Author
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.toAuthor

/**
 *  Resolve the [Author] by id to avoid showing stale name.
 */
fun UserRepo.resolveAuthor(record: Record): Record {
    val author = record.author?.id?.let(::getUser)?.toAuthor()
    return record.copy(author = author ?: record.author)
}