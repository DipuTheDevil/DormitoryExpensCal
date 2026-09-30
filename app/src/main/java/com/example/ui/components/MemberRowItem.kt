package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.Member

/**
 * Backward-compatible wrapper delegating to [MemberRow].
 */
@Composable
fun MemberRowItem(
    index: Int,
    member: Member,
    canDelete: Boolean,
    onNameChange: (String) -> Unit,
    onExpenseChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    MemberRow(
        index = index,
        member = member,
        canDelete = canDelete,
        onNameChange = onNameChange,
        onExpenseChange = onExpenseChange,
        onDelete = onDelete,
        modifier = modifier
    )
}
