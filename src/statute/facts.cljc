(ns statute.facts
  "General-law compliance catalog for Estonia (EST) -- extends this
  repo's existing `marketentry.facts` (public-procurement market-entry
  only, narrow scope) with a second, orthogonal catalog of statutes a
  company operating in this jurisdiction must generally track for
  compliance. Mirrors cloud-itonami-iso3166-jpn/-deu/-bgr/-aze/-alb/
  -arm/-atg/-ben/-btn/-bwa/-caf's `statute.facts` (ADR-2607141700,
  cloud-itonami-compliance-fact-federation).

  Every entry cites an OFFICIAL government-hosted URL -- never
  fabricated. Riigi Teataja (riigiteataja.ee), Estonia's own official
  consolidated-legislation portal, turned out in this session to be an
  Angular SPA behind Cloudflare that returns only a loading shell to
  curl/WebFetch (and to a Googlebot user-agent); no JS-execution path
  was available either (Chrome's 'Allow JavaScript from Apple Events'
  is off by default, and changing it is a browser security setting out
  of scope for this iteration to flip; System Events accessibility
  access to drive the browser via the GUI was also unavailable in this
  sandboxed environment). Rather than fabricate a law number/date, this
  iteration instead independently fetched the OFFICIAL government
  bodies that administer and cite these two specific laws, several of
  which link straight to the exact Riigi Teataja ELI permalink:

  - **Äriseadustik (Commercial Code)**: this iteration confirmed
    directly from RIK's (Registrite ja Infosüsteemide Keskus -- Centre
    of Registers and Information Systems) own e-Business Register
    portal page (`rik.ee/en/e-business-register/e-business-register-
    portal`, fetched directly) that its own navigation links the exact
    text 'Commercial Code' to
    `riigiteataja.ee/en/eli/527022024004/consolide` -- RIK's own
    citation of the current consolidated Äriseadustik, not a guessed
    act number. This iteration separately confirmed a SPECIFIC,
    substantive provision directly quoted from RIK's own dated
    (27.10.2025) establishment walkthrough
    (`abiinfo.rik.ee/en/applications-and-dashboard/establishment-new-
    legal-person/establishment-private-limited-company`, fetched
    directly): 'According to the Commercial Code, the founders are
    obliged to pay for the share in full before submitting the
    application for entering the private company into the commercial
    register, and the board members must confirm that the
    contributions to the private company have been paid.' This
    iteration did NOT independently render Riigi Teataja's own page to
    re-confirm the Code's full text at that URL (see above) -- HIGH
    confidence on the citation identity (RIK's own anchor text) and on
    the quoted share-capital provision (RIK's own dated walkthrough
    text), MODERATE confidence only in the sense that the primary
    statutory article number for that provision was not independently
    read off Riigi Teataja's own rendered page.
  - **Töölepingu seadus (Employment Contracts Act)**: this iteration
    specifically searched for and found Estonia's own Employment
    Contracts Act extensively cited, section-by-section, on the
    Estonian Labour Inspectorate's (Tööinspektsioon) own official
    citizen-facing portal (`tooelu.ee/en/7/entry-employment-contract`,
    fetched directly), including a 'Legal basis' table cell reading
    'Employment Contracts Act (ECA) section 1 Subsection 1' and
    further direct citations of ECA section 4 subsection 5, section 5
    subsection 1, section 5 subsection 5, section 6, section 9, section
    35, subsection 131(2), section 133, section 134, and section 136.
    The SAME page independently confirms the Act's current form has
    applied since 1 July 2009: 'employment contracts entered into
    before 01/07/2009 must be retained for 50 years ... on the basis of
    the previously valid Employment Contracts Act' -- i.e. a PRIOR
    Employment Contracts Act governed before that date, and the
    CURRENT Act (this catalog's citation) took over from 1 July 2009.
    This iteration did NOT independently locate a riigiteataja.ee link
    on this specific tooelu.ee page (unlike the Commercial Code, where
    RIK linked directly) -- the citation below is therefore grounded in
    Tööinspektsioon's own official page (a government LABOUR AUTHORITY
    directly administering and explaining this exact Act to the
    public, the same evidentiary weight CAF's catalog gave to a
    Ministry's own organisational page rather than the raw law text),
    not in a directly-rendered Riigi Teataja page.

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit."
  {"EST"
   [{:statute/id "est.ariseadustik"
     :statute/title "Äriseadustik (Commercial Code)"
     :statute/jurisdiction "EST"
     :statute/kind :law
     :statute/law-number "Current consolidated version, per RIK's (Registrite ja Infosüsteemide Keskus) own e-Business Register portal page, own anchor text 'Commercial Code' linking to the Riigi Teataja ELI permalink cited below. This iteration independently quoted a specific substantive provision (full share-capital payment required before registration) from RIK's own dated (27.10.2025) establishment walkthrough, but did not independently render Riigi Teataja's own page to re-confirm article numbers -- HIGH confidence on citation identity and the quoted provision, see namespace docstring."
     :statute/url "https://www.riigiteataja.ee/en/eli/527022024004/consolide"
     :statute/url-provenance :official-riigiteataja-via-rik-ee
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "est.toolepingu-seadus"
     :statute/title "Töölepingu seadus (Employment Contracts Act)"
     :statute/jurisdiction "EST"
     :statute/kind :law
     :statute/law-number "Current version in force since 1 July 2009 (own text on tooelu.ee: employment contracts entered into before 01/07/2009 were governed by 'the previously valid Employment Contracts Act', implying the CURRENT Act took over from that date), per the Estonian Labour Inspectorate's (Tööinspektsioon) own official portal, which cites section 1(1), section 4(5), section 5(1), section 5(5), section 6, section 9, section 35, subsection 131(2), section 133, section 134 and section 136 directly. This iteration did not independently locate a riigiteataja.ee permalink on the specific tooelu.ee page reached -- see namespace docstring."
     :statute/url "https://www.tooelu.ee/en/7/entry-employment-contract"
     :statute/url-provenance :official-tooinspektsioon-portal
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:labor}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-est statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "EST")) " EST statute(s) seeded with an "
                 "official citation. Extend `statute.facts/catalog`, never "
                 "fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :data-protection)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
