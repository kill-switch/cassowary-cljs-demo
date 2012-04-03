(defproject cassowary-cljs-demo "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.3.0"]

                 [com.keminglabs/c2 "0.1.0-beta1"]
                 [com.keminglabs/cassowary "0.1.0"]]

  :plugins [[lein-cljsbuild "0.1.4"]]
  
  :cljsbuild {:builds
              [{:source-path "src/cljs"
                :compiler {:pretty-print true
                           :output-to "public/main.js"
                           :libs ["cassowaryjs"]
                           :optimizations :simple}}]})


