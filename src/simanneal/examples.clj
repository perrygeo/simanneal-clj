(ns simanneal.examples
  (:require [clojure.core.reducers :as r]))

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
