(ns simanneal.core
  (:require
   [simanneal.anneal :refer [run-sa make-temperature-seq]]
   [simanneal.examples :refer [initial-state move score]])
  (:gen-class))

(defn -main
  "run example"
  [& args]
  (let [temps (make-temperature-seq 25.0 0.1 50000)]
    (println (run-sa initial-state move score temps))))
