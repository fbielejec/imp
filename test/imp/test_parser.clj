;;
;;---@fbielejec
;;

(ns imp.test-parser
  (:require [clojure.test :refer :all])
  (:require [ring.mock.request :as mock] )
  (:require [clj-json.core :as json] )
  (:require [imp.utils.utils :as u])
  (:require [imp.data.trees :as t])
  (:require [imp.data.settings :as s])
  (:require [imp.routes.handler :as h])
  (:require [imp.analysis.mean-distance-parser :as pm])
  (:require [imp.analysis.all-distances-parser :as pa])
  )


(defn get-first-date-key 
  "Return the first date-key from the n-th entry of results map"
  [coll n]
  (->
    (nth coll n)
    (:values)
    (first)
    (:time)))


(deftest test-parser
  (testing "test parser"
           ;; mock inputs
           (let [input  (slurp "test/resources/WNV_small.trees") s1 "location" s2 1 s3 10 s4 2005.3]
             (t/overwrite-trees-db input)
             (s/put-setting :attribute s1)
             (s/put-setting :burnin s2)
             (s/put-setting :nslices s3)
             (s/put-setting :mrsd s4))
           
           
           ;; --- DATA ALL --- ;;
           
           
           (testing "call to get-data-all fills the atom for future GET calls"
                    (pa/get-data-all)
                    (is (-> (deref pa/data-all)
                          (empty?)
                          (not))))
           
           
           (testing "date-keys in all results map have the same order"
                    (let [result (pa/get-data-all) first-from-first (get-first-date-key result 0) first-from-last (get-first-date-key result (dec (count result))) ]
                      (is (= first-from-first first-from-last ))))
           
           
           (testing "GET all results"
                    (let [response (h/app (mock/request :get "/data/all"))]
                      (is (= (:status response) 200))))
           
           
           ;; --- DATA MEAN --- ;;
           
           
           (testing "call to get-data-mean fills the atom for future GET calls"
                    (pm/get-data-mean)
                    (is (-> (deref pm/data-mean)
                          (empty?)
                          (not))))
           
           
           (testing "mean distances map has correct number of sices"
                    (let [results (pm/get-data-mean)]
                      (is (= (count results) (s/get-setting :nslices)))))
           
           
           (testing "GET mean results"
                    (let [response (h/app (mock/request :get "/data/mean"))]
                      (is (= (:status response) 200))))
           
           
           ))
















