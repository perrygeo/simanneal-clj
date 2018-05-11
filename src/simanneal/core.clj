(ns simanneal.core
  (:require
    [simanneal.anneal :refer [run-sa]]
    [simanneal.examples :refer [initial-state move score temps]])
  (:gen-class))

(defn -main
  "run example"
  [& args]
  (println (run-sa initial-state move score temps)))
