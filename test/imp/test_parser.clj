;;
;;---@fbielejec
;;

(ns imp.test-parser
  (:require [clojure.test :refer :all])
  (:require [ring.mock.request :as mock] )
  ;  (:require [clj-json.core :as json] )
  (:require [imp.data.trees :as t])
  (:require [imp.data.settings :as s])
  (:require [imp.analysis.parser :as p])
  (:require [imp.routes.handler :as h])
  )


(deftest test-parser
  (testing "test parser"
           ;; mock inputs
           (let [input  (slurp "/home/filip/Dropbox/ClojureProjects/imp-rest/resources/WNV_small.trees") s1 "location" s2 1 s3 10 s4 2005.3]
             (t/overwrite-trees-db input)
             (s/put-setting :attribute s1)
             (s/put-setting :burnin s2)
             (s/put-setting :nslices s3)
             (s/put-setting :mrsd s4))
           
           (testing "GET results"
                    (let [response (h/app (mock/request :get "/results"))]
                      ;             (println (:body response))
                      (is (= (:status response) 200))))))