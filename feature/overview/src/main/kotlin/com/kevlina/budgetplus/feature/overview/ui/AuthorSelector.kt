package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.DropdownItem
import com.kevlina.budgetplus.core.ui.DropdownMenu
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.rippleClick

@Composable
internal fun AuthorSelector(
    authors: List<User>,
    selectedAuthor: User?,
    setAuthor: (User?) -> Unit,
) {

    var isAuthorPickerShown by rememberSaveable { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .rippleClick { isAuthorPickerShown = true }
            .padding(horizontal = 8.dp)
    ) {

        Icon(
            imageVector = Icons.Rounded.PersonSearch,
            tint = LocalAppColors.current.dark
        )

        Box {

            Text(
                text = selectedAuthor?.name
                    ?: stringResource(id = R.string.overview_author_everyone),
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            )

            DropdownMenu(
                expanded = isAuthorPickerShown,
                onDismissRequest = { isAuthorPickerShown = false }
            ) {

                DropdownItem(
                    name = stringResource(id = R.string.overview_author_everyone),
                ) {
                    isAuthorPickerShown = false
                    setAuthor(null)
                }

                authors.forEach { author ->
                    DropdownItem(
                        name = author.name ?: stringResource(id = R.string.overview_author_anonymous),
                    ) {
                        isAuthorPickerShown = false
                        setAuthor(author)
                    }
                }
            }
        }

        Icon(
            imageVector = Icons.Rounded.ArrowDropDown,
            contentDescription = null,
            tint = LocalAppColors.current.dark
        )
    }
}