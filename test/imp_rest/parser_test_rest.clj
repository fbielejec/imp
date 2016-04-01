;;
;;---@fbielejec
;;

(ns imp-rest.parser-test-rest
  (:require [clojure.test :refer :all])
  (:require [ring.mock.request :as mock] )
  (:require [clj-json.core :as json] )
  (:require [imp-rest.web :as w])
  (:require [imp-rest.settings :as s])
  
  )

(defn rf ([] {}) ([acc [k v]] (assoc acc k v)))

(deftest test-parser-rest
  (testing "put settings"
           
;           (w/app 
;             (-> 
;               (mock/request
;                 :put
;                 "/settings/coordinateName"
;                 (json/generate-string {:value "FOO"}))
;               
;               (mock/content-type "application/json")))
           
(w/app 
  (-> (mock/request
        :put
        "/settings"
        (json/generate-string {:id "coordinateName" :value "FOO"}))
      (mock/content-type "application/json")))

           
           (let [response (w/app (mock/request :get "/settings"))]
             
             (println 
               
                 (:body response)  
               
               )
             
             (is (=  ( :body response )  "{\"filename\":null,\"coordinateName\":\"FOO\",\"burnin\":null,\"nslices\":null,\"mrsd\":null}" ) )  
             
             )))

