# cloud-itonami-iso3166-est

Open ISO 3166 Blueprint for **EST**: Estonia -- **`:implemented`**.

This repository designs **and implements** a forkable OSS business for
an independent public-sector market-entry consultant: an already-
incorporated operator (e.g. a `cloud-itonami-cofog-{code}`,
`cloud-itonami-isco-{code}`, `cloud-itonami-unspsc-{segment}` or
`cloud-itonami-{ISIC}` blueprint fork) gets a **MarketEntry-LLM**
Compliance Advisor + independent **Market-Entry Compliance Governor**
to navigate public-procurement registration, local business/tax
registration, and EU single-market rules in Estonia, so the operator
can win and service a government contract without hiring a full in-house
compliance department.

## Checks

Seven checks, in priority order, evaluated by `marketentry.governor` on
every `MarketEntry-LLM` proposal. The first six are HARD violations a
human approver cannot override; double-actuation guards are counted
separately. The confidence/actuation gate (item 7) is SOFT -- but see
Actuation below, `:filing/draft`/`:filing/submit` never auto-commit
regardless.

| # | Check | Grounds | Source |
|---|---|---|---|
| 1 | **Spec-basis** -- a `:jurisdiction/assess`/`:filing/draft`/`:filing/submit` proposal must cite an official source, never an invented one | `marketentry.facts/spec-basis` | fin.ee, rtk.ee, riigihanked.riik.ee, ariregister.rik.ee, emta.ee |
| 2 | **Evidence incomplete** -- for draft/submit, the jurisdiction's full required-evidence checklist must be on file | `marketentry.facts/required-evidence-satisfied?` | e-Business Register entry, EMTA VAT record, RHR registration confirmation, signing-method confirmation, authorized-representative confirmation |
| 3 | **e-Residency insufficient** (boundary/negative check, this jurisdiction's key trap) -- for submit, INDEPENDENTLY verify `:ariregister-registered?` is true, UNCONDITIONALLY -- never let an engagement's own `:has-e-residency? true` be mistaken for actual company registration | `marketentry.governor/e-residency-insufficient-violations` | e-resident.gov.ee (e-Residency is a digital-identity/authentication tool, not a residence permit/visa/citizenship), ariregister.rik.ee |
| 4 | **Signing-method invalid** (flagship) -- for submit, independently recompute whether the engagement's own declared `:signing-method` belongs to Estonia's closed set of nationally-recognised e-identification methods {ID-card (incl. e-Resident's digital ID), Smart-ID, Mobile-ID} | `marketentry.registry/signing-method-invalid-claim?` | ariregister.rik.ee, abiinfo.rik.ee (dated 27.10.2025), rik.ee, ria.ee -- 3 independently-confirmed pages |
| 5 | **Engagement fee mismatch** -- for submit, independently recompute `claimed-fee = base-fee + monthly-rate x monitoring-months` | `marketentry.registry/engagement-fee-matches-claim?` | ground-truth recompute (fleet-standard discipline) |
| 6 | **VAT record unverified** -- for submit, when `:requires-vat-record? true`, independently check `:vat-record-verified?` | `marketentry.governor/vat-record-unverified-violations` | Value Added Tax Act (Käibemaksuseadus) subsection 3 of section 19^1, emta.ee -- EUR 40,000 annual threshold |
| 7 | **Confidence floor / actuation gate** (SOFT) -- LLM confidence below 0.6, or the op is `:filing/draft`/`:filing/submit` -> escalate to human | `marketentry.governor/check` | this vertical's own Trust Controls (`docs/business-model.md`) |

Two further double-actuation guards (`already-drafted`,
`already-submitted`) refuse to draft or submit the SAME engagement
twice, enforced off dedicated `:drafted?`/`:submitted?` booleans, never
a `:status` value.

Checks 3 and 4 are deliberately DIFFERENT axes on the SAME underlying
Estonian e-identity mechanism: check 4 tests whether the filing's own
execution INSTRUMENT (the digital-signing method) is procedurally
valid at all; check 3 tests whether the operator's underlying
REGISTRATION SUBSTANCE (an actual e-Business Register entry) exists,
independent of whether e-Residency -- one of the valid signing
methods -- was used to get there. Holding e-Residency never substitutes
for either.

## Actuation

**Drafting a real Riigihangete register (RHR) filing / e-Business
Register submission and submitting a real filing are never
autonomous, at any phase, by construction.** Two independent layers
enforce this:

- `marketentry.governor`'s `high-stakes` set
  (`#{:actuation/draft-filing :actuation/submit-filing}`) always
  escalates, regardless of confidence.
- `marketentry.phase`'s phase table (`phase 0` through `phase 3`)
  never puts `:filing/draft` or `:filing/submit` in any phase's
  `:auto` set -- see `marketentry.phase`'s own docstring and
  `test/marketentry/phase_test.clj`'s `filing-submit-never-auto`.

The actor may intake an engagement, assess a jurisdiction and draft a
recommendation; a human market-entry operator is always the one who
actually files a draft or a submission. Grounded directly in this
blueprint's own [`docs/business-model.md`](docs/business-model.md) and
`marketentry.governor`'s own namespace docstring, which names this
vertical's Trust Controls verbatim: "any actual portal registration or
filing submission requires Market-Entry Compliance Governor clearance
and always escalates to human sign-off"; "a false or fabricated
regulatory-requirement claim is a HARD hold". `:filing/draft` and
`:filing/submit` apply SEQUENTIALLY to the SAME engagement record
(draft first, submit later) -- matching every sibling
`market-entry-compliance-governor` actor's own sequential shape.

## No robotics premise — digital/data service exemption

Market-entry and procurement-compliance navigation is a pure data/software
service with no physical-domain work (portal registration, document
checklists, regulatory-change monitoring) — the same exemption class as
`cloud-itonami-6310` (HR SaaS replacement) and `cloud-itonami-gtin-*`.
`blueprint.edn` sets `:itonami.blueprint/robotics false` and
`:required-technologies` lists only real capabilities (`:identity`,
`:forms`, `:dmn`, `:bpmn`, `:audit-ledger`), no `:robotics`.

## Core Contract

```text
operator intake + prior filing history
        |
        v
Compliance Advisor -> Market-Entry Compliance Governor -> filing draft, or human sign-off
        |
        v
gated portal registration / filing submission + audit ledger
```

No automated proposal can submit a portal registration or filing the
governor refuses, suppress a compliance record, or claim a legal/tax
conclusion the governor has not cleared. `:filing/submit` is never in any
phase's `:auto` set — it always requires human sign-off (mirrors
`cloud-itonami-M6910`'s `filing-submit-never-auto-at-any-phase`
invariant).

## What this is NOT

- **Not the government of Estonia.** See
  [`docs/business-model.md`](docs/business-model.md) for the boundary with
  `com-etzhayyim-ooyake` (read-only civic mirror), `matsurigoto` (sovereign
  statecraft), `com-etzhayyim-toritsugi` (individual citizen concierge),
  `legal-entity.etzhayyim.com` (read-only data aggregation), and
  `cloud-itonami-M6910` (company incorporation — a different regulatory
  phase this blueprint assumes is already complete).
- **Not legal or tax advice.** Every regulatory claim must cite the
  official source and route final filings to Estonian-licensed counsel
  or a registered agent where the law requires licensed representation.

## Capability layer

Resolves via [`kotoba-lang/iso3166`](https://github.com/kotoba-lang/iso3166)
(ISO 3166 `EST`). Required capabilities:

- :identity
- :forms
- :dmn
- :bpmn
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## Run

```bash
clojure -M:dev:run     # walk a clean intake -> assess -> draft -> submit lifecycle, plus HARD-hold scenarios
clojure -M:dev:test    # governor contract · phase invariants · store parity · registry conformance · facts coverage
clojure -M:lint        # clj-kondo (errors fail; CI mirrors this)
```

## License

AGPL-3.0-or-later.

## Market-entry / statute catalogs

Governed public-sector market-entry compliance actor, same architecture
as the other `cloud-itonami-iso3166-*` siblings:

- `src/marketentry/{facts,governor,phase,sim,operation,registry,store,
  marketentryllm}.cljc` -- the actor. `facts.cljc` cites the Public
  Procurement Act (Riigihangete seadus, current consolidated version
  per the Ministry of Finance's own citation), the Riigihangete
  register (RHR, riigihanked.riik.ee -- vastutav töötleja/responsible
  processor: Riigi Tugiteenuste Keskus as of 1 January 2024, per RTK's
  own dated announcement, transferred from the Ministry of Finance),
  the e-Business Register (e-Äriregister, ariregister.rik.ee --
  registrar: Registration Department of Tartu County Court; portal
  developed and managed by RIK), and the Estonian Tax and Customs
  Board (EMTA) VAT-liability registration threshold (EUR 40,000
  annual turnover, Value Added Tax Act subsection 3 of section 19^1).
  `governor.cljc`'s flagship check independently recomputes whether
  the engagement's own declared digital-signing method belongs to
  Estonia's closed set of nationally-recognised e-identification
  methods {ID-card (incl. e-Resident's digital ID), Smart-ID,
  Mobile-ID} -- a check SHAPE genuinely different from every other
  iso3166 sibling's (a procedural/instrumental validity axis on the
  filing's own execution, not a business-substance eligibility test;
  see the namespace docstrings for the full research trail and
  honestly-narrowed scope, including facts this iteration could NOT
  verify, such as an exclusion-grounds article citation and a
  same-day -- vs. the officially-documented 1-2 day -- registration
  claim). A SEPARATE boundary/negative check,
  `e-residency-insufficient-violations`, independently verifies
  `:ariregister-registered?` at `:filing/submit` UNCONDITIONALLY --
  this jurisdiction's best-known overstatement trap is treating
  e-Residency (a digital-identity/authentication CHANNEL, per
  e-resident.gov.ee's own site -- NOT a residence permit, visa, or
  citizenship) as if it were itself sufficient for business
  registration; this actor never does.
- `src/statute/facts.cljc` -- general-law catalog: the Äriseadustik
  (Commercial Code, cited via RIK's own portal page linking to the
  Riigi Teataja consolidated text) and the Töölepingu seadus
  (Employment Contracts Act, cited via the Labour Inspectorate's own
  official portal, in force since 1 July 2009).

Every citation is curl/WebFetch-verified against an official source
(fin.ee, rtk.ee, riigihanked.riik.ee, ariregister.rik.ee, abiinfo.rik.ee,
rik.ee, emta.ee, ria.ee, tooelu.ee); Riigi Teataja itself
(riigiteataja.ee) turned out to be an Angular SPA behind Cloudflare
unreachable by curl/WebFetch/Googlebot-UA in this session, so citations
to it are anchored via other official government bodies' own direct
links/quotes rather than a directly-rendered Riigi Teataja page -- see
`marketentry.facts`'s and `statute.facts`'s docstrings for exactly
which facts are HIGH-confidence (quoted directly) vs. an
honestly-flagged gap.

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Estonia:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
