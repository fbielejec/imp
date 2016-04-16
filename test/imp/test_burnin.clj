;;
;;---@fbielejec
;;

(ns imp.test-burnin
  (:require [clojure.test :refer :all])
  (:require [ring.mock.request :as mock] )
  (:require [clj-json.core :as json] )
  (:require [imp.routes.handler :as h])
  (:require [imp.data.trees :as t])
  (:require [imp.data.burnin :as b])
  )


(deftest test-burnin
  (testing "test max burnin attribute parsing"
           
           ;; mock a PUT trees request
           (let [file  (slurp "/home/filip/Dropbox/ClojureProjects/imp-rest/resources/WNV_small.trees") ]
             (h/app 
               (-> (mock/request
                     :put
                     "/trees"
                     (json/generate-string { :input file}))
                 (mock/content-type "application/json"))))        
           
           ;; test GET on burnin (tests the burnin parser)
           (let [expected-result 10 response (h/app (mock/request :get "/burnin"))]
             
             (->
               (read-string (:body response))
               (= expected-result)
               (is))
             
             )))

