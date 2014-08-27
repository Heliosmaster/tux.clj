(ns tux.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [clojure.tools.logging :as log]
            [hiccup.form :as form]
            [hiccup.page :refer [html5]]
            [taoensso.carmine :as car :refer [wcar]]
            [compojure.route :as route]))

(def redis-conn {:pool {} :spec {}})

(def valid-chars
  (map char (sort (concat
                   [93 95 126] ; ] _ ~
                   (range 48 58) ; 0-9 :
                   (range 60 64) ; < = > ?
                   (range 64 92) ; @ A-Z [
                   (range 97 123) ; a-z
                   (range 33 37) ; ! " # $
                   (range 38 47) ; & ' ( ) * + , - .
                   )))) ; 84 usable chars

(def string-length 3) ; 84^3 = 592704

(defn random-valid-char []
  (nth valid-chars (.nextInt (java.util.Random.) (count valid-chars))))

(defn random-string []
  (apply str (repeatedly string-length random-valid-char)))

(defn redirect-to [path]
  {:status 302
   :headers {"Location" path}})

(defn set-mapping [short-url long-url]
  (wcar redis-conn (car/set short-url long-url))
  (wcar redis-conn (car/expire short-url 15778463)))

(defn get-mapping [short-url]
  (wcar redis-conn (car/get short-url)))

(defn layout [body]
  {:status 200
   :body (html5
          [:head
           [:title "Tux.it"]]
          [:body
           body])})

(defn render-home [short-url]
  (if short-url
    [:h2 (str "HELLO " short-url)])
  (form/form-to
   {:role "form"} [:post "/"]
   (form/text-field "long-url")
   [:button {:type "submit"} "Tux.it!"]))

(defroutes app-routes
  (GET "/" [short-url]
       (layout (render-home short-url)))

  (POST "/" [long-url]
        (let [short-url (random-string)]
          (set-mapping short-url long-url)
          (str "Your shortened url is: " "http://tux.it/" short-url)))

  (GET "/:id" [id]
       (if-let [long-url (get-mapping id)]
         (redirect-to long-url)
         "Sorry, no shortener found"))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
