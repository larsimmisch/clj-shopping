(ns clj-shopping.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [clj-shopping.handler :refer :all]))


(deftest test-app

  (testing "list endpoint"
    (let [response (app (request :get "/list"))]
      (is (= (:status response) 200))
      (is (= (get-in response [:headers "Content-Type"]) "application/json"))))

  (testing "not-found route"
    (let [response (app (request :get "/bogus-route"))]
      (is (= (:status response) 404))))
  )
