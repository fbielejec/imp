;;
;;---@fbielejec
;;

(ns imp-rest.parser-test-rest
  (:require [clojure.test :refer :all])
  (:require [ring.mock.request :as mock] )
  (:require [clj-json.core :as json] )
  (:require [imp-rest.web :as w])
  (:require [imp-rest.settings :as s])
  (:require [imp-rest.utils :as u]) 
  )

(def escaped-response-string "{\"filename\":\"/home/filip/Dropbox/ClojureProjects/imp-rest/resources/WNV_small.trees\",\"coordinateName\":\"location\",\"burnin\":1,\"nslices\":10,\"mrsd\":2015.3}" )

(deftest test-parser-rest
  (testing "put settings"
           
           (w/app 
             (-> (mock/request
                   :put
                   "/settings"
                   (json/generate-string {:id "filename" :value "/home/filip/Dropbox/ClojureProjects/imp-rest/resources/WNV_small.trees"}))
               (mock/content-type "application/json")))
           
           (w/app 
             (-> (mock/request
                   :put
                   "/settings"
                   (json/generate-string {:id "coordinateName" :value "location"}))
               (mock/content-type "application/json")))
           
           (w/app 
             (-> (mock/request
                   :put
                   "/settings"
                   (json/generate-string {:id "burnin" :value 1}))
               (mock/content-type "application/json")))
           
           (w/app 
             (-> (mock/request
                   :put
                   "/settings"
                   (json/generate-string {:id "nslices" :value 10}))
               (mock/content-type "application/json")))
           
           (w/app 
             (-> (mock/request
                   :put
                   "/settings"
                   (json/generate-string {:id "mrsd" :value 2015.3}))
               (mock/content-type "application/json")))          
           
           
           (let [response (w/app (mock/request :get "/settings"))]
             
             (println 
               (:body response))
             
             (is (= ( :body response ) escaped-response-string ))))
  
  (testing "get results"
           (let [response (w/app (mock/request :get "/data"))]
             
             (u/p-print
               (u/from-json
                 (:body response)))
             
             (is (= 1 1))               
             
             )))

