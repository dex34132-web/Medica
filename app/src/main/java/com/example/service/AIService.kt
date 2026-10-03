package com.example.service

import com.example.model.ActionItem
import com.example.model.AnalysisProgress
import com.example.model.Case
import com.example.model.MedicaResult
import com.example.model.UrgencyLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AIService {
    fun runMultimodalAnalysis(case: Case): Flow<AnalysisProgress>
    suspend fun generateResult(case: Case): MedicaResult
}

class MockAIService : AIService {

    override fun runMultimodalAnalysis(case: Case): Flow<AnalysisProgress> = flow {
        emit(
            AnalysisProgress(
                stepIndex = 1,
                stepTitle = "CASE RECEIVED",
                percentage = 0.15f,
                details = "Ingesting ${case.media.size} local media items & responder field context..."
            )
        )
        delay(450)

        emit(
            AnalysisProgress(
                stepIndex = 2,
                stepTitle = "ANALYZING MEDIA",
                percentage = 0.38f,
                details = "Extracting acoustic frequencies, visual edge retractions, and motion vectors on-device..."
            )
        )
        delay(550)

        emit(
            AnalysisProgress(
                stepIndex = 3,
                stepTitle = "SEARCHING MEDICAL VAULT",
                percentage = 0.62f,
                details = "Scanning local emergency protocols, anatomical diagrams, and surgical video indices..."
            )
        )
        delay(500)

        emit(
            AnalysisProgress(
                stepIndex = 4,
                stepTitle = "MATCHING PROTOCOLS",
                percentage = 0.80f,
                details = "Aligning observations with Emergency Assessment Guide v3.2 & TCCC rules..."
            )
        )
        delay(450)

        emit(
            AnalysisProgress(
                stepIndex = 5,
                stepTitle = "CHECKING REFERENCE MATERIAL",
                percentage = 0.92f,
                details = "Resolving differential conditions and cross-referencing contraindications..."
            )
        )
        delay(400)

        emit(
            AnalysisProgress(
                stepIndex = 6,
                stepTitle = "GENERATING DECISION SUPPORT",
                percentage = 1.0f,
                details = "Computing uncertainty bounds and synthesizing immediate action priorities...",
                completed = true
            )
        )
        delay(300)
    }

