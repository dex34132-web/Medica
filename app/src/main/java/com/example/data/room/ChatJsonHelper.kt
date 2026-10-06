package com.example.data.room

import com.example.ondevice.models.FlowchartStep
import com.example.ondevice.models.ImageStep
import com.example.ondevice.models.LocalMediaInput
import com.example.ondevice.models.ModalityType
import com.example.ondevice.models.MultimodalActionPlan
import com.example.ondevice.models.VideoStep
import com.example.viewmodel.ChatMessage
import com.example.viewmodel.ChatSession
import org.json.JSONArray
import org.json.JSONObject

object ChatJsonHelper {

    fun toEntity(session: ChatSession): ChatSessionEntity {
        val messagesArray = JSONArray()

        session.messages.forEach { msg ->
            val msgObj = JSONObject().apply {
                put("id", msg.id)
                put("isUser", msg.isUser)
                put("text", msg.text)
                put("thinkingProcess", msg.thinkingProcess ?: "")
                put("timestamp", msg.timestamp)
                put("modelUsed", msg.modelUsed)
                put("latencyMs", msg.latencyMs)

                // Attached media
                val mediaArr = JSONArray()
                msg.attachedMedia.forEach { m ->
                    val mObj = JSONObject().apply {
                        put("id", m.id)
                        put("modality", m.modality.name)
                        put("filename", m.filename)
                        put("featureDescriptor", m.featureDescriptor)
                    }
                    mediaArr.put(mObj)
                }
                put("attachedMedia", mediaArr)

                // Multimodal plan if present
                msg.multimodalPlan?.let { plan ->
                    val planObj = JSONObject().apply {
                        put("conditionRecognized", plan.conditionRecognized)
                        put("aiExplanation", plan.aiExplanation)

                        val textStepsArr = JSONArray()
                        plan.textSteps.forEach { textStepsArr.put(it) }
                        put("textSteps", textStepsArr)

                        val contraArr = JSONArray()
                        plan.contraindications.forEach { contraArr.put(it) }
                        put("contraindications", contraArr)

                        // video steps
                        val vArr = JSONArray()
                        plan.videoSteps.forEach { v ->
                            vArr.put(JSONObject().apply {
                                put("timestamp", v.timestamp)
                                put("phase", v.phase)
                                put("actionDemonstrated", v.actionDemonstrated)
                                put("technicalTip", v.technicalTip)
                            })
                        }
                        put("videoSteps", vArr)

                        // image steps
                        val iArr = JSONArray()
                        plan.imageSteps.forEach { im ->
                            iArr.put(JSONObject().apply {
                                put("stepNumber", im.stepNumber)
                                put("title", im.title)
                                put("anatomicalLandmark", im.anatomicalLandmark)
                                put("visualDescription", im.visualDescription)
                                put("atlasReference", im.atlasReference)
                            })
                        }
                        put("imageSteps", iArr)

                        // flowchart steps
                        val fArr = JSONArray()
                        plan.flowchartSteps.forEach { fc ->
                            fArr.put(JSONObject().apply {
                                put("nodeId", fc.nodeId)
                                put("decisionCondition", fc.decisionCondition)
                                put("branchIfTrue", fc.branchIfTrue)
                                put("branchIfFalse", fc.branchIfFalse)
                            })
                        }
                        put("flowchartSteps", fArr)
                    }
                    put("multimodalPlan", planObj)
                }
            }
            messagesArray.put(msgObj)
        }

        return ChatSessionEntity(
            id = session.id,
            title = session.title,
            createdAt = session.createdAt,
            messagesJson = messagesArray.toString()
        )
    }

