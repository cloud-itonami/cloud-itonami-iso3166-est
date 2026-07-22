(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest est-has-spec-basis
  (let [sb (facts/spec-basis "EST")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "EST")))
    (is (some? (facts/signing-method-spec-basis "EST")))))

(deftest est-rep-spec-basis-is-honestly-absent
  (testing "Estonia's own Riigihangete seadus exclusion-grounds article could not be confirmed with a citable current shape this iteration -- deliberately not claimed"
    (is (nil? (facts/rep-spec-basis "EST")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "EST")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "EST" all)))
    (is (not (facts/required-evidence-satisfied? "EST" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["EST" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

(deftest signing-method-spec-basis-valid-set
  (let [sm (facts/signing-method-spec-basis "EST")]
    (is (= #{:id-card :e-resident-card :smart-id :mobile-id}
           (:signing-method-valid-set sm)))))
