package com.example.data.repository

import android.content.Context
import com.example.data.local.MessDatabase
import com.example.data.local.dao.CalculationSessionDao
import com.example.data.local.dao.MemberDao
import com.example.data.local.entity.CalculationSessionEntity
import com.example.data.local.entity.MemberEntity
import com.example.data.model.CalculationResult
import com.example.data.model.CalculationSession
import com.example.data.model.Member
import com.example.utils.BengaliFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Clean repository abstraction interface for managing Member data persistence
 * and historical Calculation Session logging.
 */
interface IMessExpenseRepository {
    val membersFlow: Flow<List<Member>>
    val sessionsFlow: Flow<List<CalculationSession>>

    suspend fun saveMembers(members: List<Member>)
    suspend fun insertOrUpdateMember(member: Member, orderIndex: Int)
    suspend fun deleteMember(memberId: String)
    suspend fun clearData()

    suspend fun saveSession(calculation: CalculationResult, members: List<Member>, note: String = ""): CalculationSession
    suspend fun deleteSession(sessionId: String)
    suspend fun clearAllSessions()
}

/**
 * Concrete Repository implementation using Room Database to bridge
 * the ViewModel/UI layer with the local SQLite persistence engine.
 */
class MessExpenseRepository(
    private val memberDao: MemberDao,
    private val sessionDao: CalculationSessionDao
) : IMessExpenseRepository {

    // Secondary constructor accepting Context for seamless ViewModel usage
    constructor(context: Context) : this(
        MessDatabase.getDatabase(context).memberDao(),
        MessDatabase.getDatabase(context).calculationSessionDao()
    )

    companion object {
        val DEFAULT_MEMBERS = listOf(
            Member(name = "", expense = ""),
            Member(name = "", expense = ""),
            Member(name = "", expense = "")
        )
    }

    /**
     * Flow of member entities mapped to domain [Member] models in ascending order.
     */
    override val membersFlow: Flow<List<Member>> = memberDao.getAllMembers()
        .map { entities ->
            if (entities.isEmpty()) {
                DEFAULT_MEMBERS
            } else {
                entities.map { it.toDomain() }
            }
        }

    /**
     * Flow of historical calculation snapshots ordered by latest timestamp.
     */
    override val sessionsFlow: Flow<List<CalculationSession>> = sessionDao.getAllSessions()
        .map { entities ->
            entities.map { it.toDomain() }
        }

    /**
     * Replaces all member rows with the updated list, preserving user order.
     */
    override suspend fun saveMembers(members: List<Member>) {
        val entities = members.mapIndexed { index, member ->
            MemberEntity.fromDomain(member, index)
        }
        memberDao.replaceAll(entities)
    }

    /**
     * Inserts or updates a single member at a specific display order.
     */
    override suspend fun insertOrUpdateMember(member: Member, orderIndex: Int) {
        memberDao.insertMember(MemberEntity.fromDomain(member, orderIndex))
    }

    /**
     * Deletes a member by their unique ID.
     */
    override suspend fun deleteMember(memberId: String) {
        memberDao.deleteMemberById(memberId)
    }

    /**
     * Clears all saved members from the database.
     */
    override suspend fun clearData() {
        memberDao.deleteAllMembers()
    }

    /**
     * Saves a snapshot of the current final settlement calculation with timestamp.
     */
    override suspend fun saveSession(
        calculation: CalculationResult,
        members: List<Member>,
        note: String
    ): CalculationSession {
        val now = System.currentTimeMillis()
        val rawDate = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale("bn", "BD")).format(Date(now))
        val formattedDate = BengaliFormatter.toBengaliDigits(rawDate)

        val session = CalculationSession(
            id = UUID.randomUUID().toString(),
            timestamp = now,
            formattedDate = formattedDate,
            totalMembers = calculation.totalMembers,
            totalExpense = calculation.totalExpense,
            perPersonExpense = calculation.perPersonExpense,
            settlements = calculation.settlements,
            members = members.map { it.copy() },
            note = note
        )

        sessionDao.insertSession(CalculationSessionEntity.fromDomain(session))
        return session
    }

    /**
     * Deletes a single calculation session snapshot.
     */
    override suspend fun deleteSession(sessionId: String) {
        sessionDao.deleteSessionById(sessionId)
    }

    /**
     * Clears all historical calculation session logs.
     */
    override suspend fun clearAllSessions() {
        sessionDao.clearAllSessions()
    }
}
