(ns simanneal.examples
  (:require [clojure.core.reducers :as r]
            [simanneal.anneal :refer [make-temperature-seq]]))

(defn switch-elements
  "Switch 2 elements in a vector by index"
  ;; https://stackoverflow.com/a/5980031/519385
  ;; Note: really makes me appreciate immutable vectors!
  ;; Input must be a vector, otherwise with lists
  ;; ClassCastException clojure.lang.LongRange cannot be cast to clojure.lang.IFn
  [v i1 i2]
  (assoc v
         i2 (v i1)
         i1 (v i2)))

(defn first-half [v]
  "foo"
  (take (int (/ (count v) 2)) v))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; demo
;;
;; sort bigger things into the first half of the vector
;;
;; Not useful but easy to debug

(def initial-state (into [] (range 20)))

(defn move
  "Return a new state with a small modification"
  [state]
  (let [idx1 (rand-int (count state))
        idx2 (rand-int (count state))]
    (switch-elements state idx1 idx2)))

(defn score
  "Given the state, calculate how well it meets the objective.
  Lowest score is best; minimize"
  [state]
  (* -1 (r/fold + (first-half state))))

(def temps (make-temperature-seq 0.1 2500.0 50000))
;; (run-sa initial-state move score temps))
