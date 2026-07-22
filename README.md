# cloud-itonami-iso3166-est

Open ISO 3166 Blueprint for **EST**: Estonia.

This repository designs a forkable OSS business for an independent
public-sector market-entry consultant: an already-incorporated operator
(e.g. a `cloud-itonami-cofog-{code}`, `cloud-itonami-isco-{code}`,
`cloud-itonami-unspsc-{segment}` or `cloud-itonami-{ISIC}` blueprint
fork) gets a Compliance Advisor + independent **Market-Entry Compliance
Governor** to navigate public-procurement registration, local business/
tax registration, and EU single-market rules in Estonia, so the operator
can win and service a government contract without hiring a full in-house
compliance department.

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
  claim).
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
