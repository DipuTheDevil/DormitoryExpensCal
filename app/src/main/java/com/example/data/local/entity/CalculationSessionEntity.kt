package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CalculationSession
import com.example.data.model.Member
import com.example.data.model.SettlementItem
import com.example.data.model.SettlementStatus
import org.json.JSONArray
import org.json.JSONObject

/**
 * Room database entity storing a historical snapshot of a calculation session.
 */
@Entity(tableName = "calculation_sessions")
data class CalculationSessionEntity(
    @PrimaryKey
    val id: String,
    val timestamp: Long,
    val formattedDate: String,
    val totalMembers: Int,
    val totalExpense: Double,
    val perPersonExpense: Double,
    val settlementsJson: String,
    val membersJson: String,
    val note: String = ""
) {
    fun toDomain(): CalculationSession {
        val settlementList = parseSettlements(settlementsJson)
        val memberList = parseMembers(membersJson)

        return CalculationSession(
            id = id,
            timestamp = timestamp,
            formattedDate = formattedDate,
            totalMembers = totalMembers,
            totalExpense = totalExpense,
            perPersonExpense = perPersonExpense,
            settlements = settlementList,
            members = memberList,
            note = note
        )
    }

    companion object {
        fun fromDomain(session: CalculationSession): CalculationSessionEntity {
            return CalculationSessionEntity(
                id = session.id,
                timestamp = session.timestamp,
                formattedDate = session.formattedDate,
                totalMembers = session.totalMembers,
                totalExpense = session.totalExpense,
                perPersonExpense = session.perPersonExpense,
                settlementsJson = serializeSettlements(session.settlements),
                membersJson = serializeMembers(session.members),
                note = session.note
            )
        }

        private fun serializeSettlements(items: List<SettlementItem>): String {
            val array = JSONArray()
            items.forEach { item ->
                val obj = JSONObject()
                obj.put("memberId", item.memberId)
                obj.put("displayName", item.displayName)
                obj.put("status", item.status.name)
                obj.put("amount", item.amount)
                array.put(obj)
            }
            return array.toString()
        }

        private fun parseSettlements(json: String): List<SettlementItem> {
            return try {
                val array = JSONArray(json)
                val list = mutableListOf<SettlementItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        SettlementItem(
                            memberId = obj.optString("memberId", ""),
                            displayName = obj.optString("displayName", ""),
                            status = SettlementStatus.valueOf(obj.optString("status", SettlementStatus.EVEN.name)),
                            amount = obj.optDouble("amount", 0.0)
                        )
                    )
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }

        private fun serializeMembers(members: List<Member>): String {
            val array = JSONArray()
            members.forEach { m ->
                val obj = JSONObject()
                obj.put("id", m.id)
                obj.put("name", m.name)
                obj.put("expense", m.expense)
                array.put(obj)
            }
            return array.toString()
        }

        private fun parseMembers(json: String): List<Member> {
            return try {
                val array = JSONArray(json)
                val list = mutableListOf<Member>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        Member(
                            id = obj.optString("id", ""),
                            name = obj.optString("name", ""),
                            expense = obj.optString("expense", "")
                        )
                    )
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