    fun toSession(entity: ChatSessionEntity): ChatSession {
        val messages = mutableListOf<ChatMessage>()
        runCatching {
            val array = JSONArray(entity.messagesJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val isUser = obj.optBoolean("isUser", false)
                val text = obj.optString("text", "")
                val thinking = obj.optString("thinkingProcess", "").takeIf { it.isNotBlank() }
                val timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                val modelUsed = obj.optString("modelUsed", "Medica AI")
                val latency = obj.optLong("latencyMs", 0)

                val mediaList = mutableListOf<LocalMediaInput>()
                val mediaArr = obj.optJSONArray("attachedMedia")
                if (mediaArr != null) {
                    for (m in 0 until mediaArr.length()) {
                        val mObj = mediaArr.getJSONObject(m)
                        val modName = mObj.optString("modality", ModalityType.TEXT.name)
                        val modality = runCatching { ModalityType.valueOf(modName) }.getOrDefault(ModalityType.TEXT)
                        mediaList.add(
                            LocalMediaInput(
                                id = mObj.optString("id", ""),
                                modality = modality,
                                filename = mObj.optString("filename", ""),
                                featureDescriptor = mObj.optString("featureDescriptor", "")
                            )
                        )
                    }
                }

                var plan: MultimodalActionPlan? = null
                val planObj = obj.optJSONObject("multimodalPlan")
                if (planObj != null) {
                    val cond = planObj.optString("conditionRecognized", "Emergency Condition")
                    val explanation = planObj.optString("aiExplanation", "")

                    val textSteps = mutableListOf<String>()
                    val tArr = planObj.optJSONArray("textSteps")
                    if (tArr != null) {
                        for (t in 0 until tArr.length()) textSteps.add(tArr.getString(t))
                    }

                    val contra = mutableListOf<String>()
                    val cArr = planObj.optJSONArray("contraindications")
                    if (cArr != null) {
                        for (c in 0 until cArr.length()) contra.add(cArr.getString(c))
                    }

                    val videoSteps = mutableListOf<VideoStep>()
                    val vArr = planObj.optJSONArray("videoSteps")
                    if (vArr != null) {
                        for (v in 0 until vArr.length()) {
                            val vo = vArr.getJSONObject(v)
                            videoSteps.add(
                                VideoStep(
                                    timestamp = vo.optString("timestamp", "00:00"),
                                    phase = vo.optString("phase", "Phase"),
                                    actionDemonstrated = vo.optString("actionDemonstrated", ""),
                                    technicalTip = vo.optString("technicalTip", "")
                                )
                            )
                        }
                    }

                    val imageSteps = mutableListOf<ImageStep>()
                    val iArr = planObj.optJSONArray("imageSteps")
                    if (iArr != null) {
                        for (im in 0 until iArr.length()) {
                            val io = iArr.getJSONObject(im)
                            imageSteps.add(
                                ImageStep(
                                    stepNumber = io.optInt("stepNumber", im + 1),
                                    title = io.optString("title", "Landmark"),
                                    anatomicalLandmark = io.optString("anatomicalLandmark", ""),
                                    visualDescription = io.optString("visualDescription", ""),
                                    atlasReference = io.optString("atlasReference", "Atlas Ref")
                                )
                            )
                        }
                    }

                    val flowchartSteps = mutableListOf<FlowchartStep>()
                    val fArr = planObj.optJSONArray("flowchartSteps")
                    if (fArr != null) {
                        for (f in 0 until fArr.length()) {
                            val fo = fArr.getJSONObject(f)
                            flowchartSteps.add(
                                FlowchartStep(
                                    nodeId = fo.optString("nodeId", "NODE-${f + 1}"),
                                    decisionCondition = fo.optString("decisionCondition", ""),
                                    branchIfTrue = fo.optString("branchIfTrue", ""),
                                    branchIfFalse = fo.optString("branchIfFalse", "")
                                )
                            )
                        }
                    }

                    plan = MultimodalActionPlan(
                        caseId = obj.optString("id", "PLAN"),
                        conditionRecognized = cond,
                        aiExplanation = explanation,
                        textSteps = textSteps,
                        imageSteps = imageSteps,
                        videoSteps = videoSteps,
                        flowchartSteps = flowchartSteps,
                        audioSteps = emptyList(),
                        dosingMatrix = emptyList(),
                        contraindications = contra,
                        retrievedVaultAssets = emptyList(),
                        neuralNetworkLatencyMs = latency,
                        confidenceScore = 0.95f,
                        provenanceHash = "SHA256-VAULT-PERSISTED"
                    )
                }

                messages.add(
                    ChatMessage(
                        id = obj.optString("id", ""),
                        isUser = isUser,
                        text = text,
                        thinkingProcess = thinking,
                        timestamp = timestamp,
                        attachedMedia = mediaList,
                        multimodalPlan = plan,
                        modelUsed = modelUsed,
                        latencyMs = latency
                    )
                )
            }
        }

        return ChatSession(
            id = entity.id,
            title = entity.title,
            messages = messages,
            createdAt = entity.createdAt
        )
    }
}
