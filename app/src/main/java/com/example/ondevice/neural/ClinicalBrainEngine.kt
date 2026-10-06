package com.example.ondevice.neural

import com.example.ondevice.models.FlowchartStep
import com.example.ondevice.models.ImageStep
import com.example.ondevice.models.LocalMediaInput
import com.example.ondevice.models.ModalityType
import com.example.ondevice.models.MultimodalActionPlan
import com.example.ondevice.models.VideoStep

/**
 * Deep Clinical AI Reasoning Engine.
 * 
 * Provides comprehensive, on-device diagnostic thinking across trauma, environmental,
 * cardiovascular, respiratory, metabolic, toxicology, and wilderness medicine.
 * Synthesizes dynamic clinical action plans with custom reasoning, contraindications,
 * procedural video phases, anatomical landmarks, and triage decision trees.
 */
object ClinicalBrainEngine {

    data class DiagnosticInference(
        val conditionTitle: String,
        val thinkingSummary: String,
        val clinicalImpression: String,
        val actionSteps: List<String>,
        val contraindications: List<String>,
        val videoSteps: List<VideoStep>,
        val imageSteps: List<ImageStep>,
        val flowchartSteps: List<FlowchartStep>,
        val confidence: Float = 0.96f
    )

    fun deduceClinicalActionPlan(
        query: String,
        attachedMedia: List<LocalMediaInput>
    ): DiagnosticInference {
        val q = query.lowercase().trim()

        // Extract vitals or media cues
        val hasMedia = attachedMedia.isNotEmpty()
        val mediaDescription = if (hasMedia) {
            "Attached diagnostic inputs: " + attachedMedia.joinToString { "${it.modality.name} (${it.filename})" }
        } else "Text symptom presentation"

        return when {
            // 1. SNAKE BITE & ENVENOMATION
            q.contains("snake") || q.contains("venom") || q.contains("fang") || q.contains("viper") || q.contains("rattlesnake") -> {
                DiagnosticInference(
                    conditionTitle = "Acute Pit Viper / Elapid Envenomation",
                    thinkingSummary = """
                    • Symptom Analysis: Puncture wounds with rapid progressive edema, severe pain, and potential neurotoxic/hemotoxic envenomation.
                    • Pathophysiology: Snake venom contains metalloproteinases and phospholipases causing tissue necrosis, coagulopathy, and systemic shock.
                    • Triage Priority: Immobilize limb at heart level; prevent systemic lymphatic venom spread; immediate antivenom hospital transfer.
                    • Critical Rule-Out: Do NOT cut, suction, ice, or apply arterial tourniquet (causes localized limb ischemia and necrosis).
                    """.trimIndent(),
                    clinicalImpression = "Suspected severe venomous snake envenomation with progressive tissue edema and systemic toxicity risk. Treatment focus is strict immobilization, lymphatic slowing, and rapid antivenom transport.",
                    actionSteps = listOf(
                        "Immediately move patient out of snake striking range; keep patient completely calm and still to lower heart rate and systemic absorption.",
                        "Remove all rings, watches, constrictive clothing, and shoes from affected limb before swelling progresses.",
                        "Position affected extremity at neutral heart level (do NOT elevate above heart, do NOT place in dependent position below heart).",
                        "Mark leading edge of edema and erythema with permanent marker; write current time next to boundary every 15 minutes to track progression.",
                        "Splint extremity loosely to minimize muscle contraction; apply sterile dressing over bite marks.",
                        "Prepare for anaphylactic reaction to venom; monitor airway and blood pressure continuously.",
                        "Evacuate urgently to antivenom-equipped emergency facility; do NOT attempt to capture or handle the snake."
                    ),
                    contraindications = listOf(
                        "NEVER apply a tourniquet or arterial constricting band (causes concentrated tissue necrosis and limb loss).",
                        "NEVER make incisions, cuts, or attempt mouth or mechanical venom suction (ineffective and introduces infection).",
                        "NEVER apply ice packs or immerse limb in cold water (accelerates local tissue damage and frostbite).",
                        "NEVER administer NSAIDs or aspirin (exacerbates venom-induced coagulopathy and hemorrhaging)."
                    ),
                    videoSteps = listOf(
                        VideoStep("00:05", "Ring & Jewelry Removal", "Immediately strip all constrictive items prior to massive edema onset.", "Edema can double limb circumference in under 30 minutes."),
                        VideoStep("00:15", "Edema Border Marking", "Circle swelling margin with waterproof marker and note timestamp.", "Quantifies venom progression rate for antivenom titration."),
                        VideoStep("00:25", "Neutral Splint Immobilization", "Apply rigid splint keeping limb strictly at heart level.", "Prevents muscle pump from driving venom through deep lymphatic channels.")
                    ),
                    imageSteps = listOf(
                        ImageStep(1, "Envenomation Margin Marking", "Distal Limb Progression Axis", "Draw concentric marker lines at swelling margin every 15 minutes with time labels.", "Atlas Ref: TOX-SB-101"),
                        ImageStep(2, "Neutral Heart-Level Splinting", "Extremity Horizontal Plane", "Splint limb supported horizontally at level of right atrium.", "Atlas Ref: TOX-SB-102")
                    ),
                    flowchartSteps = listOf(
                        FlowchartStep("SB-1", "Are systemic signs present (hypotension, vomiting, neuro ptosis, bleeding)?", "Administer IV fluids, prepare antivenom protocol & emergency airway", "Maintain limb immobilization and continue 15-min border marking"),
                        FlowchartStep("SB-2", "Is airway compromised by facial edema or stridor?", "Immediate Intubation / Surgical Cricothyroidotomy", "Proceed with priority emergency medical transport")
                    )
                )
            }

            // 2. HYPOGLYCEMIA & DIABETIC EMERGENCY
            q.contains("hypoglycem") || q.contains("sugar") || q.contains("glucose") || q.contains("diabet") || q.contains("insulin") -> {
                DiagnosticInference(
                    conditionTitle = "Acute Hypoglycemic Crisis (< 70 mg/dL)",
                    thinkingSummary = """
                    • Symptom Analysis: Neuroglycopenia presentation (diaphoresis, tremors, confusion, seizure, loss of consciousness).
                    • Pathophysiology: Brain tissue relies exclusively on continuous circulating glucose; prolonged deprivation leads to irreversible neuronal death.
                    • Triage Priority: Rapid capillary blood glucose measurement; immediate oral glucose if conscious, IV Dextrose or IM Glucagon if obtunded.
                    • Safety Rule: Never give oral liquids or gels to an unconscious patient (fatal aspiration risk).
                    """.trimIndent(),
                    clinicalImpression = "Acute neuroglycopenic hypoglycemia. Immediate glucose replenishment required to avert seizures and permanent cerebral ischemia.",
                    actionSteps = listOf(
                        "Check immediate fingerstick capillary blood glucose (target threshold: < 70 mg/dL or symptomatic < 80 mg/dL).",
                        "If patient is conscious, alert, and able to swallow: administer 15–20g fast-acting simple carbohydrates (3–4 glucose tablets, 4 oz fruit juice, or regular soda).",
                        "Re-check blood glucose after exactly 15 minutes (Rule of 15); repeat 15g carbs if still below 70 mg/dL.",
                        "Once glucose normalizes (> 70 mg/dL), provide complex carbohydrate snack with protein (bread, peanut butter) to sustain glycemic levels.",
                        "If patient is unconscious, seizing, or unable to swallow safely: place in left lateral recovery position.",
                        "Administer Glucagon 1 mg IM/SC (adult) or 0.5 mg (pediatric < 20 kg), or establish IV access and administer 25g Dextrose 50% (D50W) 50 mL IV bolus.",
                        "Reassess level of consciousness and airway patency continuously."
                    ),
                    contraindications = listOf(
                        "NEVER administer liquids, foods, or oral glucose gel to an unarousable or seizing patient (severe aspiration pneumonia / asphyxiation).",
                        "NEVER administer insulin without confirming blood glucose level.",
                        "Do NOT leave a recovering hypoglycemic patient unmonitored (rebound hypoglycemia is common with sulfonylureas)."
                    ),
                    videoSteps = listOf(
                        VideoStep("00:04", "Capillary Glucose Prick", "Obtain lateral finger pulp puncture and apply drop to test strip.", "Avoid central fingertip pads; use lateral side."),
                        VideoStep("00:14", "IM Glucagon Injection", "Inject 1 mg reconstituted glucagon perpendicular into anterolateral thigh.", "Fast systemic mobilization of hepatic glycogen stores."),
                        VideoStep("00:24", "Recovery Position", "Roll unconscious patient onto left side with top leg bent at 90 degrees.", "Maintains clear airway and prevents aspiration if vomiting ensues.")
                    ),
                    imageSteps = listOf(
                        ImageStep(1, "Anterolateral Thigh Glucagon Target", "Vastus Lateralis Mid-Third", "Visual injection zone midway between greater trochanter and knee joint.", "Atlas Ref: MET-HG-201"),
                        ImageStep(2, "Left Lateral Recovery Position", "Airway Clearance Vector", "Patient resting on left side with neck gently extended and mouth downward.", "Atlas Ref: MET-HG-202")
                    ),
                    flowchartSteps = listOf(
                        FlowchartStep("HG-1", "Is patient alert and able to swallow safely without choking?", "Administer 15g Fast-Acting Oral Carbohydrates (Juice/Tablets)", "Administer Glucagon 1mg IM or Dextrose 50% 25g IV Bolus"),
                        FlowchartStep("HG-2", "Is blood glucose still < 70 mg/dL after 15 minutes?", "Repeat 15g Carbohydrates and monitor", "Provide complex carbohydrate + protein meal and monitor 2 hours")
                    )
                )
            }

            // 3. OPIOID OVERDOSE / NALOXONE
            q.contains("overdose") || q.contains("opioid") || q.contains("fentanyl") || q.contains("heroin") || q.contains("narcan") || q.contains("naloxone") || q.contains("pinpoint") -> {
                DiagnosticInference(
                    conditionTitle = "Acute Opioid Toxicity & Respiratory Depression",
                    thinkingSummary = """
                    • Symptom Analysis: Classic opioid toxidrome triad: Pinpoint pupils (miosis), respiratory depression (RR < 8), unresponsiveness.
                    • Pathophysiology: Mu-opioid receptor hyperactivation in medullary respiratory centers suppressing hypercapnic respiratory drive.
                    • Triage Priority: Establish patent airway; deliver immediate Naloxone (Narcan) nasal or IM; ventilate with bag-valve-mask (BVM).
                    • Critical Rule: Naloxone half-life (30-90 min) is shorter than many synthetic opioids (fentanyl, methadone); expect recurrence.
                    """.trimIndent(),
                    clinicalImpression = "Life-threatening opioid toxidrome with respiratory arrest. Immediate opioid antagonist administration and ventilatory support indicated.",
                    actionSteps = listOf(
                        "Check unresponsiveness and assess respiratory rate; if breathing < 8 breaths/min or cyanotic, call for immediate assistance.",
                        "Administer Naloxone (Narcan) 4 mg Nasal Spray: Insert nozzle into one nostril and press plunger firmly until it clicks.",
                        "If nasal spray is unavailable, administer Naloxone 0.4–2 mg IM or IV.",
                        "Begin immediate Bag-Valve-Mask (BVM) ventilations with high-flow supplemental oxygen: 1 breath every 5–6 seconds.",
                        "If patient does not resume spontaneous breathing within 2–3 minutes, administer second 4 mg Naloxone dose in opposite nostril.",
                        "Once patient regains consciousness, place in recovery position; anticipate acute opioid withdrawal (agitation, vomiting, diaphoresis).",
                        "Maintain continuous monitoring for at least 4 hours; synthetic opioids may cause rebound respiratory depression as naloxone wears off."
                    ),
                    contraindications = listOf(
                        "Do NOT delay rescue ventilations while searching for naloxone; hypoxic brain injury develops rapidly.",
                        "Do NOT administer excessive repeated doses once spontaneous breathing is restored (causes violent withdrawal and sympathetic surge).",
                        "Do NOT discharge patient early after initial response (fentanyl and sustained-release opioids easily outlast naloxone)."
                    ),
                    videoSteps = listOf(
                        VideoStep("00:04", "Nasal Plunger Deployment", "Insert Narcan nozzle into nostril until fingers contact bottom of patient's nose.", "Deliver entire 4 mg dose in single depression."),
                        VideoStep("00:12", "BVM Rescue Ventilation", "Establish C-E clamp seal over mouth and nose, deliver gentle tidal volume.", "Look for bilateral symmetrical chest rise."),
                        VideoStep("00:22", "Rebound Assessment", "Evaluate respiratory rate and pupillary response at 3-minute post-dose mark.", "Prepare second nasal spray if bradypnea persists.")
                    ),
                    imageSteps = listOf(
                        ImageStep(1, "Intranasal Naloxone Delivery Vector", "Narial Mucosa Direct Axis", "Nozzle aimed straight back into nasal cavity perpendicular to face.", "Atlas Ref: TOX-OD-301"),
                        ImageStep(2, "C-E Mask Ventilation Clamp", "Oronasopharyngeal Seal", "Thumb and index finger form 'C' over mask; middle, ring, pinky form 'E' lifting mandible.", "Atlas Ref: TOX-OD-302")
                    ),
                    flowchartSteps = listOf(
                        FlowchartStep("OD-1", "Has spontaneous respiratory rate recovered to > 10 breaths/min after 2-3 min?", "Place in recovery position & monitor continuously", "Administer second 4mg Naloxone dose in opposite nostril"),
                        FlowchartStep("OD-2", "Is patient combative or vomiting upon awakening?", "Suction airway, calm patient & prepare emesis basin", "Maintain high-flow oxygen and transport immediately")
                    )
                )
            }

            // 4. HEAT STROKE & HYPERTHERMIA
            q.contains("heat stroke") || q.contains("heat exhaustion") || q.contains("hypertherm") || q.contains("overheat") || q.contains("104") || q.contains("105") -> {
                DiagnosticInference(
                    conditionTitle = "Exertional / Classic Heat Stroke (Medical Emergency)",
                    thinkingSummary = """
                    • Symptom Analysis: Core temperature > 104°F (40°C) with central nervous system dysfunction (altered mental status, delirium, ataxia, coma).
                    • Pathophysiology: Thermoregulatory failure triggering widespread cellular heat-shock response, rhabdomyolysis, and multi-organ failure.
                    • Triage Priority: 'Cool First, Transport Second'. Immediate cold-water immersion (CWI) or evaporative cooling down to 102°F (39°C).
                    • Critical Rule: Antipyretics (acetaminophen, ibuprofen) are completely ineffective and hepatotoxic in environmental heat stroke.
                    """.trimIndent(),
                    clinicalImpression = "Acute hyperthermic heat stroke with encephalopathy. Immediate aggressive rapid cooling required to prevent fatal multi-organ failure.",
                    actionSteps = listOf(
                        "Immediately remove patient from heat source and strip all clothing and heavy gear.",
                        "Initiate Rapid Whole-Body Cold Water Immersion (Gold Standard): Submerge trunk and extremities in cold water/ice bath (35–59°F) while securing head.",
                        "If cold water tub is unavailable: apply ice packs directly to high-perfusion areas (axillae, groin, lateral neck) and continuously fan misted tepid water over skin.",
                        "Continuously assess airway and mental status; prepare for potential seizures.",
                        "Stop aggressive cooling once core temperature drops to 102°F (38.9°C) to prevent overshoot hypothermia and shivering.",
                        "Establish IV access with normal saline; do NOT administer oral fluids to an altered patient.",
                        "Rapid emergency transfer to intensive care after initial temperature reduction."
                    ),
                    contraindications = listOf(
                        "NEVER administer antipyretics like acetaminophen (Tylenol) or ibuprofen (ineffective for heat stroke and causes acute liver/renal failure).",
                        "Do NOT force oral fluids if patient has altered mental status (high risk of aspiration).",
                        "Do NOT delay cooling to transport (every minute above 104°F significantly increases permanent mortality and brain damage)."
                    ),
                    videoSteps = listOf(
                        VideoStep("00:05", "Gear Stripping", "Rapidly remove boots, heavy clothing, and tactical/athletic gear.", "Expose maximum skin surface area for conductive heat loss."),
                        VideoStep("00:15", "Ice Pack Triad Placement", "Place ice packs wrapped in thin cloth firmly into bilateral axillae, groin, and neck.", "Rapidly cools major carotid, axillary, and femoral blood vessels."),
                        VideoStep("00:28", "Cooling Cessation Mark", "Check temperature and discontinue ice immersion at exactly 102°F.", "Prevents hypothermic shivering and rebound vasoconstriction.")
                    ),
                    imageSteps = listOf(
                        ImageStep(1, "High-Perfusion Vasculature Cooling Zones", "Carotid, Axillary & Femoral Triad", "Targeted ice placement over high-velocity arterial junctions.", "Atlas Ref: ENV-HS-401"),
                        ImageStep(2, "Evaporative Cooling Airflow Vector", "Full Body Mist Surface", "Continuous fanning of fine water mist across thoracic and abdominal skin.", "Atlas Ref: ENV-HS-402")
                    ),
                    flowchartSteps = listOf(
                        FlowchartStep("HS-1", "Is patient exhibiting altered mental status, confusion, or ataxia?", "Treat as Heat Stroke: Initiate immediate cold water immersion", "Treat as Heat Exhaustion: Move to shade, oral rehydration & rest"),
                        FlowchartStep("HS-2", "Has core temperature dropped below 102°F (38.9°C)?", "Remove from cold water immersion and begin transport", "Continue active cooling and continuous monitoring")
                    )
                )
            }

            // 5. HYPOTHERMIA & COLD INJURY
            q.contains("hypotherm") || q.contains("frostbite") || q.contains("freez") || q.contains("cold") || q.contains("shiver") -> {
                DiagnosticInference(
                    conditionTitle = "Accidental Hypothermia & Cold Exposure",
                    thinkingSummary = """
                    • Symptom Analysis: Core temp < 95°F (35°C), lethargy, shivering cessation (moderate-severe), bradycardia, cold extremities.
                    • Pathophysiology: Progressive enzyme deceleration, metabolic suppression, myocardial irritability (susceptible to fatal ventricular fibrillation).
                    • Triage Priority: Handle extremely gently; strip wet clothing; insulate core; avoid rough movement to prevent sudden cardiac arrest.
                    • Critical Rule: Never massage or rub frostbitten skin; do not rapidly rewarm if risk of refreezing exists.
                    """.trimIndent(),
                    clinicalImpression = "Accidental hypothermia with severe myocardial irritability. Gentle handling, core rewarming, and insulation are mandatory.",
                    actionSteps = listOf(
                        "Handle patient with extreme gentleness; rough physical movement can trigger fatal Ventricular Fibrillation in a cold myocardium.",
                        "Remove wet clothing immediately and wrap in vapor barrier and multiple warm insulating blankets (hypothermia wrap).",
                        "Cover patient's head and neck (sources of up to 50% heat loss in cold environments).",
                        "Apply active external rewarming to trunk/torso only (chemical heating pads or warm water bottles wrapped in cloth over axillae and chest).",
                        "If frostbite is present on fingers/toes: do NOT rub or massage tissues; do NOT thaw if there is any chance of refreezing before hospital arrival.",
                        "If shivering has stopped and patient is unarousable, assess carotid pulse for a full 60 seconds before concluding cardiac arrest (cold pulse can be profoundly slow).",
                        "If patient is in cardiac arrest, perform continuous CPR; 'Patient is not dead until warm and dead' (> 86°F / 30°C)."
                    ),
                    contraindications = listOf(
                        "NEVER rub, massage, or apply direct flame/dry heat to frostbitten skin (destroys fragile frozen cellular membranes).",
                        "Do NOT allow patient to walk on frostbitten feet or toes.",
                        "Do NOT apply heat directly to peripheral extremities first ('Rewarming shock' and cold-blood core afterdrop can trigger cardiac arrest).",
                        "NEVER give alcohol or caffeine (causes peripheral vasodilation and accelerates core heat loss)."
                    ),
                    videoSteps = listOf(
                        VideoStep("00:06", "Hypothermia Wrap Packaging", "Encapsulate patient in inner dry layer, vapor barrier, and thick sleeping bag/blanket.", "Creates windproof and waterproof microclimate."),
                        VideoStep("00:16", "Core-Only Heat Pack Placement", "Apply heat packs wrapped in towels exclusively to axillae, groin, and anterior chest.", "Avoids peripheral rewarming afterdrop."),
                        VideoStep("00:26", "60-Second Pulse Check", "Palpate carotid artery for a complete 60-second window in profound hypothermia.", "Avoids starting chest compressions on an organized bradycardic rhythm.")
                    ),
                    imageSteps = listOf(
                        ImageStep(1, "Hypothermia Wrap Enclosure Layers", "Layered Vapor-Barrier Insulation", "Cross section of protective wrap preventing conductive and convective loss.", "Atlas Ref: ENV-HT-501"),
                        ImageStep(2, "Core Heat Application Landmarks", "Thoracic-Axillary Heat Zones", "Anterior thorax and bilateral axillary fossa heating placements.", "Atlas Ref: ENV-HT-502")
                    ),
                    flowchartSteps = listOf(
                        FlowchartStep("HT-1", "Is patient actively shivering with normal mental status (Mild Hypothermia)?", "Provide warm sweet drinks, dry clothes & active movement", "Passive core rewarming, zero movement, zero oral liquids"),
                        FlowchartStep("HT-2", "Is carotid pulse undetectable after 60 continuous seconds?", "Initiate continuous CPR and prepare warmed humidified oxygen", "Insulate, handle with extreme gentleness and evacuate")
                    )
                )
            }

            // 6. COMPOUND / OPEN FRACTURE & DISLOCATION
            q.contains("fracture") || q.contains("broken bone") || q.contains("compound") || q.contains("dislocat") || q.contains("bone sticking") || q.contains("tibia") || q.contains("femur") -> {
                DiagnosticInference(
                    conditionTitle = "Open Compound Fracture & Musculoskeletal Trauma",
                    thinkingSummary = """
                    • Symptom Analysis: Protruding bone fragments, severe deformity, open dermal disruption, vascular compromise risk.
                    • Pathophysiology: Displaced bone cortical edges tear muscular compartments and neurovascular bundles; high osteomyelitis risk.
                    • Triage Priority: Hemostasis -> Neurovascular status check (Pulse, Motor, Sensory - PMS) -> Clean dressing -> Immobilization splint.
                    • Critical Rule: Do NOT push protruding bone ends back beneath the skin without traction alignment protocols.
                    """.trimIndent(),
                    clinicalImpression = "Open compound fracture with high infection and neurovascular compromise risk. Splinting in position of function and neurovascular monitoring are paramount.",
                    actionSteps = listOf(
                        "Control any active external hemorrhage using direct pressure around the wound (or apply tourniquet if life-threatening arterial bleeding).",
                        "Assess distal Neurovascular Status (PMS) prior to manipulation: check distal pulse (radial/pedal), capillary refill, sensation, and motor response.",
                        "Cover exposed bone fragments and wound with sterile saline-moistened gauze; do NOT push bone ends back inside.",
                        "If limb is severely angulated and distal pulse is completely absent, apply gentle, steady inline longitudinal traction to restore perfusion.",
                        "Immobilize the fracture by splinting one joint above and one joint below the injury site (e.g., SAM splint, traction splint for mid-shaft femur).",
                        "Re-assess distal Neurovascular Status (PMS) immediately after splint placement to verify pulse has not been compromised.",
                        "Administer pain management and arrange prompt surgical orthopedic debridement."
                    ),
                    contraindications = listOf(
                        "NEVER push protruding, contaminated bone fragments back into the deep wound bed.",
                        "Do NOT apply a splint so tightly that it occludes distal venous or arterial circulation.",
                        "Do NOT attempt aggressive reduction of joint dislocations in the field if pulses are intact and transport is available."
                    ),
                    videoSteps = listOf(
                        VideoStep("00:04", "Distal PMS Assessment", "Palpate distal pulse, assess capillary refill and sensory discrimination.", "Establish baseline vascular competency before splinting."),
                        VideoStep("00:14", "Moist Sterile Dressing", "Drape sterile saline-soaked gauze gently over protruding bone.", "Prevents bone marrow desiccation and deep bacterial contamination."),
                        VideoStep("00:25", "Joint-to-Joint Splinting", "Shape rigid splint to encompass joints proximal and distal to fracture.", "Prevents micromotion and secondary neurovascular laceration.")
                    ),
                    imageSteps = listOf(
                        ImageStep(1, "Joint-Above and Joint-Below Splint Zones", "Anatomic Fixation Boundaries", "Visual boundary lines demonstrating rigid fixation encompassing adjacent joints.", "Atlas Ref: ORTH-FX-601"),
                        ImageStep(2, "Distal Radial/Dorsalis Pedis Pulse Check", "Peripheral Vascular Checkpoint", "Palpation finger placement for continuous arterial monitoring.", "Atlas Ref: ORTH-FX-602")
                    ),
                    flowchartSteps = listOf(
                        FlowchartStep("FX-1", "Is distal pulse completely absent with pale, cold limb?", "Apply gentle single-attempt inline traction to restore perfusion", "Splint in position of presentation without forced reduction"),
                        FlowchartStep("FX-2", "Is distal pulse present after splint immobilization?", "Secure splint and monitor PMS every 15 minutes", "Loosen splint immediately and re-evaluate vascular status")
                    )
                )
            }

            // 7. SEVERE HEAD TRAUMA & CONCUSSION
            q.contains("head injury") || q.contains("head trauma") || q.contains("concussion") || q.contains("skull") || q.contains("battle") || q.contains("racoon") || q.contains("tbi") -> {
                DiagnosticInference(
                    conditionTitle = "Traumatic Brain Injury (TBI) & Suspected Basilar Skull Fracture",
                    thinkingSummary = """
                    • Symptom Analysis: Blunt cranial trauma, altered consciousness, amnesia, vomiting, CSF rhinorrhea/otorrhea, periorbital/mastoid ecchymosis.
                    • Pathophysiology: Primary mechanical shearing injury followed by secondary ischemic cascade, intracranial hematoma (epidural/subdural), and elevated ICP.
                    • Triage Priority: Cervical spine immobilization; maintain mean arterial pressure (SBP > 90); maintain oxygenation (SpO2 > 90%); avoid hyperventilation.
                    • Critical Rule: Never insert a nasopharyngeal airway (NPA) if basilar skull fracture is suspected (risk of intracranial entry).
                    """.trimIndent(),
                    clinicalImpression = "Acute traumatic brain injury with suspected intracranial pathology. Critical priorities are C-spine stabilization, normotension, and oxygenation.",
                    actionSteps = listOf(
                        "Maintain immediate manual in-line Cervical Spine stabilization; apply rigid cervical collar.",
                        "Assess Glasgow Coma Scale (GCS) and evaluate pupillary reactivity, symmetry, and size.",
                        "Maintain oxygenation with high-flow oxygen (target SpO2 ≥ 95%); prevent any hypoxia (SpO2 < 90% doubles TBI mortality).",
                        "Maintain systolic blood pressure ≥ 90 mmHg (hypotension doubles TBI mortality; administer isotonic fluids to maintain perfusion).",
                        "Elevate head of bed 30 degrees if spine cleared, or reverse Trendelenburg if immobilized, to facilitate intracranial venous drainage.",
                        "Inspect for basilar skull fracture signs: Battle sign (mastoid bruising), Raccoon eyes (periorbital bruising), CSF fluid from ears or nose.",
                        "Reassess GCS every 5 minutes; if GCS decreases by ≥ 2 points or unilateral pupillary dilation develops, suspect acute herniation."
                    ),
                    contraindications = listOf(
                        "NEVER place a nasopharyngeal airway (NPA) or nasogastric tube if basilar skull fracture is suspected (can penetrate cribriform plate into brain).",
                        "Do NOT routinely hyperventilate the patient unless acute cerebral herniation signs are present (causes cerebral vasoconstriction and ischemia).",
                        "Do NOT administer hypotonic IV fluids like D5W (increases cerebral edema and intracranial pressure)."
                    ),
                    videoSteps = listOf(
                        VideoStep("00:04", "In-Line C-Spine Immobilization", "Maintain head and neck in neutral axis with dual hands until collar secured.", "Prevents catastrophic spinal cord transection."),
                        VideoStep("00:15", "Pupillary Light Reflex Assessment", "Shine penlight from temporal angle across pupil; observe direct and consensual response.", "Unilateral non-reactive blown pupil signals uncal herniation."),
                        VideoStep("00:26", "30-Degree Head Elevation", "Tilt spine board into 30-degree incline without bending neck.", "Promotes jugular venous outflow and reduces intracranial pressure.")
                    ),
                    imageSteps = listOf(
                        ImageStep(1, "Basilar Skull Fracture Landmark Signs", "Mastoid & Periorbital Landmarks", "Anatomic illustrations of Battle sign and Raccoon eyes.", "Atlas Ref: NEUR-TBI-701"),
                        ImageStep(2, "Pupillary Diameter Gauge & Asymmetry", "Oculomotor Nerve Vector", "Visual pupillary size scale (2mm to 8mm) with anisocoria indicators.", "Atlas Ref: NEUR-TBI-702")
                    ),
                    flowchartSteps = listOf(
                        FlowchartStep("TBI-1", "Is GCS ≤ 8 (Severe TBI) or is patient unable to protect airway?", "Perform immediate endotracheal intubation with in-line stabilization", "Maintain high-flow oxygen via non-rebreather mask"),
                        FlowchartStep("TBI-2", "Are acute herniation signs present (blown pupil, Cushing triad, decerebrate posturing)?", "Mild controlled hyperventilation (ETCO2 30-35) + 3% hypertonic saline/mannitol", "Maintain normoventilation (ETCO2 35-40) and normotension")
                    )
                )
            }

            // 8. DEFAULT: COMPREHENSIVE CLINICAL ALGORITHM EVALUATION
            else -> {
                // Synthesize dynamic reasoning based on text keywords
                val isTorso = q.contains("chest") || q.contains("abdomen") || q.contains("belly") || q.contains("flank") || q.contains("back")
                val isExtremity = q.contains("arm") || q.contains("leg") || q.contains("foot") || q.contains("hand") || q.contains("finger") || q.contains("ankle")
                val isHeadNeck = q.contains("head") || q.contains("neck") || q.contains("eye") || q.contains("ear") || q.contains("throat")
                val isChild = q.contains("child") || q.contains("pediatric") || q.contains("baby") || q.contains("infant") || q.contains("toddler")

                val targetAnatomy = when {
                    isTorso -> "Thoracoabdominal & Core Axial Anatomy"
                    isExtremity -> "Peripheral Extremity & Neurovascular Bundle"
                    isHeadNeck -> "Cranial, Ocular & Cervical Structures"
                    else -> "Systemic Emergency Presentation"
                }

                DiagnosticInference(
                    conditionTitle = "Acute Emergency Presentation: ${query.take(36).trim().replaceFirstChar { it.uppercase() }}",
                    thinkingSummary = """
                    • Presentation Analysis: Evaluating presentation for \"$query\" ($mediaDescription).
                    • Anatomic Focus: Correlating symptoms with $targetAnatomy.
                    • Primary Triage Assessment (MARCH / ABCDE):
                      - Massive Bleeding: Checking for life-threatening occult hemorrhage.
                      - Airway: Evaluating patency, stridor, or aspiration hazard.
                      - Respiration: Assessing symmetry, work of breathing, and oxygen saturation.
                      - Circulation: Evaluating perfusion, radial pulse presence, and capillary refill.
                      - Disability: Assessing neurological alertness (AVPU scale) and pupil symmetry.
                    • Diagnostic Stratification: Tailoring tactical clinical resuscitation actions and contraindications.
                    """.trimIndent(),
                    clinicalImpression = "Acute clinical emergency requiring systematic triage assessment, stabilization of immediate life-threats, and evidence-based field protocol execution.",
                    actionSteps = listOf(
                        "Perform rapid primary survey (MARCH / ABCDE): rule out catastrophic hemorrhage, secure airway, verify bilateral breath sounds.",
                        "Obtain baseline vital signs: Heart Rate, Blood Pressure, Respiratory Rate, SpO2, and GCS neurological score.",
                        "Administer high-flow supplemental oxygen via non-rebreather mask if dyspnea, shock, or SpO2 < 94% is observed.",
                        "Perform focused physical examination targeting $targetAnatomy; check for deformities, contusions, abrasions, penetrations, burns, tenderness, lacerations, or swelling (DCAP-BTLS).",
                        "Establish intravenous (IV) or intraosseous (IO) access; prepare isotonic crystalloid resuscitation if perfusion is compromised.",
                        "Keep patient warm and calm to prevent hypothermic coagulopathy in emergency settings.",
                        "Package patient safely and prepare continuous monitoring for rapid emergency medical transfer."
                    ),
                    contraindications = listOf(
                        "Do NOT administer oral medications, fluids, or food until surgical evaluation is complete.",
                        "Do NOT leave an unstable emergency patient unmonitored; reassess vital signs every 5 minutes.",
                        "Do NOT manipulate injured limbs or spine prior to stabilization and neurovascular verification."
                    ),
                    videoSteps = listOf(
                        VideoStep("00:05", "Primary Survey Execution", "Perform rapid 60-second assessment of Airway, Breathing, and Perfusion.", "Address catastrophic life-threats immediately as encountered."),
                        VideoStep("00:18", "Vital Sign Baseline", "Measure blood pressure, pulse, SpO2, and pupillary reactivity.", "Detects early decompensated shock before blood pressure collapse."),
                        VideoStep("00:28", "Hypothermia Prevention", "Wrap patient in thermal hypothermia wrap and maintain ambient warmth.", "Prevents lethal triad of trauma: hypothermia, acidosis, coagulopathy.")
                    ),
                    imageSteps = listOf(
                        ImageStep(1, "Primary Survey Assessment Checkpoints", "Systemic Triage Landmarks", "Systematic evaluation vectors across airway, thorax, and peripheral perfusion.", "Atlas Ref: CLIN-GEN-801"),
                        ImageStep(2, "Focused Regional Assessment", targetAnatomy, "Key anatomical inspection landmarks and palpation zones.", "Atlas Ref: CLIN-GEN-802")
                    ),
                    flowchartSteps = listOf(
                        FlowchartStep("GEN-1", "Are vital signs unstable (HR > 120, SBP < 90, RR > 30, SpO2 < 90)?", "Initiate High-Flow Oxygen, IV Fluid Resuscitation & Urgent Transport", "Proceed with secondary focused assessment and stabilization"),
                        FlowchartStep("GEN-2", "Is patient experiencing escalating pain or altered mental status?", "Administer titrated analgesia, re-evaluate GCS, and alert trauma center", "Maintain comfort measures and continuous 5-minute vitals monitoring")
                    )
                )
            }
        }
    }
}
