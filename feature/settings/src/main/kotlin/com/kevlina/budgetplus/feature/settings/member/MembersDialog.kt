package com.kevlina.budgetplus.feature.settings.member

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.lottie.PremiumCrown
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppDialog
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.IconButton
import com.kevlina.budgetplus.core.ui.InfiniteCircularProgress
import com.kevlina.budgetplus.core.ui.Text

@Composable
internal fun MembersDialog(
    onDismiss: () -> Unit,
) {

    val viewModel = hiltViewModel<MembersViewModel>()
    viewModel.loadMembers()

    val members by viewModel.bookMembers.collectAsStateWithLifecycle()

    var removeMember by remember { mutableStateOf<User?>(null) }

    AppDialog(onDismissRequest = onDismiss) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text(
                text = stringResource(id = R.string.members_title),
                fontSize = FontSize.Large,
                fontWeight = FontWeight.SemiBold
            )

            if (members.isEmpty()) {
                InfiniteCircularProgress(
                    modifier = Modifier.padding(top = 32.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(members, key = { user -> user.id }) { member ->
                        MemberCard(
                            member = member,
                            myUserId = viewModel.userId,
                            ownerId = viewModel.ownerId,
                            removeUser = { removeMember = member }
                        )
                    }
                }
            }
        }
    }

    val member = removeMember
    if (member != null) {

        ConfirmDialog(
            message = stringResource(
                id = R.string.members_confirm_remove,
                member.name.orEmpty(),
                viewModel.bookName.orEmpty()
            ),
            onConfirm = {
                viewModel.removeMember(member.id)
                removeMember = null
            },
            onDismiss = { removeMember = null }
        )
    }
}

@Composable
private fun MemberCard(
    member: User,
    myUserId: String,
    ownerId: String?,
    removeUser: () -> Unit,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(56.dp)
            .padding(vertical = 4.dp)
    ) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(member.photoUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        if (member.premium == true) {
            PremiumCrown(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp)
            )
        }

        Text(
            text = member.name.orEmpty(),
            modifier = Modifier
                .weight(1F)
                .padding(end = 16.dp)
        )

        when {
            member.id == ownerId -> {
                Text(
                    text = stringResource(id = R.string.members_owner_label),
                    fontSize = FontSize.Small,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            myUserId == ownerId -> {
                IconButton(
                    onClick = removeUser,
                    rippleColor = LocalAppColors.current.dark,
                    size = 40.dp
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(id = R.string.cta_delete),
                        tint = LocalAppColors.current.dark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MemberCard_Preview() = AppTheme {
    MemberCard(
        member = User(
            name = "Kevin Chiu",
            photoUrl = ""
        ),
        myUserId = "user01",
        ownerId = "user01",
        removeUser = { }
    )
}