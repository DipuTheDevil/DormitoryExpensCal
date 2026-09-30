package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Member
import com.example.ui.theme.AppTheme
import com.example.ui.theme.DeleteBtnBg
import com.example.ui.theme.DeleteBtnText
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.PrimaryIndigo
import com.example.utils.BengaliFormatter
import com.example.utils.HapticFeedbackManager

/**
 * Reusable 'MemberRow' Compose component adapting dynamically to Light and Dark theme.
 * Includes text fields for member name and expense, along with a delete button.
 */
@Composable
fun MemberRow(
    index: Int,
    name: String,
    expense: String,
    onNameChange: (String) -> Unit,
    onExpenseChange: (String) -> Unit,
    onDelete: () -> Unit,
    canDelete: Boolean,
    modifier: Modifier = Modifier
) {
    val serialNo = BengaliFormatter.toBengaliDigits(index + 1)
    val namePlaceholder = stringResource(R.string.placeholder_member_name, serialNo)
    val expensePlaceholder = stringResource(R.string.placeholder_expense)

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val colors = AppTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (colors.isDark) 4.dp else 1.5.dp,
                shape = RoundedCornerShape(10.dp),
                ambientColor = Color(0x33000000),
                spotColor = Color(0x26000000)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(colors.cardRowBackground)
            .border(1.dp, colors.cardRowBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("member_row_$index")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // 1. Member Name Input Field
            Column(
                modifier = Modifier.weight(1.15f)
            ) {
                Text(
                    text = stringResource(R.string.label_member_name, serialNo),
                    color = colors.textMuted,
                    fontFamily = HindSiliguriFontFamily,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                AdaptiveTextField(
                    value = name,
                    onValueChange = onNameChange,
                    placeholder = namePlaceholder,
                    keyboardType = KeyboardType.Text,
                    testTag = "member_name_input_$index"
                )
            }

            // 2. Member Expense Input Field
            Column(
                modifier = Modifier.weight(0.95f)
            ) {
                Text(
                    text = stringResource(R.string.label_member_expense),
                    color = colors.textMuted,
                    fontFamily = HindSiliguriFontFamily,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                AdaptiveTextField(
                    value = expense,
                    onValueChange = onExpenseChange,
                    placeholder = expensePlaceholder,
                    keyboardType = KeyboardType.Number,
                    testTag = "member_expense_input_$index"
                )
            }

            // 3. Delete Action Button
            if (canDelete) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(DeleteBtnBg)
                        .border(1.dp, Color(0x33EF4444), RoundedCornerShape(6.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = DeleteBtnText)
                        ) {
                            HapticFeedbackManager.performDeleteAction(context, haptic)
                            onDelete()
                        }
                        .testTag("delete_member_button_$index"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        color = DeleteBtnText,
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Convenience overload for `Member` model.
 */
@Composable
fun MemberRow(
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
        name = member.name,
        expense = member.expense,
        onNameChange = onNameChange,
        onExpenseChange = onExpenseChange,
        onDelete = onDelete,
        canDelete = canDelete,
        modifier = modifier
    )
}

@Composable
private fun AdaptiveTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    testTag: String,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val colors = AppTheme.colors

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) colors.inputFocusBorder else colors.inputBorder,
        animationSpec = tween(durationMillis = 180),
        label = "input_border_color"
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .clip(RoundedCornerShape(6.dp))
            .background(colors.inputBackground)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp)
            .testTag(testTag),
        textStyle = TextStyle(
            color = colors.textMain,
            fontFamily = HindSiliguriFontFamily,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Medium
        ),
        singleLine = true,
        cursorBrush = SolidColor(PrimaryIndigo),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = colors.textMuted.copy(alpha = 0.7f),
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 13.5.sp
                    )
                }
                innerTextField()
            }
        }
    )
}
