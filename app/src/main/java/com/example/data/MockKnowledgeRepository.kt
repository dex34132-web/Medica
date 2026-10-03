package com.example.data

import com.example.model.FlowNode
import com.example.model.FlowchartModel
import com.example.model.KnowledgeAsset
import com.example.model.KnowledgeType
import com.example.model.UrgencyLevel

object MockKnowledgeRepository {

    val assets: List<KnowledgeAsset> = listOf(
        // 1. FLOWCHART - Primary Emergency Airway Assessment
        KnowledgeAsset(
            id = "VAULT-FC-01",
            title = "Respiratory & Airway Assessment Flowchart",
            type = KnowledgeType.FLOWCHART,
            category = "Airway & Breathing",
            source = "Emergency Assessment Guide",
            version = "3.2",
            date = "2026-01-10",
            description = "Step-by-step interactive algorithmic decision pathway for acute dyspnea, upper airway obstruction, and stridor.",
            tags = listOf("Airway", "Stridor", "Triage", "Obstruction", "Dyspnea"),
            local = true,
            content = "Clinical protocol for immediate triage of compromised airway. Follow branch nodes based on audible stridor, respiratory muscle retractions, and SpO2.",
            relatedAssetIds = listOf("VAULT-PR-01", "VAULT-DG-01", "VAULT-VD-01"),
            flowchartData = FlowchartModel(
                startNodeId = "node_airway_start",
                nodes = mapOf(
                    "node_airway_start" to FlowNode(
                        id = "node_airway_start",
                        title = "START: Airway Patency & Immediate Danger",
                        questionOrDetail = "Is there immediate life threat (complete airway obstruction, agonal breathing, or apnea)?",
                        yesNodeId = "node_immediate_danger",
                        noNodeId = "node_stridor_check",
                        outcomeLevel = UrgencyLevel.CRITICAL,
                        actionAdvice = "Perform 10-second visual and auditory scan for chest rise and airflow."
                    ),
                    "node_immediate_danger" to FlowNode(
                        id = "node_immediate_danger",
                        title = "HIGH PRIORITY: Complete Airway Obstruction",
                        questionOrDetail = "Patient cannot vocalize or cough. Initiate immediate foreign body maneuvers (Heimlich / chest thrusts) or surgical airway preparation.",
                        outcomeLevel = UrgencyLevel.CRITICAL,
                        actionAdvice = "Administer 5 back blows followed by 5 abdominal thrusts. Call ALS immediately.",
                        isTerminal = true
                    ),
                    "node_stridor_check" to FlowNode(
                        id = "node_stridor_check",
                        title = "Audible Stridor / Partial Obstruction?",
                        questionOrDetail = "Is there high-pitched inspiratory sound (stridor) or severe sternal/intercostal retraction?",
                        yesNodeId = "node_stridor_action",
                        noNodeId = "node_breathing_effort",
                        outcomeLevel = UrgencyLevel.URGENT,
                        actionAdvice = "Keep patient calm. Do NOT agitate or force into a supine position."
                    ),
                    "node_stridor_action" to FlowNode(
                        id = "node_stridor_action",
                        title = "URGENT: Upper Airway Compromise",
                        questionOrDetail = "Signs indicate laryngeal edema, subglottic stenosis, or partial foreign body. Escalate to Advanced Life Support (ALS).",
                        outcomeLevel = UrgencyLevel.URGENT,
                        actionAdvice = "Deliver high-flow humidified O2 via non-rebreather mask (15 L/min). Avoid blind finger sweeps.",
                        isTerminal = true
                    ),
                    "node_breathing_effort" to FlowNode(
                        id = "node_breathing_effort",
                        title = "Respiratory Rate & SpO2 Stability",
                        questionOrDetail = "Is respiratory rate between 12-24 bpm with SpO2 >= 94% on room air?",
                        yesNodeId = "node_stable_resp",
                        noNodeId = "node_tachypnea_hypoxia",
                        outcomeLevel = UrgencyLevel.MODERATE,
                        actionAdvice = "Count ventilations over 30 seconds. Check pulse oximeter waveform quality."
                    ),
                    "node_stable_resp" to FlowNode(
                        id = "node_stable_resp",
                        title = "STABLE: Continue Standard Observation",
                        questionOrDetail = "Patient maintains adequate oxygenation and ventilation without distress. Continue periodic vital sign monitoring.",
                        outcomeLevel = UrgencyLevel.LOW,
                        actionAdvice = "Re-evaluate vitals every 15 minutes. Document baseline findings.",
                        isTerminal = true
                    ),
                    "node_tachypnea_hypoxia" to FlowNode(
                        id = "node_tachypnea_hypoxia",
                        title = "MODERATE/URGENT: Respiratory Insufficiency",
                        questionOrDetail = "Tachypnea or desaturation present. Administer supplemental oxygen and auscultate for wheezes or rales.",
                        outcomeLevel = UrgencyLevel.URGENT,
                        actionAdvice = "Titrate oxygen via nasal cannula to target SpO2 94-98%. Prepare bronchodilator if bronchospasm suspected.",
                        isTerminal = true
                    )
                )
            )
        ),

        // 2. PROTOCOL - Emergency Assessment Guide v3.2
        KnowledgeAsset(
            id = "VAULT-PR-01",
            title = "Emergency Assessment Guide (Airway & Breathing)",
            type = KnowledgeType.PROTOCOL,
            category = "Airway & Breathing",
            source = "Field Triage & Trauma Protocols",
            version = "3.2",
            date = "2026-02-01",
            description = "Standard operating field protocol for systematic primary and secondary assessment in pre-hospital emergency settings.",
            tags = listOf("Protocol", "Assessment", "Airway", "Triage", "Primary Survey"),
            local = true,
            content = """
                EMERGENCY ASSESSMENT PROTOCOL (EAG v3.2)
                
                1. SCENE SIZE-UP & SAFETY
                - Ensure responder and patient safety before contact.
                - Don appropriate PPE (gloves, N95/eye protection if aerosol generating).
                
                2. PRIMARY SURVEY (X-A-B-C-D-E)
                - X (Exsanguinating Hemorrhage): Control catastrophic external bleeding immediately.
                - A (Airway): Assess patency, vocalization, audible stridor, or secretions.
                - B (Breathing): Assess rate, depth, bilateral expansion, intercostal indrawing.
                - C (Circulation): Radial pulse quality, skin color, capillary refill.
                - D (Disability): Quick AVPU or GCS neurological assessment.
                - E (Exposure): Identify occult injuries while preventing hypothermia.
                
                3. CRITICAL AIRWAY INTERVENTIONS
                - Position of comfort: Allow patient to sit upright; never force supine if struggling.
                - Oxygenation: High-flow non-rebreather mask at 12–15 L/min for SpO2 < 92%.
                - Avoid instrumentation if epiglottitis or upper airway swelling suspected.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-FC-01", "VAULT-VD-01", "VAULT-DG-01")
        ),

        // 3. DIAGRAM - Respiratory Anatomy & Stridor Sites
        KnowledgeAsset(
            id = "VAULT-DG-01",
            title = "Respiratory Anatomy & Obstruction Sites",
            type = KnowledgeType.DIAGRAM,
            category = "Airway & Breathing",
            source = "Anatomical Reference Atlas",
            version = "2.8",
            date = "2025-11-18",
            description = "Cross-sectional anatomical diagram illustrating the supraglottic, glottic, and subglottic landmarks relevant to acute stridor.",
            tags = listOf("Anatomy", "Airway", "Larynx", "Vocal Cords", "Diagram"),
            local = true,
            content = """
                ANATOMICAL REFERENCE: LARYNGEAL & SUBGLOTTIC AIRWAY
                
                1. Supraglottic Zone:
                   - Epiglottis, aryepiglottic folds, false vocal cords.
                   - Common site of rapid inflammatory edema in epiglottitis or severe anaphylaxis.
                
                2. Glottic Zone:
                   - True vocal cords, rima glottidis.
                   - Narrowest portion of the adult upper airway.
                
                3. Subglottic Zone:
                   - Cricoid cartilage ring (complete cartilage ring).
                   - Narrowest portion of the pediatric airway; site of subglottic stenosis and croup.
                
                Clinical Acoustic Correlation:
                - Inspiratory Stridor: Typically supraglottic or glottic restriction.
                - Biphasic Stridor: Subglottic or fixed tracheal narrowing.
                - Expiratory Wheeze: Intrathoracic lower airway constriction (bronchiolar).
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-PR-01", "VAULT-FC-01")
        ),

        // 4. VIDEO - Assessment Demonstration
        KnowledgeAsset(
            id = "VAULT-VD-01",
            title = "Airway Assessment Demonstration",
            type = KnowledgeType.VIDEO,
            category = "Airway & Breathing",
            source = "Tactical Field Medicine Training",
            version = "4.0",
            date = "2025-09-14",
            description = "Instructional reference video demonstrating physical signs of acute upper airway obstruction, accessory muscle usage, and stridor auscultation.",
            tags = listOf("Video", "Demonstration", "Auscultation", "Stridor", "Clinical Skill"),
            local = true,
            videoTimestamp = "04:32–05:10",
            duration = "08:24",
            content = """
                REFERENCE VIDEO PLAYBOOK
                Timestamp Highlight: 04:32 – 05:10
                Segment Title: Differentiating Inspiratory Stridor vs Laryngeal Spasm
                
                Summary of key cues shown in segment:
                - Suprasternal notch retraction visible during inspiration.
                - Tracheal tugging correlated with audible high-pitched tone.
                - Proper positioning of stethoscope diaphragm at the anterior neck (cricothyroid membrane) for clear acoustic capture.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-FC-01", "VAULT-PR-01", "VAULT-AU-01")
        ),

        // 5. FLOWCHART - Massive Hemorrhage Control Flowchart
        KnowledgeAsset(
            id = "VAULT-FC-02",
            title = "Massive Hemorrhage Control Flowchart",
            type = KnowledgeType.FLOWCHART,
            category = "Circulation & Bleeding",
            source = "Tactical Combat Casualty Care (TCCC)",
            version = "5.1",
            date = "2026-01-20",
            description = "Rapid decision tree for managing life-threatening limb vs junctional hemorrhage in the field.",
            tags = listOf("Bleeding", "Tourniquet", "Hemorrhage", "TCCC", "Shock"),
            local = true,
            content = "Algorithmic decision tree for arterial bleeding, limb tourniquets, and junctional wound packing.",
            relatedAssetIds = listOf("VAULT-PR-02", "VAULT-DG-02", "VAULT-VD-02"),
            flowchartData = FlowchartModel(
                startNodeId = "node_hem_start",
                nodes = mapOf(
                    "node_hem_start" to FlowNode(
                        id = "node_hem_start",
                        title = "START: Is Bleeding Life-Threatening?",
                        questionOrDetail = "Is there pulsatile arterial spurting, pooling blood > half-liter, or soaked clothing?",
                        yesNodeId = "node_limb_or_junctional",
                        noNodeId = "node_minor_bleeding",
                        outcomeLevel = UrgencyLevel.CRITICAL,
                        actionAdvice = "Apply direct manual pressure immediately while preparing equipment."
                    ),
                    "node_limb_or_junctional" to FlowNode(
                        id = "node_limb_or_junctional",
                        title = "Limb or Junctional Anatomy?",
                        questionOrDetail = "Is the bleeding source located on an extremity (arm/leg) amenable to a tourniquet?",
                        yesNodeId = "node_tourniquet_action",
                        noNodeId = "node_wound_packing_action",
                        outcomeLevel = UrgencyLevel.CRITICAL,
                        actionAdvice = "Identify wound location without delay."
                    ),
                    "node_tourniquet_action" to FlowNode(
                        id = "node_tourniquet_action",
                        title = "CRITICAL: Apply Limb Tourniquet",
                        questionOrDetail = "Apply limb tourniquet 2-3 inches proximal to wound (or high & tight if source unclear). Tighten until bleeding stops and distal pulse vanishes.",
                        outcomeLevel = UrgencyLevel.CRITICAL,
                        actionAdvice = "Record exact application time (e.g., T=14:32) on patient forehead or tourniquet band. Do not cover tourniquet.",
                        isTerminal = true
                    ),
                    "node_wound_packing_action" to FlowNode(
                        id = "node_wound_packing_action",
                        title = "CRITICAL: Wound Packing & Hemostatic Agent",
                        questionOrDetail = "Junctional wound (groin, axilla, neck). Pack cavity deeply with hemostatic gauze directly against the bleeding vessel.",
                        outcomeLevel = UrgencyLevel.CRITICAL,
                        actionAdvice = "Hold uninterrupted direct pressure for at least 3 minutes after packing, then apply pressure bandage.",
                        isTerminal = true
                    ),
                    "node_minor_bleeding" to FlowNode(
                        id = "node_minor_bleeding",
                        title = "MODERATE: Direct Pressure & Compression Bandage",
                        questionOrDetail = "Venous or capillary oozing without hemodynamic compromise. Clean wound and apply sterile pressure dressing.",
                        outcomeLevel = UrgencyLevel.MODERATE,
                        actionAdvice = "Check distal pulses before and after bandage application to ensure no distal ischemia.",
                        isTerminal = true
                    )
                )
            )
        ),

        // 6. PROTOCOL - Massive Hemorrhage & Shock Protocol
        KnowledgeAsset(
            id = "VAULT-PR-02",
            title = "Massive Hemorrhage & Hypovolemic Shock Protocol",
            type = KnowledgeType.PROTOCOL,
            category = "Circulation & Bleeding",
            source = "Trauma Management Standards",
            version = "4.2",
            date = "2025-12-05",
            description = "Clinical criteria and procedural steps for hemostasis, tourniquet placement, and prevention of the lethal triad.",
            tags = listOf("Hemorrhage", "Shock", "Tourniquet", "Protocol", "Hypothermia"),
            local = true,
            content = """
                MASSIVE HEMORRHAGE & SHOCK PROTOCOL
                
                1. Immediate Hemostasis:
                   - Apply CAT/SOFTT tourniquet immediately for catastrophic limb hemorrhage.
                   - If bleeding persists through first tourniquet, apply a second tourniquet proximal to the first.
                   - For junctional wounds (axilla, groin), pack with hemostatic gauze and maintain 3 minutes firm manual pressure.
                
                2. Prevent Lethal Triad (Hypothermia, Acidosis, Coagulopathy):
                   - Keep patient warm with thermal hypothermia blanket immediately.
                   - Avoid aggressive cold crystalloid fluid boluses.
                
                3. Shock Index (SI) Monitoring:
                   - SI = Heart Rate / Systolic Blood Pressure.
                   - SI > 0.9 indicates occult compensated shock.
                   - SI > 1.3 indicates severe critical decompensated shock.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-FC-02", "VAULT-DG-02", "VAULT-VD-02")
        ),

        // 7. DIAGRAM - Arterial Pressure Points & Tourniquet Zones
        KnowledgeAsset(
            id = "VAULT-DG-02",
            title = "Arterial Pressure Points & Tourniquet Placement",
            type = KnowledgeType.DIAGRAM,
            category = "Circulation & Bleeding",
            source = "Trauma Anatomy Atlas",
            version = "3.0",
            date = "2025-10-22",
            description = "Visual anatomical schematic of femoral, brachial, and axillary pressure zones with safety margins for tourniquet application.",
            tags = listOf("Anatomy", "Artery", "Tourniquet", "Diagram", "Vascular"),
            local = true,
            content = """
                ANATOMICAL TARGETS: MAJOR PERIPHERAL ARTERIES
                
                1. Brachial Artery: Runs along medial aspect of upper arm. Compressive target against humerus.
                2. Femoral Artery: Exits beneath inguinal ligament at mid-inguinal point. Compress firmly against femoral head.
                3. Tourniquet Placement Rules:
                   - Placement over clothing is acceptable only in rapid-threat scenarios; replace/adjust to skin 2-3 inches above wound when safe.
                   - Never place tourniquet directly over a joint (elbow, knee).
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-PR-02", "VAULT-FC-02")
        ),

        // 8. VIDEO - Wound Packing & Tourniquet Technique
        KnowledgeAsset(
            id = "VAULT-VD-02",
            title = "Combat Gauze Packing & Tourniquet Application",
            type = KnowledgeType.VIDEO,
            category = "Circulation & Bleeding",
            source = "Pre-Hospital Trauma Care",
            version = "3.5",
            date = "2025-08-30",
            description = "Instructional video demonstration of wound cavity exploration, tactile packing, and windlass locking mechanism.",
            tags = listOf("Video", "Tourniquet", "Wound Packing", "Hemorrhage", "Training"),
            local = true,
            videoTimestamp = "02:15–03:45",
            duration = "07:15",
            content = """
                INSTRUCTIONAL HIGHLIGHT
                Timestamp: 02:15 – 03:45
                Topic: Achieving Hemostatic Occlusion in High-Volume Inguinal Lacerations
                
                Step Checklist:
                1. Clear clots with quick finger sweep.
                2. Feed gauze continuously into wound bottom without letting off backpressure.
                3. Secure with Israeli emergency pressure bandage.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-PR-02", "VAULT-FC-02")
        ),

        // 9. FLOWCHART - Neurological & Head Trauma Triage
        KnowledgeAsset(
            id = "VAULT-FC-03",
            title = "Acute Head Injury & GCS Assessment Flowchart",
            type = KnowledgeType.FLOWCHART,
            category = "Neurological",
            source = "Trauma Neurosurgery Standards",
            version = "2.9",
            date = "2026-01-15",
            description = "Algorithmic triage flowchart for head trauma, assessing Glasgow Coma Scale, pupil asymmetry, and signs of herniation.",
            tags = listOf("Head Trauma", "GCS", "Concussion", "Pupils", "Flowchart"),
            local = true,
            content = "Stepwise neurological evaluation for suspected traumatic brain injury (TBI) and spinal precautions.",
            relatedAssetIds = listOf("VAULT-PR-03", "VAULT-DG-03"),
            flowchartData = FlowchartModel(
                startNodeId = "node_gcs_start",
                nodes = mapOf(
                    "node_gcs_start" to FlowNode(
                        id = "node_gcs_start",
                        title = "START: Glasgow Coma Scale (GCS) Assessment",
                        questionOrDetail = "Is patient GCS score <= 8 (severe TBI) or has pupil asymmetry (> 1mm difference with sluggish response)?",
                        yesNodeId = "node_severe_tbi",
                        noNodeId = "node_moderate_gcs",
                        outcomeLevel = UrgencyLevel.CRITICAL,
                        actionAdvice = "Check Eye (1-4), Verbal (1-5), and Motor (1-6) components."
                    ),
                    "node_severe_tbi" to FlowNode(
                        id = "node_severe_tbi",
                        title = "CRITICAL: Severe TBI & Impending Herniation",
                        questionOrDetail = "GCS <= 8 mandates airway protection (endotracheal or supraglottic) and immediate trauma center neurosurgical transport.",
                        outcomeLevel = UrgencyLevel.CRITICAL,
                        actionAdvice = "Maintain SpO2 > 95%, avoid hypoxia and hypotension (keep SBP > 100 mmHg). Immobilize cervical spine.",
                        isTerminal = true
                    ),
                    "node_moderate_gcs" to FlowNode(
                        id = "node_moderate_gcs",
                        title = "GCS 9-13 or Post-Traumatic Amnesia?",
                        questionOrDetail = "Does patient demonstrate confusion, repeated questions, nausea/vomiting, or loss of consciousness > 1 minute?",
                        yesNodeId = "node_moderate_tbi_action",
                        noNodeId = "node_mild_concussion",
                        outcomeLevel = UrgencyLevel.URGENT,
                        actionAdvice = "Perform serial neurological checks every 10 minutes to detect progressive deterioration."
                    ),
                    "node_moderate_tbi_action" to FlowNode(
                        id = "node_moderate_tbi_action",
                        title = "URGENT: Moderate TBI / Intracranial Pathology Risk",
                        questionOrDetail = "High risk for epidural or subdural hematoma expansion. High-priority transfer for urgent non-contrast head CT.",
                        outcomeLevel = UrgencyLevel.URGENT,
                        actionAdvice = "Keep head of bed elevated 30 degrees if spine cleared. Escalate transport priority.",
                        isTerminal = true
                    ),
                    "node_mild_concussion" to FlowNode(
                        id = "node_mild_concussion",
                        title = "MODERATE/LOW: Mild Concussion / Contusion",
                        questionOrDetail = "Patient alert (GCS 14-15), no focal neurological deficit. Monitor for red flags (worsening headache, vomiting, seizures).",
                        outcomeLevel = UrgencyLevel.MODERATE,
                        actionAdvice = "Provide cognitive rest. Do not leave patient unmonitored for the next 6 hours.",
                        isTerminal = true
                    )
                )
            )
        ),

        // 10. PROTOCOL - Traumatic Brain Injury Protocol
        KnowledgeAsset(
            id = "VAULT-PR-03",
            title = "Traumatic Brain Injury & Spinal Motion Restriction Protocol",
            type = KnowledgeType.PROTOCOL,
            category = "Neurological",
            source = "Field Neurotrauma Protocol",
            version = "3.1",
            date = "2025-11-01",
            description = "Pre-hospital clinical guideline for preventing secondary brain injury by avoiding hypoxia, hypotension, and hyperventilation.",
            tags = listOf("TBI", "Spine", "C-Spine", "Protocol", "Neurology"),
            local = true,
            content = """
                FIELD TBI MANAGEMENT RULES (The "Avoid the H's" Rule)
                
                1. Avoid Hypoxia: Even a single SpO2 episode < 90% doubles mortality in severe TBI. Maintain SpO2 >= 95%.
                2. Avoid Hypotension: Maintain Systolic BP >= 100 mmHg (110 mmHg for age 50-69).
                3. Avoid Hyperventilation: Do not hyperventilate unless active cerebral herniation signs present (bilateral fixed/dilated pupils or decerebrate posturing).
                4. Spinal Immobilization: Maintain inline cervical spine stabilization if blunt trauma mechanism indicates risk.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-FC-03", "VAULT-DG-03")
        ),

        // 11. DIAGRAM - Dermatome & Burn TBSA Rule of Nines
        KnowledgeAsset(
            id = "VAULT-DG-03",
            title = "Burn TBSA Rule of Nines & Fluid Replacement Chart",
            type = KnowledgeType.DIAGRAM,
            category = "Trauma & Spine",
            source = "International Burn Care Consortium",
            version = "2.4",
            date = "2025-07-12",
            description = "Adult and pediatric Total Body Surface Area (TBSA) percentage allocation and modified Parkland formula calculation chart.",
            tags = listOf("Burn", "TBSA", "Parkland", "Diagram", "Trauma"),
            local = true,
            content = """
                BURN SURFACE AREA REFERENCE (RULE OF NINES)
                
                Adult TBSA:
                - Head & Neck: 9%
                - Anterior Trunk: 18% (Chest 9%, Abdomen 9%)
                - Posterior Trunk: 18% (Upper back 9%, Lower back 9%)
                - Each Arm: 9%
                - Each Leg: 18% (Anterior 9%, Posterior 9%)
                - Perineum: 1%
                
                Parkland Formula (First 24 Hours):
                - Fluid Volume = 4 mL x Weight (kg) x % TBSA (2nd & 3rd degree burns)
                - Give first half over 8 hours from time of injury.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-PR-04")
        ),

        // 12. PROTOCOL - Anaphylaxis Field Protocol
        KnowledgeAsset(
            id = "VAULT-PR-04",
            title = "Anaphylaxis Emergency Treatment Protocol",
            type = KnowledgeType.PROTOCOL,
            category = "Allergy & Tox",
            source = "Emergency Allergy Consensus",
            version = "3.4",
            date = "2026-02-12",
            description = "Dosage guidelines, intramuscular epinephrine delivery, airway vigilance, and refractory hypotension management.",
            tags = listOf("Anaphylaxis", "Epinephrine", "Allergy", "Protocol", "Shock"),
            local = true,
            content = """
                ANAPHYLAXIS CLINICAL ALGORITHM
                
                Criteria: Acute onset of skin/mucosal symptoms (hives, pruritus, angioedema) PLUS respiratory distress OR hypotension.
                
                1. First-Line Drug:
                   - Epinephrine 1:1,000 (1 mg/mL) IM into anterolateral thigh immediately.
                   - Adult dose: 0.3 mg to 0.5 mg IM.
                   - Pediatric dose: 0.01 mg/kg (max 0.3 mg) IM.
                
                2. Repeat: If no improvement after 5 minutes, repeat IM epinephrine dose.
                
                3. Positioning: Place patient supine with legs elevated (unless airway compromised). Fatal cardiovascular collapse can occur if patient sits up abruptly.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-PR-01", "VAULT-FC-01")
        ),

        // 13. AUDIO - Auscultation Abnormalities Guide
        KnowledgeAsset(
            id = "VAULT-AU-01",
            title = "Acoustic Auscultation Reference: Stridor, Wheezing, Crackles",
            type = KnowledgeType.AUDIO,
            category = "Airway & Breathing",
            source = "Clinical Auscultation Library",
            version = "2.1",
            date = "2025-06-20",
            description = "High-fidelity audio recordings comparing inspiratory stridor, polyphonic wheezing, and coarse crepitations.",
            tags = listOf("Audio", "Auscultation", "Breath Sounds", "Stridor", "Wheeze"),
            local = true,
            duration = "04:15",
            content = """
                ACOUSTIC PHONOGRAM REFERENCE
                
                Sample 1 (00:00 - 01:20): High-pitch inspiratory stridor (tracheal narrowing).
                Sample 2 (01:21 - 02:45): Diffuse expiratory wheeze in bronchospasm.
                Sample 3 (02:46 - 04:15): Bilateral basilar crackles in pulmonary edema.
                
                Key Field Differentiation:
                Stridor is loudest over the neck and diminishes over the lung periphery. Wheezing is loudest over the chest fields and rarely heard at the larynx.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-VD-01", "VAULT-PR-01")
        ),

        // 14. DOCUMENT - Field Triage Decision Scheme 2026
        KnowledgeAsset(
            id = "VAULT-DC-01",
            title = "Field Triage Decision Scheme 2026",
            type = KnowledgeType.DOCUMENT,
            category = "Triage Protocols",
            source = "National Triage Guideline Committee",
            version = "6.0",
            date = "2026-01-05",
            description = "Four-step criteria for identifying critically injured patients requiring immediate Level 1 or 2 trauma center transport.",
            tags = listOf("Triage", "Field Scheme", "Trauma", "Criteria", "Transport"),
            local = true,
            content = """
                FIELD TRIAGE STEPWISE DECISION SCHEME
                
                Step 1: Physiologic Criteria
                - GCS <= 13
                - Systolic BP < 90 mmHg
                - Respiratory rate < 10 or > 29 breaths/min (< 20 in infants)
                -> Transport to highest-level trauma center.
                
                Step 2: Anatomic Criteria
                - Penetrating injuries to head, neck, torso, or proximal extremities.
                - Chest wall instability or flail chest.
                - Two or more proximal long-bone fractures.
                - Crushed, degloved, mangled, or pulseless extremity.
                - Amputation proximal to wrist or ankle.
                - Pelvic fractures.
                - Open or depressed skull fracture.
                - Paralysis.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-PR-01", "VAULT-PR-02", "VAULT-FC-02")
        ),

        // 15. IMAGE - Anatomical Reference: Cricoid & Vocal Cords
        KnowledgeAsset(
            id = "VAULT-IM-01",
            title = "Anatomical Reference: Laryngeal Framework",
            type = KnowledgeType.IMAGE,
            category = "Airway & Breathing",
            source = "Surgical Airway Atlas",
            version = "2.0",
            date = "2025-05-18",
            description = "High-contrast clinical illustration identifying thyroid notch, cricothyroid membrane, and cricoid cartilage for rapid palpatory identification.",
            tags = listOf("Image", "Anatomy", "Cricothyroid", "Airway", "Reference"),
            local = true,
            content = """
                RAPID PALPATORY LANDMARKS FOR CRICOTHYROIDOTOMY
                
                1. Thyroid Cartilage (Adam's Apple): Slide index finger down from chin to find prominent thyroid notch.
                2. Cricothyroid Membrane: 1-2 cm below thyroid notch, feel the soft depression between thyroid cartilage and cricoid ring.
                3. Cricoid Ring: Firm transverse ridge immediately below the membrane.
                4. Incision Zone: Transverse puncture directly across the center of the cricothyroid membrane.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-DG-01", "VAULT-VD-01")
        ),

        // 16. PROTOCOL - Pediatric Resuscitation & Broselow Reference
        KnowledgeAsset(
            id = "VAULT-PR-05",
            title = "Pediatric Emergency Assessment & Weight-Based Protocol",
            type = KnowledgeType.PROTOCOL,
            category = "Pediatric",
            source = "Pediatric Advanced Life Support (PALS)",
            version = "4.0",
            date = "2025-10-10",
            description = "Pediatric assessment triangle (appearance, work of breathing, circulation to skin) and weight-based dosing quick reference.",
            tags = listOf("Pediatric", "PALS", "Broselow", "Vitals", "Protocol"),
            local = true,
            content = """
                PEDIATRIC ASSESSMENT TRIANGLE (PAT)
                
                1. Appearance (Tone, Interactivity, Consolability, Look/gaze, Speech/cry - TICLS).
                2. Work of Breathing (Abnormal airway sounds, retractions, flaring, grunting).
                3. Circulation to Skin (Pallor, mottling, cyanosis).
                
                Pediatric Normal Vital Signs:
                - Infant (<1 yr): HR 100-160, RR 30-60, SBP > 60
                - Toddler (1-3 yr): HR 90-150, RR 24-40, SBP > 70
                - Preschool (3-5 yr): HR 80-140, RR 22-34, SBP > 75
                - School-age (6-12 yr): HR 70-120, RR 18-30, SBP > 80
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-PR-01")
        ),

        // 17. VIDEO - Needle Thoracostomy & Decompression
        KnowledgeAsset(
            id = "VAULT-VD-03",
            title = "Tension Pneumothorax Needle Decompression",
            type = KnowledgeType.VIDEO,
            category = "Airway & Breathing",
            source = "Trauma Surgery Review",
            version = "3.0",
            date = "2025-04-10",
            description = "Visual demonstration of second intercostal space mid-clavicular line vs fifth intercostal space anterior axillary line decompression.",
            tags = listOf("Video", "Pneumothorax", "Decompression", "Chest", "Trauma"),
            local = true,
            videoTimestamp = "01:45–02:50",
            duration = "05:50",
            content = """
                PROCEDURAL VIDEO PROTOCOL
                Timestamp: 01:45 - 02:50
                Topic: Identifying the 5th Intercostal Space Anterior Axillary Line
                
                Key points:
                - The lateral site (5th ICS anterior axillary line) has lower failure rate than 2nd ICS MCL due to chest wall thickness.
                - Use a minimum 3.25 inch (8 cm) 10- or 14-gauge catheter over needle.
                - Listen for audible rush of air upon pleural entry.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-PR-01", "VAULT-FC-01")
        ),

        // 18. DOCUMENT - Chemical & Toxic Inhalation Guide
        KnowledgeAsset(
            id = "VAULT-DC-02",
            title = "Hazmat & Toxic Inhalation Field Reference",
            type = KnowledgeType.DOCUMENT,
            category = "Allergy & Tox",
            source = "Chemical Emergency Field Standards",
            version = "2.2",
            date = "2025-03-15",
            description = "Identification of irritant gases (chlorine, ammonia, phosgene) causing delayed pulmonary edema vs immediate upper airway spasm.",
            tags = listOf("Hazmat", "Inhalation", "Toxicology", "Chemical", "Document"),
            local = true,
            content = """
                TOXIC INHALATION SYNDROMES
                
                High Water Solubility (Ammonia, HCl): Immediate onset of severe mucous membrane irritation, upper airway stridor, and coughing. Patient flees scene quickly.
                
                Low Water Solubility (Phosgene, NO2): Minimal early irritation; severe delayed non-cardiogenic pulmonary edema develops 6-24 hours post-exposure.
                
                Immediate Treatment: Move upwind, decontamination, 100% O2, continuous airway vigilance.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-PR-01")
        ),

        // 19. DIAGRAM - Pelvic Binder Placement Anatomy
        KnowledgeAsset(
            id = "VAULT-DG-04",
            title = "Pelvic Circumferential Compression & Anatomy",
            type = KnowledgeType.DIAGRAM,
            category = "Trauma & Spine",
            source = "Orthopedic Trauma Foundation",
            version = "2.1",
            date = "2025-08-01",
            description = "Precise placement line centered over greater trochanters to reduce open-book pelvic fracture bleeding.",
            tags = listOf("Pelvis", "Binder", "Anatomy", "Diagram", "Shock"),
            local = true,
            content = """
                PELVIC BINDER PLACEMENT RULES
                
                Crucial Landmark: Greater trochanters of the femurs (NOT the iliac crests).
                Placing the binder high around the abdomen causes pain and fails to reduce pelvic volume.
                Ensure internal rotation of lower extremities and secure feet together to assist pelvic ring reduction.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-PR-02")
        ),

        // 20. PROTOCOL - Hypothermia & Environmental Exposure
        KnowledgeAsset(
            id = "VAULT-PR-06",
            title = "Accidental Hypothermia & Cold Injury Protocol",
            type = KnowledgeType.PROTOCOL,
            category = "Environmental",
            source = "Wilderness Medical Society",
            version = "3.2",
            date = "2025-12-18",
            description = "Swiss Staging Model for hypothermia (Stage I to IV), gentle handling rules, and insulation strategies.",
            tags = listOf("Hypothermia", "Cold", "Environmental", "Protocol", "Rewarming"),
            local = true,
            content = """
                SWISS HYPOTHERMIA STAGING MODEL
                
                Stage I (Mild): Conscious, shivering. Core temp 32-35°C. Rewarm actively with dry clothing, warm sweet drinks.
                Stage II (Moderate): Impaired consciousness, no shivering. Core temp 28-32°C. Handle extremely gently (rough movement may trigger ventricular fibrillation).
                Stage III (Severe): Unconscious, vital signs present. Core temp 24-28°C. Active thoracic warming, ALS airway management.
                Stage IV (Apparent Death): No vitals. Core temp < 24°C. "Not dead until warm and dead." Prolonged CPR indicated.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-PR-01", "VAULT-DC-01")
        ),

        // 21. AUDIO - Emergency SITREP & MIST Handover Audio
        KnowledgeAsset(
            id = "VAULT-AU-02",
            title = "MIST Report Emergency Radio Transmission Format",
            type = KnowledgeType.AUDIO,
            category = "Triage Protocols",
            source = "Paramedic Communications Handbook",
            version = "2.0",
            date = "2025-07-28",
            description = "Standardized audio example of crisp 30-second pre-arrival radio notification using the MIST format.",
            tags = listOf("Radio", "Handover", "MIST", "Communications", "Audio"),
            local = true,
            duration = "02:30",
            content = """
                MIST RADIO TRANSMISSION TEMPLATE
                
                M - Mechanism of injury or medical complaint
                I - Injuries or clinical findings identified
                S - Signs (Vitals: HR, BP, RR, SpO2, GCS)
                T - Treatment initiated and response
                
                Keep transmission under 45 seconds to avoid frequency congestion.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-DC-01")
        ),

        // 22. DOCUMENT - Tourniquet Conversion & Field Prolonged Care
        KnowledgeAsset(
            id = "VAULT-DC-03",
            title = "Tourniquet Conversion & Field Prolonged Care",
            type = KnowledgeType.DOCUMENT,
            category = "Circulation & Bleeding",
            source = "Prolonged Field Care Guidelines",
            version = "2.5",
            date = "2026-01-28",
            description = "Criteria for converting limb tourniquets to pressure dressings when transport delay exceeds 2 hours.",
            tags = listOf("Tourniquet", "Prolonged Care", "Bleeding", "Field Care"),
            local = true,
            content = """
                TOURNIQUET CONVERSION CRITERIA
                
                Convert tourniquet to hemostatic dressing within 2 hours if:
                1. Patient is not in hemorrhagic shock.
                2. Bleeding wound can be inspected and packed adequately.
                3. Limb is not completely amputated.
                
                NEVER release a tourniquet that has been on for > 6 hours due to lethal reperfusion injury and microemboli wash-in unless surgical capabilities are present.
            """.trimIndent(),
            relatedAssetIds = listOf("VAULT-PR-02", "VAULT-FC-02")
        )
    )
}
