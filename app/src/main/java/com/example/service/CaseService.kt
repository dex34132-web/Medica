package com.example.service

import com.example.data.MockDemoCases
import com.example.data.room.CaseDao
import com.example.data.room.CaseJsonHelper
import com.example.model.Case
import com.example.model.EscalationStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface CaseService {
    fun getAllCases(): Flow<List<Case>>
    fun getCaseById(id: String): Flow<Case?>
    suspend fun saveCase(case: Case)
    suspend fun deleteCase(id: String)
    suspend fun updateEscalation(caseId: String, status: EscalationStatus, notes: String)
    suspend fun seedInitialDataIfEmpty()
}

class LocalCaseService(
    private val caseDao: CaseDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : CaseService {

    init {
        scope.launch {
            seedInitialDataIfEmpty()
        }
    }

    override suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val count = caseDao.getCaseCount()
        if (count == 0) {
            MockDemoCases.initialCases.forEach { case ->
                caseDao.insertCase(CaseJsonHelper.caseToEntity(case))
            }
        }
    }

    override fun getAllCases(): Flow<List<Case>> {
        return caseDao.getAllCases().map { entities ->
            entities.map { CaseJsonHelper.entityToCase(it) }
        }
    }

    override fun getCaseById(id: String): Flow<Case?> {
        return caseDao.getCaseById(id).map { entity ->
            entity?.let { CaseJsonHelper.entityToCase(it) }
        }
    }

    override suspend fun saveCase(case: Case) = withContext(Dispatchers.IO) {
        caseDao.insertCase(CaseJsonHelper.caseToEntity(case))
    }

    override suspend fun deleteCase(id: String) = withContext(Dispatchers.IO) {
        caseDao.deleteCaseById(id)
    }

    override suspend fun updateEscalation(caseId: String, status: EscalationStatus, notes: String) = withContext(Dispatchers.IO) {
        caseDao.updateEscalation(caseId, status.name, notes)
    }
}
