(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `signing-method-valid?` / `signing-method-invalid-claim?` are the
  SAME discipline applied to a genuinely Estonia-specific mechanism:
  Estonia's national electronic-identification (eID) scheme, per THREE
  independently-confirmed RIK-family pages (ariregister.rik.ee's own
  front page; abiinfo.rik.ee's own establishment walkthrough, dated
  27.10.2025; rik.ee's own portal page), all naming the SAME closed set
  of methods valid for digitally signing an e-Business Register
  petition: Estonian ID-card (including the e-Resident's digital ID
  card), Smart-ID, or Mobile-ID. RIA (Estonia's Information System
  Authority) separately confirms the general legal backing: 'Estonia's
  digital signature carries equal weight with a handwritten signature
  ... and public sector institutions must accept digitally signed
  documents.'

  This is a GENUINELY DIFFERENT check SHAPE than every prior iso3166
  sibling this repo mirrors: Bulgaria's ЗОП Art. 54(5) de-minimis is a
  PERCENTAGE-OF-TURNOVER ELIGIBILITY formula, Albania's Neni 76(2)(c)
  carve-out is a FLAT-CONSTANT ELIGIBILITY threshold, Azerbaijan's/
  Armenia's flagship checks are BOOLEAN registry-membership ELIGIBILITY
  reads, Antigua and Barbuda's vendor-class check is a THREE-TIER
  ELIGIBILITY-THRESHOLD classification, Benin's MPME mechanism is a
  BID-EVALUATION PRICE ADJUSTMENT, Bhutan's FDI Negative List is a
  CATEGORICAL SECTOR-EXCLUSION allow-list gate, Botswana's citizen/
  resident-preference check is an ORDERED-TIER CLASSIFICATION, and
  CAF's Marché réservé mechanism is a MULTI-CRITERION INCLUSION-
  ELIGIBILITY test over the bidder's OWN workforce composition/legal
  form. Estonia's digital-signing-method check is none of these: every
  prior sibling's flagship tests the BIDDER's business SUBSTANCE
  (turnover, a flat value, registry membership, contract-value tier, a
  price adjustment, business activity/sector, or workforce
  composition). This check instead tests the VALIDITY OF THE FILING'S
  OWN EXECUTION INSTRUMENT -- a procedural/instrumental axis genuinely
  new to this family: was the filing digitally signed with a method
  Estonia's e-government infrastructure actually recognises at all?

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real procurement portal. It builds the RECORD an
  operator would keep, not the act of submitting a portal registration
  itself (that is `marketentry.operation`'s `:filing/submit`, always
  human-gated -- see README Actuation)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  (+ (double base-fee)
     (* (double monthly-rate) (double monitoring-months))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (== (double claimed-fee) (compute-engagement-fee engagement)))

(def valid-signing-methods
  "The closed set of Estonian digital-signing methods this iteration
  independently confirmed, on THREE separate RIK-family pages
  (ariregister.rik.ee, abiinfo.rik.ee dated 27.10.2025, rik.ee), are
  accepted for signing an e-Business Register petition. A filing
  signed with anything outside this set has not, per those three
  sources, actually been validly executed on the portal."
  #{:id-card :e-resident-card :smart-id :mobile-id})

(defn signing-method-valid?
  "The ground-truth signing-method validity for `engagement`,
  independently recomputed from its own declared `:signing-method` --
  a closed-set membership test, not a business-substance eligibility
  test. A missing/nil declared method is simply not a member of the
  set (does not throw)."
  [{:keys [signing-method]}]
  (boolean (contains? valid-signing-methods signing-method)))

(defn signing-method-invalid-claim?
  "Does `engagement` fail the INDEPENDENTLY recomputed
  `signing-method-valid?` check? Unlike CAF's reserved-market check
  (entity-scope-gated on an opt-in `:reserved-market?` flag), this
  check is UNCONDITIONAL: every `:filing/submit` engagement has
  necessarily used SOME method to execute its own filing, so there is
  no opt-in flag to gate on -- the requirement is universal, not
  conditional, reflecting the real regulatory shape (every e-Business-
  Register-adjacent petition must be digitally signed with a
  recognised method, full stop)."
  [engagement]
  (not (signing-method-valid? engagement)))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  portal."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a portal
  registration (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
