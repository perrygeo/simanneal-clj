(ns simanneal.anneal
  (:gen-class))

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
  [initial-state move-fn score-fn temperature-seq & opts]
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
