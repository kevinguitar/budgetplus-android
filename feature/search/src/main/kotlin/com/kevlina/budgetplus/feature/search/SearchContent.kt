package com.kevlina.budgetplus.feature.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.ui.Button
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.TextField

@Composable
internal fun SearchContent() {

    val vm = hiltViewModel<SearchViewModel>()

    val records by vm.records.collectAsStateWithLifecycle()
    var query by remember {
        mutableStateOf("")
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Row {
            TextField(
                value = query,
                onValueChange = { query = it },
                title = "",
                modifier = Modifier.weight(1F)
            )

            Button(
                onClick = {
                    vm.search(query)
                }
            ) {

                Text(text = "Search")
            }
        }

        LazyColumn() {

            items(records.orEmpty()) { record ->
                Text(text = record.name)
            }
        }
    }

}