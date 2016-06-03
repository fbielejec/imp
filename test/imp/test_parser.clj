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
  (:require [imp.analysis.parser :as p])
  (:require [imp.routes.handler :as h])
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
           (let [input  (slurp "/home/filip/Dropbox/ClojureProjects/imp-rest/resources/WNV_small.trees") s1 "location" s2 1 s3 10 s4 2005.3]
             (t/overwrite-trees-db input)
             (s/put-setting :attribute s1)
             (s/put-setting :burnin s2)
             (s/put-setting :nslices s3)
             (s/put-setting :mrsd s4))
           
           
           (testing "date-keys in results map have the same order"
                    (let [result (p/parse-data) first-from-first (get-first-date-key result 0) first-from-last (get-first-date-key result (dec (count result))) ]
                      (is (= first-from-first first-from-last ))))
           
           (testing "GET results"
                    (let [response (h/app (mock/request :get "/results"))]
                      (is (= (:status response) 200))))
           
           
           
           (testing "mean distances"
                    (let [results (p/parse-mean-data)]
                      
                      
                      
                      )
                    )
           
           ))
















