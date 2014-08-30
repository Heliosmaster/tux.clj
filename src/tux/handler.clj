(ns tux.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [clojure.tools.logging :as log]
            [hiccup.form :as form]
            [hiccup.page :refer [html5 include-css]]
            [taoensso.carmine :as car :refer [wcar]]
            [compojure.route :as route])
  (:import [org.apache.log4j Logger]))

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

(defn random-string [_]
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
           (include-css "/css/tux.css")
           [:title "Tux.it"]]
          [:body
           [:div.container
            body
            [:img.tux-img {:src "/tux.png"}]]])})

(def not-found-page
  [:p.text
   "Sorry, you did not enter a valid url. "
   [:a {:href "/"} "Home page"]])

(def home
  (form/form-to
   {:role "form" :id "form"} [:post "/"]
   (form/text-field {:placeholder "Long and boring url"} "long-url")
   [:button {:type "submit"} "Tux.it!"]))

(defroutes app-routes
  (route/resources "/")
  (GET "/" [short-url]
       (layout home))

  (POST "/" [long-url]
        (let [short-url (atom "")]
          (swap! short-url random-string)
          (while (= 1 (wcar redis-conn (car/exists @short-url)))
            (swap! short-url random-string))
          (set-mapping @short-url long-url)
          (layout
           [:p.text
            "Your shortened url is: "
            [:a {:href (str "/" @short-url)} (str "http://tux.it/" @short-url)]])))

  (GET "/:id" [id]
       (if-let [long-url (get-mapping id)]
         (redirect-to long-url)
         (layout not-found-page)))

  (route/not-found (layout
                    [:p.text "Uh oh, not found!"
                     [:a {:href "/"} "Home page"]])))

(def app
  (handler/site app-routes))
