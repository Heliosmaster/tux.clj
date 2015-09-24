(ns tux.test.handler
  (:require [clojure.test :refer :all]
            [tux.handler :refer :all]
            [ring.mock.request :as mock]))

(deftest test-helpers
  (testing "valid-url?"
    (is (valid-url? "https://tux.it"))
    (is (valid-url? "http://tux.it"))
    (is (not (valid-url? "tux.it")))))

(deftest test-database
  (testing "setting and getting a value"
    (let [long-url "https://tux.it"
          short-url "abc"]
      (set-mapping short-url long-url)
      (is (= long-url (get-mapping short-url))))))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200)))))
