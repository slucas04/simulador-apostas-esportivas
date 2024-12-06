(defproject av3funcional "0.1.0-SNAPSHOT"
  :description "Projeto para interação com API utilizando Clojure"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [clj-http "3.12.3"]
                 [cheshire "5.10.1"]
                 [org.clojure/data.json "2.5.0"]
                 [environ "1.2.0"]
                 [ring/ring-core "1.9.6"]
                 [ring-cors "0.1.13"]
                 [ring/ring-jetty-adapter "1.9.6"]
                 [ring/ring-json "0.5.1"] ;; Middleware JSON
                 [compojure "1.6.3"]]
  :main ^:skip-aot av3funcional.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
