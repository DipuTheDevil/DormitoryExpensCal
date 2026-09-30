package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Member

/**
 * Room Entity representing a Mess Member and their expense record in SQLite database.
 */
@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val expense: String,
    val orderIndex: Int = 0
) {
    fun toDomain(): Member = Member(
        id = id,
        name = name,
        expense = expense
    )

    companion object {
        fun fromDomain(member: Member, orderIndex: Int): MemberEntity = MemberEntity(
            id = member.id,
            name = member.name,
            expense = member.expense,
            orderIndex = orderIndex
        )
    }
}
