(ns clj-shopping.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :refer :all]
            [ring.middleware.params :refer :all]
            [cheshire.core :as json]))

(def shopping-list (ref nil))

(defn init
  []
  (dosync
    (ref-set shopping-list (json/parse-stream (clojure.java.io/reader "shopping.json")))
  ))

(defn contains-value? [coll element]
  (boolean (some #(= element %) coll)))

(defn status-response
  [status body]
  {
   :status  status
   :headers {"Content-Type" "application/json"}
   :body    body
   })


(defn done-from-list
  [list shop item]
  (if-let [items (list shop)]
    (let [newitems (filter #(not= item %) items)]
      ;; remove empty shop mapping
      (if (empty? newitems)
        (dissoc list shop)
        (assoc list shop newitems)
        )
      )
    list
    )
  )

(defn add-to-list
  [list shop item]
  (let [items (or (list shop) [])]
    ;; don't add the same item twice
    ;; - if we used distinct, we'd convert the vector to a sequence
    (if (contains-value? items item)
      list
      (assoc list shop (conj items item)))
    )
  )

(defn list-op
  [op shop item]
  (dosync
    (alter shopping-list op shop item)
    )
  )

(defn list-patch-handler
  [{{action "action" shop "shop" item "item"} :params}]
  (cond
    (not (and shop item)) (status-response 422 {:message "Invalid/missing shop/item"})
    (= action "add") (status-response 200 ((partial list-op add-to-list) shop item))
    (= action "done") (status-response 200 ((partial list-op done-from-list) shop item))
    :else (status-response 422 {:message "Invalid/missing action"}))
  )

(defn list-handler
  [_]
  (status-response 200 @shopping-list)
  )

(defroutes app-routes
           (GET "/list" [] list-handler)
           (PATCH "/list" [] list-patch-handler)
           (route/not-found {:message "Page not found"}))

(defn wrap-log-request [handler]
  (fn [req]
    (println req)
    (handler req)))

(def app
  (-> app-routes
      wrap-log-request
      wrap-params
      wrap-json-response
      wrap-json-body))