    override suspend fun generateResult(case: Case): MedicaResult {
        val lowerText = (case.title + " " + case.context + " " + case.media.joinToString { it.filename + " " + it.description }).lowercase()

        return when {
            lowerText.contains("bleed") || lowerText.contains("wound") || lowerText.contains("lacerat") || lowerText.contains("arter") || lowerText.contains("tourniquet") || lowerText.contains("shock") -> {
                MedicaResult(
                    caseId = case.id,
                    urgencyLevel = UrgencyLevel.CRITICAL,
                    triageCode = "RED - IMMEDIATE (MAJOR HEMORRHAGE RISK)",
                    observations = listOf(
                        "Extensive vascular disruption or deep tissue laceration with rapid fluid loss risk.",
                        "Direct pressure or compression required; signs of systemic perfusion decline.",
                        "Pulse rate elevated relative to blood volume; cool pale extremities noted."
                    ),
                    possibleConditions = listOf(
                        "Extremity / Junctional Arterial Hemorrhage",
                        "Decompensated Hypovolemic Shock",
                        "High Risk of Lethal Triad (Hypothermia / Coagulopathy / Acidosis)"
                    ),
                    immediateActions = listOf(
                        ActionItem(1, "Apply Proximal Limb Tourniquet", "Apply CAT/SOFTT 2-3 inches above wound. Tighten until bleeding stops completely.", true),
                        ActionItem(2, "Pack Cavity with Hemostatic Gauze", "For junctional groin/axilla areas, pack tightly and apply 3 min direct pressure.", true),
                        ActionItem(3, "Prevent Hypothermia with Active Insulation", "Wrap with thermal blanket immediately. Keep patient warm.", true),
                        ActionItem(4, "Record Application Timestamp", "Mark 'T=' on patient forehead or tourniquet band.", false)
                    ),
                    warnings = listOf(
                        "Never loosen a field tourniquet without dedicated surgical support.",
                        "Re-evaluate bleeding after any patient movement."
                    ),
                    uncertainty = "Confidence: 91%. Bleeding volume and occult internal extension cannot be quantified without surgical exploration.",
                    confidence = 0.91f,
                    evidenceIds = listOf("VAULT-FC-02", "VAULT-PR-02", "VAULT-DG-02", "VAULT-VD-02", "VAULT-DC-03"),
                    escalationRecommended = true,
                    escalationReason = "Exsanguinating arterial bleeding with high risk of hemorrhagic shock requires immediate ALS surgical transport."
                )
            }
            lowerText.contains("head") || lowerText.contains("concuss") || lowerText.contains("gcs") || lowerText.contains("fall") || lowerText.contains("unconscious") || lowerText.contains("pupil") -> {
                MedicaResult(
                    caseId = case.id,
                    urgencyLevel = UrgencyLevel.URGENT,
                    triageCode = "YELLOW - DELAYED / RAPID ESCALATION CANDIDATE",
                    observations = listOf(
                        "Blunt cranial impact mechanism with potential intracranial shear stresses.",
                        "Reported transient loss of consciousness or amnesia.",
                        "GCS assessment indicated; serial neurological checks essential."
                    ),
                    possibleConditions = listOf(
                        "Traumatic Brain Injury (Mild to Moderate)",
                        "Risk of Expanding Epidural / Subdural Hematoma",
                        "Cervical Spine Contusion / Occult Injury"
                    ),
                    immediateActions = listOf(
                        ActionItem(1, "Maintain Cervical Spine Motion Restriction", "Keep inline stabilization until cervical spine clinically cleared.", true),
                        ActionItem(2, "Serial GCS & Pupil Evaluation", "Record Eye, Verbal, and Motor response every 10-15 minutes.", false),
                        ActionItem(3, "Elevate Head 30 Degrees if Spine Cleared", "Promotes venous drainage and minimizes intracranial pressure rises.", false)
                    ),
                    warnings = listOf(
                        "Beware of 'lucid interval' where patient appears recovered prior to abrupt deterioration.",
                        "Immediate transfer required if vomiting, asymmetric pupils, or GCS drop occurs."
                    ),
                    uncertainty = "Confidence: 84%. Clinical exam alone cannot exclude acute intracranial hemorrhage without CT imaging.",
                    confidence = 0.84f,
                    evidenceIds = listOf("VAULT-FC-03", "VAULT-PR-03", "VAULT-DC-01"),
                    escalationRecommended = true,
                    escalationReason = "Suspected intracranial pathology post-trauma warrants urgent emergency department evaluation."
                )
            }
            lowerText.contains("burn") || lowerText.contains("fire") || lowerText.contains("scald") -> {
                MedicaResult(
                    caseId = case.id,
                    urgencyLevel = UrgencyLevel.URGENT,
                    triageCode = "YELLOW - URGENT (BURN RESUSCITATION)",
                    observations = listOf(
                        "Thermal epidermal/dermal destruction with potential fluid shift.",
                        "Risk of circumferential constriction or inhalational airway compromise if smoke involved."
                    ),
                    possibleConditions = listOf(
                        "Second/Third Degree Thermal Injury",
                        "Systemic Hypovolemia / Burn Shock",
                        "Potential Inhalational Airway Edema"
                    ),
                    immediateActions = listOf(
                        ActionItem(1, "Assess Airway for Soot or Singed Facial Hairs", "Early intubation or high-flow O2 if inhalation suspected.", true),
                        ActionItem(2, "Cover with Clean Dry Sheet", "Avoid cold water or ice; prevent hypothermia.", true),
                        ActionItem(3, "Calculate Parkland Formula", "Estimate TBSA using Rule of Nines.", false)
                    ),
                    warnings = listOf(
                        "Do not apply greasy ointments or peel adherent clothing.",
                        "Burn patients rapidly lose thermal regulation."
                    ),
                    uncertainty = "Confidence: 87%. TBSA depth assessment requires continuous reassessment over first 24 hours.",
                    confidence = 0.87f,
                    evidenceIds = listOf("VAULT-DG-03", "VAULT-DC-01", "VAULT-PR-01"),
                    escalationRecommended = true,
                    escalationReason = "Extensive burn injury requires specialized Burn Center care."
                )
            }
            else -> {
                // Default: Respiratory / Airway assessment pattern
                MedicaResult(
                    caseId = case.id,
                    urgencyLevel = UrgencyLevel.URGENT,
                    triageCode = "YELLOW - DELAYED (AIRWAY PATENCY RISK)",
                    observations = listOf(
                        "Acoustic frequency analysis indicates upper respiratory resistance.",
                        "Visual cues indicate tachypneic compensatory effort with accessory muscle engagement.",
                        "Oxygen saturation indicates borderline hypoxemia on ambient room air."
                    ),
                    possibleConditions = listOf(
                        "Upper Airway Compromise / Partial Obstruction",
                        "Acute Bronchospasm or Laryngeal Edema",
                        "Infectious or Environmental Airway Irritation"
                    ),
                    immediateActions = listOf(
                        ActionItem(1, "Place in Position of Maximum Comfort", "Allow patient to remain upright (tripod position). Do not force supine.", true),
                        ActionItem(2, "Administer Supplemental High-Flow Oxygen", "Titrate via non-rebreather mask to target SpO2 >= 94%.", true),
                        ActionItem(3, "Continuous Stridor & Respiratory Monitoring", "Monitor every 3-5 minutes for signs of fatigue or loss of air movement.", false),
                        ActionItem(4, "Avoid Blind Airway Instrumentation", "Do not perform blind finger sweeps or aggressive probing.", true)
                    ),
                    warnings = listOf(
                        "Sudden cessation of stridor without clinical improvement may indicate complete obstruction.",
                        "Prepare pediatric/adult bag-valve-mask and suction equipment immediately."
                    ),
                    uncertainty = "Confidence: 86%. On-device acoustic matching suggests upper airway narrowing, but etiology (foreign body vs anaphylaxis vs infection) requires clinical history correlation.",
                    confidence = 0.86f,
                    evidenceIds = listOf("VAULT-FC-01", "VAULT-PR-01", "VAULT-DG-01", "VAULT-VD-01", "VAULT-AU-01"),
                    escalationRecommended = true,
                    escalationReason = "High risk of airway compromise warrants immediate Advanced Life Support (ALS) dispatch."
                )
            }
        }
    }
}
