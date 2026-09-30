package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.Member

/**
 * Member Management UI component for 'হিসাব Pro'
 * Mimics HTML .members-list container with glassmorphic cards for each member.
 */
@Composable
fun MemberManagementSection(
    members: List<Member>,
    onNameChange: (index: Int, name: String) -> Unit,
    onExpenseChange: (index: Int, expense: String) -> Unit,
    onDeleteMember: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("members_container"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val canDelete = members.size > 1
        members.forEachIndexed { index, member ->
            MemberRowItem(
                index = index,
                member = member,
                canDelete = canDelete,
                onNameChange = { newName -> onNameChange(index, newName) },
                onExpenseChange = { newExpense -> onExpenseChange(index, newExpense) },
                onDelete = { onDeleteMember(index) }
            )
        }
    }
}
