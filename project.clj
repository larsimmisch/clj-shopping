(defproject clj-shopping "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ring "0.8.10"]]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-jetty-adapter "1.2.1"]
                 [compojure "1.1.6"]]
  :ring {:handler clj-shopping.handler/app
       :nrepl {:start? true
               :port 9998}}
  :profiles {:dev {:dependencies [[ring/ring-mock "0.3.0"]]}})