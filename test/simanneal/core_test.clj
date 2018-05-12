(ns simanneal.core-test
  (:require [clojure.test :refer :all]
            [simanneal.core :refer :all]))

(deftest test-main
  (testing "main integration tests"
    (is (= (-main) nil))))
