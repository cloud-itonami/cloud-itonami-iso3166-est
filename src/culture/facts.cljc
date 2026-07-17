(ns culture.facts
  "Country-level regional-culture catalog for Estonia (EST) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"EST"
   [{:culture/id "est.dish.verivorst"
     :culture/name "Verivorst (blood sausage)"
     :culture/name-local "Verivorst"
     :culture/country "EST"
     :culture/kind :dish
     :culture/summary "Estonian blood sausage, sold and eaten mostly in winter as a traditional Christmas food, usually cooked in an oven and paired with lingonberry jam."
     :culture/url "https://en.wikipedia.org/wiki/Blood_sausage"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "est.dish.mulgipuder"
     :culture/name "Mulgipuder"
     :culture/country "EST"
     :culture/kind :dish
     :culture/summary "Traditional dish of potatoes mixed with pearl barley and topped with fried pork, from the Mulgimaa region of southern Estonia; recognized on UNESCO's Intangible Cultural Heritage list."
     :culture/url "https://en.wikipedia.org/wiki/Mulgipuder"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "est.dish.kama"
     :culture/name "Kama"
     :culture/country "EST"
     :culture/kind :dish
     :culture/summary "Traditional Baltic dish from Estonia and Finland of roasted cereal flour and legumes mixed with milk; many Estonians consider it a distinctive national food."
     :culture/url "https://en.wikipedia.org/wiki/Kama_(food)"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "est.beverage.vana-tallinn"
     :culture/name "Vana Tallinn"
     :culture/country "EST"
     :culture/kind :beverage
     :culture/summary "Estonian brand of rum-based liqueur manufactured continuously by Liviko since 1960 and sold in about 60 countries."
     :culture/url "https://en.wikipedia.org/wiki/Vana_Tallinn"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "est.craft.haapsalu-shawl"
     :culture/name "Haapsalu shawl"
     :culture/name-local "Haapsalu sall"
     :culture/country "EST"
     :culture/kind :craft
     :culture/summary "Knitted lace shawl that originated in the seaside resort town of Haapsalu, Estonia, in the early 19th century, knitted from lamb's wool so finely it can pass through a wedding ring."
     :culture/url "https://en.wikipedia.org/wiki/Haapsalu_shawl"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "est.festival.song-festival"
     :culture/name "Estonian Song Festival"
     :culture/country "EST"
     :culture/kind :festival
     :culture/summary "One of the largest choral events in the world, held in Tallinn since 1869 and occurring every five years; recognized as UNESCO Intangible Cultural Heritage since 2008."
     :culture/url "https://en.wikipedia.org/wiki/Estonian_Song_Festival"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "est.festival.jaanipaev"
     :culture/name "Jaanipäev"
     :culture/country "EST"
     :culture/kind :festival
     :culture/summary "Estonian midsummer holiday celebrated on 23-24 June, among the longest-celebrated public holidays in the Estonian folk calendar, marked by bonfires, singing and dancing."
     :culture/url "https://en.wikipedia.org/wiki/Jaanip%C3%A4ev"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "est.heritage.tallinn-old-town"
     :culture/name "Tallinn Old Town"
     :culture/country "EST"
     :culture/kind :heritage
     :culture/summary "Oldest part of Tallinn, Estonia, wholly preserving its medieval and Hanseatic structure, a UNESCO World Heritage Site since 1997."
     :culture/url "https://en.wikipedia.org/wiki/Tallinn_Old_Town"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-est culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "EST"))
                 " EST entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
