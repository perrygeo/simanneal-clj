(ns simanneal.anneal
  (:require [clojure.core.async :as async]
            [clojure.core.reducers :as r]))

(defn make-temperature-seq
  "produce an exponential cooling schedule
  from tmax to tmix"
  [tmax tmin steps]
  (let [tfactor (* -1 (Math/log (/ tmax tmin)))]
    (for [step (range steps)]
      (if (= step 0)
        tmax
        (* tmax (Math/exp (* tfactor (/ step (dec steps)))))))))

(defn run-sa
  "Run simulated annealing.
  Repeatedly move-fn on the state, creating new states.
  Change to the proposed state if either one of two conditions occur
  - The proposed solution scores better
  - The proposed solution scores worse but within range
    defined by the delta, the temperature and some randomness"

  [initial-state
   move-fn
   score-fn
   temperature-seq]

  (let [state (atom initial-state)]
    (doseq [temp temperature-seq]
      (let [prev-score (score-fn @state)
            proposed (move-fn @state)
            score (score-fn proposed)
            dE (- score prev-score)]
        (if (or
             (< dE 0)
             (> (Math/exp (/ (* -1 dE) temp)) (rand)))
          (reset! state proposed))))
    @state))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; demo
;; world's worst sorting algorithm
;; Not useful but easy to debug

(comment

  (defn switch-elements
    "Switch 2 elements in a vector by index"
    [v i1 i2]
    (assoc v i2 (v i1) i1 (v i2)))

  (defn move
    "Return a new state with a small modification"
    [state]
    (let [idx1 (rand-int (count state))
          idx2 (rand-int (count state))]
      (switch-elements state idx1 idx2)))

  (defn score
    "weight numbers by order.
    Higher numbers penalized more at the end of the vector =
    effectively a reverse sort"
    [state]
    (let [indexes (range 1 (+ 1 (count state)))
          weighted-scores (map * indexes state)]
      (reduce + weighted-scores)))

  (run-sa (into [] (range 10))
          move
          score
          (make-temperature-seq 1.5 0.1 5000)))
