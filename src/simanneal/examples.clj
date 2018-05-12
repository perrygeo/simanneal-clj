(ns simanneal.examples
  (:require [clojure.core.reducers :as r]
            [simanneal.anneal :refer [make-temperature-seq]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Utils

(defn switch-elements
  "Switch 2 elements in a vector by index"
  ;; https://stackoverflow.com/a/5980031/519385
  ;; Note: really makes me appreciate immutable vectors!
  [v i1 i2]
  (assoc v
         i2 (v i1)
         i1 (v i2)))

(defn first-half
  "Cut the list roughly in half"
  [v]
  (take (int (/ (count v) 2)) v))

; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; ;; demo
; ;;
; ;; sort bigger things into the first half of the vector
; ;;
; ;; Not useful but easy to debug

; (def initial-state (into [] (range 1000)))

; (defn move
;   "Return a new state with a small modification"
;   [state]
;   (let [idx1 (rand-int (count state))
;         idx2 (rand-int (count state))]
;     (switch-elements state idx1 idx2)))

; (defn score
;   "Given the state, calculate how well it meets the objective.
;   Lowest score is best; minimize

;   In this case, take the negative sum of the first half
;   ie optimize so that greater values sort to the first half"
;   [state]
;   (* -1 (r/fold + (first-half state))))

; (def temps
;   (make-temperature-seq 2500.0 0.1 50000))

;; (run-sa initial-state move score temps)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Travelling Salesman Problem
;
; Simulating nodes of a graph for which we'll calculate
; the shortest tour visiting all points

;; Domain-specific functions

(defn random-point
  "Random 2D point vector"
  []
  [(rand) (rand)])

(defn distance
  "Take two 2D point vectors and calculate euclidean distance"
  [[x1 y1] [x2 y2]]
  (let [dx (- x2 x1)
        dy (- y2 y1)]
    (Math/sqrt (+ (* dx dx) (* dy dy)))))

;; Annealing inputs

(def initial-state
  (into [] (take 50 (repeatedly random-point))))

(defn move
  "Return a new tour with a small random modification"
  [tour]
  (let [idx1 (rand-int (count tour))
        idx2 (rand-int (count tour))]
    (switch-elements tour idx1 idx2)))

(defn score
  "Calculate the total distance of the tour.
  Lowest score is best; minimize"
  [tour]
  (reduce
   + (for [[pt1 pt2] (partition 2 1 tour)]
       (distance pt1 pt2))))

(def temps
  (make-temperature-seq 25.0 0.1 50000))
