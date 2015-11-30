(ns clj-shopping.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [clj-shopping.handler :refer :all]
            [cheshire.core :as json]))

(defn json-body
  [response]
  (json/parse-string (:body response)))

(deftest test-operations
  (testing "add to list"
    (is (= (add-to-list {} "Supermarkt" "Senf") {"Supermarkt" ["Senf"]}))
    ;; Don't add duplicates
    (is (= (add-to-list {"Supermarkt" ["Senf"]} "Supermarkt" "Senf") {"Supermarkt" ["Senf"]}))
    (is (= (add-to-list {"Supermarkt" ["Senf"]} "Supermarkt" "Milch") {"Supermarkt" ["Senf" "Milch"]}))
    )

  (testing "done from list"
    (is (= (done-from-list {"Supermarkt" ["Milch" "Senf"]} "Supermarkt" "Senf") {"Supermarkt" ["Milch"]}))
    (is (= (done-from-list {"Supermarkt" ["Senf"]} "Supermarkt" "Senf") {}))
    )

  (testing "operations on the shopping-list ref"
    (dosync (ref-set shopping-list {}))
    (is (= (list-op add-to-list "Supermarkt" "Milch")) {"Supermarkt" ["Milch"]} )
    (is (= (list-op done-from-list "Supermarkt" "Milch")) {} )
    )
  )

(deftest test-app
  (testing "list patch endpoint"
    (dosync (ref-set shopping-list {}))
    (let [response (app (request :patch "/list"))]
      (is (= (:status response) 422))
      )
    (let [response (app (request :patch "/list" {"action" "add"}))]
      (is (= (:status response) 422))
      )
    (let [response (app (request :patch "/list" {"action" "add", "shop" "Supermarkt"}))]
      (is (= (:status response) 422))
      )
    (let [response (app (request :patch "/list" {"action" "add", "shop" "Supermarkt", "item", "Senf"}))]
      (is (= (:status response) 200))
      )
    )

  (testing "list get endpoint"
    (dosync (ref-set shopping-list {"Supermarkt" ["Senf"]}))
    (let [response (app (request :get "/list"))]
      (is (= (:status response) 200))
      (is (= (get-in response [:headers "Content-Type"]) "application/json"))
      (is (= (json-body response) {"Supermarkt" ["Senf"]}))
      )
    )

  (testing "list operations in sequence"
    (dosync (ref-set shopping-list {"Supermarkt" ["Senf"]}))
    (let [response (app (request :patch "/list" {"action" "add", "shop" "Supermarkt", "item", "Senf"}))]
      (is (= (:status response) 200)))
    (let [response (app (request :get "/list"))]
      (is (= (json-body response) {"Supermarkt" ["Senf"]}))
      )
    (let [response (app (request :patch "/list" {"action" "done", "shop" "Supermarkt", "item", "Senf"}))]
      (is (= (:status response) 200)))
    (let [response (app (request :get "/list"))]
      (is (= (json-body response) {}))
      )
    )

    (testing "not-found route"
    (let [response (app (request :get "/bogus-route"))]
      (is (= (:status response) 404))))
  )
