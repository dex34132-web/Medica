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
        // Real user cases only: No artificial mock cases seeded
    }
}
