package com.example.service

import com.example.data.MockKnowledgeRepository
import com.example.model.KnowledgeAsset
import com.example.model.KnowledgeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface KnowledgeService {
    fun getAllAssets(): Flow<List<KnowledgeAsset>>
    fun getAssetById(id: String): KnowledgeAsset?
    fun searchAssets(query: String, filterType: KnowledgeType?): List<KnowledgeAsset>
    fun getRelatedAssets(assetId: String): List<KnowledgeAsset>
    fun getAssetsByIds(ids: List<String>): List<KnowledgeAsset>
}

class MockKnowledgeService : KnowledgeService {

    private val _assetsFlow = MutableStateFlow(MockKnowledgeRepository.assets)

    override fun getAllAssets(): Flow<List<KnowledgeAsset>> = _assetsFlow.asStateFlow()

    override fun getAssetById(id: String): KnowledgeAsset? {
        return _assetsFlow.value.find { it.id == id }
    }

    override fun searchAssets(query: String, filterType: KnowledgeType?): List<KnowledgeAsset> {
        val q = query.trim().lowercase()
        return _assetsFlow.value.filter { asset ->
            val matchesType = filterType == null || asset.type == filterType
            val matchesQuery = q.isEmpty() ||
                    asset.title.lowercase().contains(q) ||
                    asset.description.lowercase().contains(q) ||
                    asset.category.lowercase().contains(q) ||
                    asset.tags.any { it.lowercase().contains(q) } ||
                    asset.source.lowercase().contains(q)
            matchesType && matchesQuery
        }
    }

    override fun getRelatedAssets(assetId: String): List<KnowledgeAsset> {
        val current = getAssetById(assetId) ?: return emptyList()
        return _assetsFlow.value.filter { it.id in current.relatedAssetIds }
    }

    override fun getAssetsByIds(ids: List<String>): List<KnowledgeAsset> {
        return _assetsFlow.value.filter { it.id in ids }
    }
}
