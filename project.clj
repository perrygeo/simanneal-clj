(defproject simanneal "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/core.async "0.4.474"]]
  :plugins [[jonase/eastwood "0.2.6"]]
  :main ^:skip-aot simanneal.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
