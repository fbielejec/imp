;;
;;---@fbielejec
;;

(ns imp.test-ntrees
  (:require [clojure.test :refer :all])
  (:require [ring.mock.request :as mock] )
  (:require [clj-json.core :as json] )
  (:require [imp.routes.handler :as h])
  (:require [imp.data.trees :as t])
;  (:require [imp.data.ntrees :as n])
  )


(deftest test-ntrees
  (testing "test ntrees parsing"
           
           ;; mock a PUT trees request
           (let [file  (slurp "test/resources/WNV_small.trees") ]
             (h/app 
               (-> (mock/request
                     :put
                     "/trees"
                     (json/generate-string { :input file}))
                 (mock/content-type "application/json"))))        
           
           ;; test GET on burnin (tests the ntrees parser)
           (let [expected-result 10 response (h/app (mock/request :get "/ntrees"))]
             
             (->
               (read-string (:body response))
               (= expected-result)
               (is))
             
             )))

