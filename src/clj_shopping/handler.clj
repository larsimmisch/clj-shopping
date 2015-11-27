(ns clj-shopping.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :refer :all]))

(defn list-handler
  [req]
  {
   :status  200
   :headers {"Content-Type" "application/json"}
   :body    {"Supermarkt"  ["Coleman's Senf" "Milch"]
             "Wochenmarkt" ["Kohlrabi"]
             }
   })

(defroutes app-routes
           (GET "/list" [] list-handler)
           (route/not-found {:message "Page not found"}))

(defn wrap-log-request [handler]
  (fn [req]
    (println req)
    (handler req)))

(def app
  (-> app-routes
      wrap-log-request
      wrap-json-response
      wrap-json-body))
