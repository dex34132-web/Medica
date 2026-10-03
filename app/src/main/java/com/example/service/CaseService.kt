package com.example.service

import com.example.data.room.CaseDao
import com.example.data.room.CaseJsonHelper
import com.example.model.CaseRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface CaseService {
    fun getAllCases(): Flow<List<CaseRecord>>
    suspend fun saveCase(case: CaseRecord)
    suspend fun deleteCase(id: String)
    suspend fun seedInitialCasesIfEmpty()
}

class LocalCaseService(private val caseDao: CaseDao) : CaseService {

    override fun getAllCases(): Flow<List<CaseRecord>> {
        return caseDao.getAllCases().map { list ->
            list.map { CaseJsonHelper.toRecord(it) }
        }
    }

    override suspend fun saveCase(case: CaseRecord) = withContext(Dispatchers.IO) {
        caseDao.insertCase(CaseJsonHelper.toEntity(case))
    }

    override suspend fun deleteCase(id: String) = withContext(Dispatchers.IO) {
        caseDao.deleteCaseById(id)
    }

    override suspend fun seedInitialCasesIfEmpty() = withContext(Dispatchers.IO) {
        if (caseDao.getCaseCount() == 0) {
            val initial = listOf(
                CaseRecord(
                    id = "MED-78901",
                    title = "Male, 54, Chest Pain",
                    demographic = "(M, 54)",
                    timeAgo = "Created 1h ago",
                    status = "Active",
                    notes = "Patient reports sudden onset substernal pressure radiating to left arm. Vitals: BP 142/88, HR 88."
                ),
                CaseRecord(
                    id = "MED-78902",
                    title = "Acute Abdominal Pain",
                    demographic = "(M, 54)",
                    timeAgo = "Created 1h ago",
                    status = "Active",
                    notes = "Severe right lower quadrant tenderness with guarding. Onset 4 hours ago."
                ),
                CaseRecord(
                    id = "MED-78991",
                    title = "Male, 54, Respiratory",
                    demographic = "(F, 65)",
                    timeAgo = "Created 1h ago",
                    status = "Active",
                    notes = "Mild tachypnea with productive cough and wheezing on exertion."
                ),
                CaseRecord(
                    id = "MED-78903",
                    title = "Male, 54, Chest Pain",
                    demographic = "(F, 65)",
                    timeAgo = "Created 1h ago",
                    status = "Active",
                    notes = "Intermittent sharp chest pain aggravated by deep inspiration."
                ),
                CaseRecord(
                    id = "MED-78904",
                    title = "Male, 54, Chest Pain",
                    demographic = "(F, 65)",
                    timeAgo = "Created 1h ago",
                    status = "Pending",
                    notes = "Awaiting troponin lab panel and serial 12-lead ECG."
                ),
                CaseRecord(
                    id = "MED-78905",
                    title = "Male, 54, Chest Pain",
                    demographic = "(F, 65)",
                    timeAgo = "Created 1h ago",
                    status = "Active",
                    notes = "Patient sitting upright, reports dizziness and mild diaphoresis."
                ),
                CaseRecord(
                    id = "MED-78906",
                    title = "Male, 54, Chest Pain",
                    demographic = "(M, 39)",
                    timeAgo = "Created 1h ago",
                    status = "Pending",
                    notes = "Exertional chest discomfort resolving with rest."
                ),
                CaseRecord(
                    id = "MED-78907",
                    title = "Male, 54, Chest Pain",
                    demographic = "(M, 31)",
                    timeAgo = "Created 1h ago",
                    status = "Active",
                    notes = "Atypical chest tightness post heavy lifting."
                ),
                CaseRecord(
                    id = "MED-78908",
                    title = "Male, 54, Chest Pain",
                    demographic = "(M, 31)",
                    timeAgo = "Created 1h ago",
                    status = "Pending",
                    notes = "Musculoskeletal chest wall tenderness upon palpation."
                )
            )
            initial.forEach { c ->
                caseDao.insertCase(CaseJsonHelper.toEntity(c))
            }
        }
    }
}
