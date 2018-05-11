(ns simanneal.core
  (:gen-class))

(defn swap
  ;; https://stackoverflow.com/a/5980031/519385
  ;; Note: really makes me appreciate immutable vectors!
  ;; Input must be a vector, otherwise with lists
  ;; ClassCastException clojure.lang.LongRange cannot be cast to clojure.lang.IFn  simanneal.core/swap (core.clj:8)
  [v i1 i2]
  (assoc v i2 (v i1)
           i1 (v i2)))

(defn move
  "Return a new state with a small modification"
  [state]
  (let [idx1 (rand-int (count state))
        idx2 (rand-int (count state))]
    (swap state idx1 idx2)))

(defn first-half [v]
  (take (int (/ (count v) 2)) v))

(defn score
  "Given the state, calculate how well it meets the objective
  Lowest score is best; objective is to minimize."
  [state]
  (* -1 (reduce + 0 (first-half state))))

(defn greedy
  "run the greedy algorithm - only accept improvements"
  [move-fn score-fn state & opts]
  (let [temp (atom 2000)
        best-score (atom 0)]
    (while (> @temp 0)
      (let [proposed (move-fn @state)
            score (score-fn proposed)]
        (swap! temp dec)
        (if (< score @best-score)
          (do
            (reset! best-score score)
            (reset! state proposed)
            (println @temp @state @best-score)))))))


(defn make-temperature-seq
  "produce an expontential cooling schedule"
  [tmin tmax steps]
  (let [tfactor (* -1 (Math/log (/ tmax tmin)))]
    (for [step (range steps)]
      (* tmax (Math/exp (* tfactor (/ step steps)))))))


(defn anneal
  "Run simulated annealing.
  Repeatedly apply the move-fn to state, creating new states.
  Change to the proposed state if either one of two conditions occur
  - The proposed solution scores better
  - The proposed solution scores worse but within range defined by temperature + randomness"
  [move-fn score-fn initial-state temperature-seq & opts]
  (let [state (atom initial-state)]
    (doseq [temp temperature-seq]
      (let [prev-score (score-fn @state)
            proposed (move-fn @state)
            score (score-fn proposed)
            dE (- score prev-score)]
        (if (or
              ;; score dropped, improvement
              (< dE 0)                                  
              ;; or no improvement but within temperature
              (> (Math/exp (/ (* -1 dE) temp)) (rand)))
          (do
            (reset! state proposed)))))
      @state))

(def initial-state (apply vector (range 20)))
(def temps (make-temperature-seq 0.1 2500.0 5000))

(defn -main
  "run it"
  [& args]
  (anneal move score initial-state temps))
