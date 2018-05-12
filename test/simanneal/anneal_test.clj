(ns simanneal.anneal-test
  (:require [clojure.test :refer :all]
            [simanneal.anneal :refer :all]))

(defn fuzzy= [x y & [tolerance]]
  (let [tolerance (or tolerance 0.01)
        diff (Math/abs (- x y))]
    (< diff tolerance)))

(deftest test-make-temperature-seq
  (let [temp (make-temperature-seq 100.0 1.0 5)]
    (is (= (count temp) 5))
    (is (= (first temp) 100.0))
    (is (= (apply max temp) 100.0))
    (is (fuzzy= (apply min temp) 1.0))))
