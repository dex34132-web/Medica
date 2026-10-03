package com.example.data

import com.example.model.ActionItem
import com.example.model.Case
import com.example.model.CaseMedia
import com.example.model.CaseStatus
import com.example.model.EscalationStatus
import com.example.model.MediaType
import com.example.model.MedicaResult
import com.example.model.TimelineEvent
import com.example.model.UrgencyLevel

object MockDemoCases {

    val initialCases: List<Case> = listOf(
        // CASE 1: MDC-1042 - Acute Stridor & Respiratory Compromise
        Case(
            id = "MDC-1042",
            title = "Acute Respiratory Distress with Stridor",
            createdAtFormatted = "Today, 14:32",
            timestampMs = System.currentTimeMillis() - 3600_000,
            media = listOf(
                CaseMedia(
                    id = "M-1042-01",
                    type = MediaType.IMAGE,
                    filename = "anterior_neck_retraction.jpg",
                    description = "Patient in tripod posture, visible suprasternal notching",
                    size = "2.4 MB",
                    duration = null,
                    mockPreviewType = "NECK_RETRACTION"
                ),
                CaseMedia(
                    id = "M-1042-02",
                    type = MediaType.AUDIO,
                    filename = "tracheal_auscultation_sample.m4a",
                    description = "High-pitch inspiratory stridor audible without stethoscope",
                    size = "840 KB",
                    duration = "00:34",
                    mockPreviewType = "STRIDOR_AUDIO"
                ),
                CaseMedia(
                    id = "M-1042-03",
                    type = MediaType.VIDEO,
                    filename = "respiratory_effort_20s.mp4",
                    description = "Tachypnea (~28 bpm) and intercostal indrawing",
                    size = "14.2 MB",
                    duration = "00:22",
                    mockPreviewType = "AIRWAY_VIDEO"
                )
            ),
            context = "Adult male approx 45 yo found in workshop. Sudden onset difficulty breathing ~20 mins ago after sudden coughing fit. Mild peripheral cyanosis on nail beds, unable to speak full sentences.",
            status = CaseStatus.COMPLETED,
            escalationStatus = EscalationStatus.RECOMMENDED,
            escalationContact = "Trauma & Resuscitation Center - ALS Team Alpha",
            escalationNotes = "ALS intercept requested. Continuous O2 via NRB mask at 15 L/min.",
            timeline = listOf(
                TimelineEvent("14:32:05", "Case Initialized", "Responder created case MDC-1042 in field"),
                TimelineEvent("14:32:40", "Media Ingested", "3 local media files captured (1 photo, 1 audio, 1 video)"),
                TimelineEvent("14:33:10", "Multimodal Heuristic Analysis", "On-device pipeline completed processing in 1.4s"),
                TimelineEvent("14:33:45", "Triage Decision Generated", "Urgency level flagged as URGENT (YELLOW - DELAYED to RED transition risk)"),
                TimelineEvent("14:34:10", "Escalation Recommended", "High probability of impending airway decompensation")
            ),
            result = MedicaResult(
                caseId = "MDC-1042",
                urgencyLevel = UrgencyLevel.URGENT,
                triageCode = "YELLOW - HIGH RISK (AIRWAY DELAYED/IMMEDIATE)",
                observations = listOf(
                    "Prominent high-pitched inspiratory acoustic frequency (>1200 Hz) consistent with upper airway/laryngeal narrowing.",
                    "Sternal notch retraction and paradoxical abdominal breathing visible on captured video.",
                    "Tachypnea estimated at 28–30 breaths per minute with reduced vocalization capacity.",
                    "Peripheral nail bed duskiness and tachypneic exhaustion risk."
                ),
                possibleConditions = listOf(
                    "Partial Foreign Body Airway Obstruction (Upper Laryngeal)",
                    "Acute Laryngeal Edema / Anaphylactoid Reaction",
                    "Subglottic Stenosis / Vocal Cord Spasm"
                ),
                immediateActions = listOf(
                    ActionItem(
                        priority = 1,
                        title = "Maintain Position of Maximum Comfort",
                        detail = "Keep patient sitting upright in tripod position. DO NOT force patient into a supine position under any circumstances.",
                        isCritical = true
                    ),
                    ActionItem(
                        priority = 2,
                        title = "Deliver High-Flow Oxygen",
                        detail = "Administer 100% humidified oxygen via non-rebreather mask (12–15 L/min) targeting SpO2 >= 94%.",
                        isCritical = true
                    ),
                    ActionItem(
                        priority = 3,
                        title = "Avoid Blind Airway Probing",
                        detail = "Do NOT perform blind finger sweeps or forceful oral pharyngeal manipulation; risk of dislodging partial obstruction deeper.",
                        isCritical = true
                    ),
                    ActionItem(
                        priority = 4,
                        title = "Prepare Emergency Surgical Airway Kit",
                        detail = "Ensure cricothyroidotomy equipment and bag-valve-mask with PEEP valve are at bedside for immediate access.",
                        isCritical = false
                    )
                ),
                warnings = listOf(
                    "Patient is at extreme risk of sudden total airway obstruction if fatigued or agitated.",
                    "Do not administer oral medications or liquids.",
                    "Monitor constantly for sudden loss of breath sounds (silent chest), indicating complete obstruction."
                ),
                uncertainty = "Confidence: 88% based on acoustic waveform match against local vault respiratory acoustics (AU-01) and visual retraction heuristics. Differential between partial mechanical foreign body vs inflammatory subglottic edema requires direct fiberoptic or laryngoscopic visualization.",
                confidence = 0.88f,
                evidenceIds = listOf("VAULT-FC-01", "VAULT-PR-01", "VAULT-DG-01", "VAULT-VD-01", "VAULT-AU-01"),
                escalationRecommended = true,
                escalationReason = "High threat of complete airway occlusion. Immediate Advanced Life Support (ALS) intercept and hospital airway team standby required."
            )
        ),

        // CASE 2: MDC-1043 - Deep Inguinal Laceration & Hemorrhagic Shock Risk
        Case(
            id = "MDC-1043",
            title = "Deep Laceration & Hemorrhagic Shock Risk",
            createdAtFormatted = "Today, 11:15",
            timestampMs = System.currentTimeMillis() - 14400_000,
            media = listOf(
                CaseMedia(
                    id = "M-1043-01",
                    type = MediaType.IMAGE,
                    filename = "proximal_thigh_laceration.jpg",
                    description = "Deep puncture and laceration at proximal femoral triangle",
                    size = "3.1 MB",
                    duration = null,
                    mockPreviewType = "WOUND_IMAGE"
                ),
                CaseMedia(
                    id = "M-1043-02",
                    type = MediaType.AUDIO,
                    filename = "patient_mental_status.m4a",
                    description = "Confused, slow responses, weak voice",
                    size = "520 KB",
                    duration = "00:18",
                    mockPreviewType = "VOICE_SAMPLE"
                )
            ),
            context = "Industrial mishap with machinery blade. Bright red arterial spurting managed by responder with direct pressure, estimated blood loss > 700 mL prior to arrival. Patient pale, diaphoretic, weak radial pulse.",
            status = CaseStatus.ESCALATED,
            escalationStatus = EscalationStatus.CONFIRMED,
            escalationContact = "County Shock Trauma Communications",
            escalationNotes = "Catheter tourniquet applied at 11:18. Dispatching rotary air-ambulance.",
            timeline = listOf(
                TimelineEvent("11:15:20", "Case Created", "Emergency case initiated in field"),
                TimelineEvent("11:16:05", "Media Ingested", "Captured laceration photo and audio sample"),
                TimelineEvent("11:16:30", "Analysis Executed", "Local triage heuristics classified as CRITICAL (RED - IMMEDIATE)"),
                TimelineEvent("11:17:15", "Escalation Confirmed", "Air ambulance requested by responder")
            ),
            result = MedicaResult(
                caseId = "MDC-1043",
                urgencyLevel = UrgencyLevel.CRITICAL,
                triageCode = "RED - IMMEDIATE (EXSANGUINATING HEMORRHAGE)",
                observations = listOf(
                    "Wound geometry consistent with high-velocity laceration crossing femoral triangle.",
                    "Reported pulsatile arterial spurting with estimated blood loss exceeding 700 mL.",
                    "Signs of Class II-III hemorrhagic shock (pallor, diaphoresis, delayed capillary refill > 3s, altered mentation).",
                    "Shock Index calculated >= 1.1 indicating hemodynamic instability."
                ),
                possibleConditions = listOf(
                    "Arterial Transection (Superficial Femoral / Profunda)",
                    "Decompensated Hypovolemic Hemorrhagic Shock",
                    "Lethal Triad Coagulopathy Risk"
                ),
                immediateActions = listOf(
                    ActionItem(
                        priority = 1,
                        title = "Apply Limb Tourniquet Immediately",
                        detail = "Apply CAT/SOFTT tourniquet high and tight on the proximal thigh. Tighten until bleeding and distal pedal pulse cease.",
                        isCritical = true
                    ),
                    ActionItem(
                        priority = 2,
                        title = "Pack Wound Cavity if Junctional Bleed Persists",
                        detail = "If bleeding is too high for tourniquet cuff, pack cavity deeply with hemostatic gauze and hold 3 minutes firm manual pressure.",
                        isCritical = true
                    ),
                    ActionItem(
                        priority = 3,
                        title = "Prevent Hypothermia Immediately",
                        detail = "Wrap patient in insulated thermal blanket. Cold prevents blood clotting and triggers lethal hypothermia-coagulopathy cascade.",
                        isCritical = true
                    )
                ),
                warnings = listOf(
                    "Do NOT remove or loosen tourniquet once applied in the field without surgical capability.",
                    "Mark exact application time clearly on the tourniquet band (T=11:18).",
                    "Prepare for rapid hemodynamic collapse if occult bleeding continues."
                ),
                uncertainty = "Confidence: 94% regarding life-threatening exsanguination risk. Anatomical proximity to femoral canal makes junctional packing likely necessary if tourniquet cannot achieve proximal seal.",
                confidence = 0.94f,
                evidenceIds = listOf("VAULT-FC-02", "VAULT-PR-02", "VAULT-DG-02", "VAULT-VD-02"),
                escalationRecommended = true,
                escalationReason = "Exsanguinating junctional/extremity arterial hemorrhage with hemodynamic compromise requires immediate surgical trauma center transfer."
            )
        ),

        // CASE 3: MDC-1044 - Closed Head Injury with Suspected Concussion
        Case(
            id = "MDC-1044",
            title = "Closed Head Injury & Concussion Assessment",
            createdAtFormatted = "Yesterday, 16:45",
            timestampMs = System.currentTimeMillis() - 86400_000,
            media = listOf(
                CaseMedia(
                    id = "M-1044-01",
                    type = MediaType.IMAGE,
                    filename = "temporal_contusion_photo.jpg",
                    description = "Mild scalp hematoma at right temporal-parietal region",
                    size = "1.8 MB",
                    duration = null,
                    mockPreviewType = "HEAD_CONTUSION"
                )
            ),
            context = "Fall from bicycle without helmet. Transient loss of consciousness reported by bystander for approx 30 seconds. Patient is now alert but exhibits repetitive questioning and mild photophobia.",
            status = CaseStatus.COMPLETED,
            escalationStatus = EscalationStatus.NOT_REQUIRED,
            escalationContact = "Metro Emergency Dispatch / Medical Control",
            escalationNotes = "Patient transported by ground ambulance to Community Hospital ED.",
            timeline = listOf(
                TimelineEvent("16:45:10", "Case Created", "Field assessment started"),
                TimelineEvent("16:46:00", "Analysis Executed", "Classified as MODERATE (GREEN/YELLOW)"),
                TimelineEvent("16:47:30", "Assessment Documented", "GCS 14 verified (Eye 4, Verbal 4, Motor 6)")
            ),
            result = MedicaResult(
                caseId = "MDC-1044",
                urgencyLevel = UrgencyLevel.MODERATE,
                triageCode = "YELLOW - DELAYED (OBSERVATION REQUIRED)",
                observations = listOf(
                    "Glasgow Coma Scale: 14 (E4, V4 [confused], M6).",
                    "Pupils equal, round, and reactive to light (3mm bilaterally).",
                    "Focal soft tissue swelling over right temporal bone without palpable step-off or laceration.",
                    "Amnestic to event with repetitive questioning."
                ),
                possibleConditions = listOf(
                    "Cerebral Concussion / Mild Traumatic Brain Injury",
                    "Risk of Delayed Epidural Hematoma (Middle Meningeal Artery)",
                    "Scalp Contusion / Subgaleal Hematoma"
                ),
                immediateActions = listOf(
                    ActionItem(
                        priority = 1,
                        title = "Perform Serial Neurological Checks",
                        detail = "Re-check GCS and pupil size/reactivity every 15 minutes. Any drop >= 2 points requires immediate emergency transport.",
                        isCritical = false
                    ),
                    ActionItem(
                        priority = 2,
                        title = "Maintain Cervical Spine Caution",
                        detail = "Avoid abrupt neck rotation until full cervical evaluation is performed given ground impact mechanism.",
                        isCritical = false
                    ),
                    ActionItem(
                        priority = 3,
                        title = "Monitor for Cushing's Triad",
                        detail = "Watch for widening pulse pressure, bradycardia, or irregular respirations suggesting intracranial pressure rise.",
                        isCritical = true
                    )
                ),
                warnings = listOf(
                    "Temporal bone trauma carries risk of middle meningeal artery injury with a 'lucid interval' before rapid coma.",
                    "Do NOT allow patient to drive or remain unobserved."
                ),
                uncertainty = "Confidence: 82%. Current neurological status is stable, but intracranial hemorrhage cannot be definitively ruled out without non-contrast head CT.",
                confidence = 0.82f,
                evidenceIds = listOf("VAULT-FC-03", "VAULT-PR-03", "VAULT-DC-01"),
                escalationRecommended = false,
                escalationReason = null
            )
        )
    )
}
