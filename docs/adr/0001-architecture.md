# ADR-0001: EST

Actor architecture and governor checks for the Estonia (EST) market-entry
compliance actor -- same shape as every `cloud-itonami-iso3166-*` sibling
(`marketentry.marketentryllm` advisor sealed behind `marketentry.governor`,
driving a `langgraph-clj` StateGraph in `marketentry.operation`, over an
append-only audit ledger in `marketentry.store`).

## Governor checks (`src/marketentry/governor.cljc`)

1. `spec-basis-violations` -- official-source citation required for any
   `:jurisdiction/assess`/`:filing/draft`/`:filing/submit` proposal.
2. `evidence-incomplete-violations` -- the jurisdiction's required-evidence
   checklist must be on file before draft/submit.
3. `e-residency-insufficient-violations` -- **boundary/negative check,
   this jurisdiction's key trap.** e-Residency is a digital-identity/
   authentication CHANNEL, not a residence permit, visa, or citizenship,
   and does NOT by itself satisfy business-registration substantive
   requirements. Independently verifies `:ariregister-registered?`
   UNCONDITIONALLY at `:filing/submit`, regardless of the engagement's
   own `:has-e-residency?` claim.
4. `signing-method-invalid-violations` -- flagship closed-set validity
   check: is the engagement's declared digital-signing method a member
   of Estonia's nationally-recognised set {ID-card (incl. e-Resident's
   digital ID), Smart-ID, Mobile-ID}, per three independently-confirmed
   RIK-family sources?
5. `engagement-fee-mismatch-violations` -- independent recompute of
   `claimed-fee = base-fee + monthly-rate x monitoring-months`.
6. `vat-record-unverified-violations` -- EMTA/MTA VAT-liability
   (Käibemaksuseadus §19^1(3)) record verification, conditional on
   `:requires-vat-record?`.
7. Confidence floor / actuation gate (SOFT) -- `:filing/draft`/
   `:filing/submit` always escalate regardless of confidence; see
   `marketentry.phase` for the independent phase-table enforcement of
   the same invariant.

Plus double-draft (`already-drafted-violations`) / double-submit
(`already-submitted-violations`) guards off dedicated `:drafted?`/
`:submitted?` booleans, never a `:status` value.

See `src/marketentry/facts.cljc`'s namespace docstring for the full
per-citation provenance trail (fin.ee, rtk.ee, riigihanked.riik.ee,
ariregister.rik.ee, abiinfo.rik.ee, rik.ee, emta.ee, ria.ee) and the
honestly-flagged gaps (Riigi Teataja itself unreachable this session;
`rep-spec-basis` deliberately left `nil` for EST).
