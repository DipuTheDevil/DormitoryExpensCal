package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.local.entity.MemberEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Member database operations.
 */
@Dao
interface MemberDao {

    @Query("SELECT * FROM members ORDER BY orderIndex ASC")
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<MemberEntity>)

    @Query("DELETE FROM members WHERE id = :id")
    suspend fun deleteMemberById(id: String)

    @Query("DELETE FROM members")
    suspend fun deleteAllMembers()

    @Transaction
    suspend fun replaceAll(members: List<MemberEntity>) {
        deleteAllMembers()
        if (members.isNotEmpty()) {
            insertMembers(members)
        }
    }
}
