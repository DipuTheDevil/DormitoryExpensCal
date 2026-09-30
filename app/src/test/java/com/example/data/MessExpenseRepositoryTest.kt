package com.example.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.MessDatabase
import com.example.data.model.Member
import com.example.data.repository.MessExpenseRepository
import com.example.domain.ExpenseCalculator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MessExpenseRepositoryTest {

    private lateinit var database: MessDatabase
    private lateinit var repository: MessExpenseRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            MessDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = MessExpenseRepository(database.memberDao(), database.calculationSessionDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveAndRetrieveMembers() = runTest {
        val members = listOf(
            Member(id = "1", name = "হাসান", expense = "1200"),
            Member(id = "2", name = "রাকিব", expense = "800")
        )
        repository.saveMembers(members)

        val retrieved = repository.membersFlow.first()
        assertEquals(2, retrieved.size)
        assertEquals("হাসান", retrieved[0].name)
        assertEquals("1200", retrieved[0].expense)
        assertEquals("রাকিব", retrieved[1].name)
        assertEquals("800", retrieved[1].expense)
    }

    @Test
    fun deleteMemberById() = runTest {
        val members = listOf(
            Member(id = "1", name = "সদস্য ১", expense = "500"),
            Member(id = "2", name = "সদস্য ২", expense = "700")
        )
        repository.saveMembers(members)
        repository.deleteMember("1")

        val retrieved = repository.membersFlow.first()
        assertEquals(1, retrieved.size)
        assertEquals("সদস্য ২", retrieved[0].name)
    }

    @Test
    fun clearDataEmitsDefaultMembers() = runTest {
        val members = listOf(
            Member(id = "1", name = "সদস্য ১", expense = "500")
        )
        repository.saveMembers(members)
        repository.clearData()

        val retrieved = repository.membersFlow.first()
        assertEquals(MessExpenseRepository.DEFAULT_MEMBERS.size, retrieved.size)
    }

    @Test
    fun saveAndRetrieveCalculationSessionSnapshot() = runTest {
        val members = listOf(
            Member(id = "1", name = "রহিম", expense = "600"),
            Member(id = "2", name = "করিম", expense = "300"),
            Member(id = "3", name = "সুমন", expense = "300")
        )
        val calcResult = ExpenseCalculator.calculate(members)
        val session = repository.saveSession(calcResult, members, note = "টেস্ট বাজার")

        val sessions = repository.sessionsFlow.first()
        assertEquals(1, sessions.size)
        val saved = sessions[0]
        assertEquals(session.id, saved.id)
        assertEquals(3, saved.totalMembers)
        assertEquals(1200.0, saved.totalExpense, 0.001)
        assertEquals(400.0, saved.perPersonExpense, 0.001)
        assertEquals(3, saved.settlements.size)
        assertTrue(saved.formattedDate.isNotEmpty())
    }

    @Test
    fun deleteSessionAndClearAll() = runTest {
        val members = listOf(
            Member(id = "1", name = "রহিম", expense = "600"),
            Member(id = "2", name = "করিম", expense = "600")
        )
        val calcResult = ExpenseCalculator.calculate(members)
        val s1 = repository.saveSession(calcResult, members)
        val s2 = repository.saveSession(calcResult, members)

        var sessions = repository.sessionsFlow.first()
        assertEquals(2, sessions.size)

        repository.deleteSession(s1.id)
        sessions = repository.sessionsFlow.first()
        assertEquals(1, sessions.size)
        assertEquals(s2.id, sessions[0].id)

        repository.clearAllSessions()
        sessions = repository.sessionsFlow.first()
        assertEquals(0, sessions.size)
    }
}
