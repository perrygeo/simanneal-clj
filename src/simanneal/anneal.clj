(ns simanneal.anneal
  (:require [clojure.core.async :as async]
            [clojure.core.reducers :as r]
            [simanneal.examples :refer [switch-elements first-half]]))

(defn make-temperature-seq
  "produce an exponential cooling schedule
  from tmax to tmix"
  [tmax tmin steps]
  (let [tfactor (* -1 (Math/log (/ tmax tmin)))]
    (for [step (range steps)]
      (if (= step 0)
        tmax
        (* tmax (Math/exp (* tfactor (/ step (dec steps)))))))))

(comment
  ;; pay no attention
  (def temps
    (make-temperature-seq 25.0 0.1 50000))
  (defn handler [request]
    (let [{{:keys [database email]} :services} request]))
  (+ x 1)
  (make-temperature-seq 500 1 3))

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

;; TODO
(defn run-sa2
  "Attempts to optimize run-sa. WIP"
  [initial-state
   create-move-fn
   score-fn
   temperature-seq]

  (let [state (atom initial-state)]
    (doseq [temp temperature-seq]
      (let [;; memoize makes it slower by 10%
            ;; score (memoize score-fn)
            prev-score (score-fn @state)
            proposed-move-fn (create-move-fn @state)
            score (score-fn (proposed-move-fn @state))
            dE (- score prev-score)]
        (if (or
             (< dE 0)
             (> (Math/exp (/ (* -1 dE) temp)) (rand)))
          ;; Swapping seem marginally SLOWER than resetting
          (swap! state proposed-move-fn))))
    @state))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; demo
;;
;; sort bigger things into the first half of the vector
;;
;; Not useful but easy to debug

(comment

  (defn move
    "Return a new state with a small modification"
    [state]
    (let [idx1 (rand-int (count state))
          idx2 (rand-int (count state))]
      (switch-elements state idx1 idx2)))

  (defn create-random-move
    "Return a function which closes over randomness.
    Fn creates new state with a small modification."
    [current-state]
    (let [idx1 (rand-int (count current-state))
          idx2 (rand-int (count current-state))]
      (fn [state]
        (switch-elements state idx1 idx2))))

  (defn score
    "Given the state, calculate how well it meets the objective.
    Lowest score is best; minimize
    In this case, take the negative sum of the first half
    ie encourage greater values to sort forward"
    [state]
    (* -1 (r/fold + (first-half state))))

  (let [temp-seq (make-temperature-seq 25000 1 125000)
        initial-state (into [] (range 30))]
    (time (println (run-sa2 initial-state create-random-move score temp-seq)))
    (time (println (run-sa initial-state move score temp-seq)))))
