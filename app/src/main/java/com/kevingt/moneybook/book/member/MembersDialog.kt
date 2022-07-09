package com.kevingt.moneybook.book.member

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kevingt.moneybook.R
import com.kevingt.moneybook.data.remote.User
import com.kevingt.moneybook.ui.AppDialog
import com.kevingt.moneybook.ui.ConfirmDialog

@Composable
fun MembersDialog(
    onDismiss: () -> Unit
) {

    //TODO: We should generate new instance of VM on every call
    val viewModel = hiltViewModel<MembersViewModel>()
    viewModel.loadMembers()

    val members by viewModel.bookMembers.collectAsState()

    var removeMember by remember { mutableStateOf<User?>(null) }

    AppDialog(onDismissRequest = onDismiss) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.size(width = 280.dp, height = 400.dp)
        ) {

            Text(text = stringResource(id = R.string.members_title))

            if (members.isEmpty()) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(members) { member ->
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
            onConfirm = { viewModel.removeMember(member.id) },
            onDismiss = { removeMember = null }
        )
    }
}

@Composable
private fun MemberCard(
    member: User,
    myUserId: String,
    ownerId: String?,
    removeUser: () -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
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
            modifier = Modifier.clip(CircleShape)
        )

        Text(
            text = member.name.orEmpty(),
            modifier = Modifier.weight(1F)
        )

        when {

            member.id == ownerId -> {
                Text(
                    text = stringResource(id = R.string.members_owner_label)
                )
            }

            myUserId == ownerId -> {

                IconButton(onClick = removeUser) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(id = R.string.cta_delete),
                        tint = Color.Black,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MemberCard_Preview() = MemberCard(
    member = User(
        name = "Kevin Chiu",
        photoUrl = ""
    ),
    myUserId = "user01",
    ownerId = "user01",
    removeUser = { }
)