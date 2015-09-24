(defproject tux "0.1.0-SNAPSHOT"
  :description "Simple URL shortener in Clojure"
  :url "http://tux.it"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [hiccup "1.0.5"]
                 [org.slf4j/slf4j-log4j12 "1.7.5"]
                 [org.clojure/tools.logging "0.3.0"]
                 [com.taoensso/carmine "2.11.1"]
                 [compojure "1.4.0"]]
  :plugins [[lein-ring "0.9.6"]]
  :ring {:handler tux.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
