;
;---@fbielejec
;

(ns imp.test-settings
  (:require [clojure.test :refer :all])
  (:require [ring.mock.request :as mock] )
  (:require [clj-json.core :as json] )
  (:require [imp.routes.handler :as h])
  (:require [imp.data.settings :as s])
  ;  (:require [imp.utils.utils :as u]) 
  )


;; --- REGULAR TESTS---;;

(deftest test-settings
  (testing "Testing settings"
           (let [s1 "location" s2 1 s3 10 s4 2005.3 ]
             
             (s/put-setting :coordinateName s1)
             (s/put-setting :burnin s2)
             (s/put-setting :nslices s3)
             (s/put-setting :mrsd s4)
             
             (is (= s1 (s/get-setting :coordinateName)))              
             (is (= s2 (s/get-setting :burnin)))   
             (is (= s3 (s/get-setting :nslices)))   
             (is (= s4 (s/get-setting :mrsd)))))
  
  (testing "Testing non-existing setting"
           (let [s1 "bar"]
             
             (s/put-setting :foo s1)
             ; this should not accept new key
             (is (= nil (s/get-setting :foo))))))

;; --- REST INTERFACE TESTS---;;

(deftest test-settings-rest
  (testing "Testing REST interface for settings"
           (let [s1 "location" s2 1 s3 10 s4 2005.3 ] 
             
             
             (h/app 
               (-> (mock/request
                     :put
                     "/settings"
                     (json/generate-string {:id "coordinateName" :value s1}))
                 (mock/content-type "application/json")))         
             
             (h/app 
               (-> (mock/request
                     :put
                     "/settings"
                     (json/generate-string {:id "burnin" :value s2}))
                 (mock/content-type "application/json")))
             
             (h/app 
               (-> (mock/request
                     :put
                     "/settings"
                     (json/generate-string {:id "nslices" :value s3}))
                 (mock/content-type "application/json")))
             
             (h/app 
               (-> (mock/request
                     :put
                     "/settings"
                     (json/generate-string {:id "mrsd" :value s4}))
                 (mock/content-type "application/json")))          
             
             (is (= s1 (s/get-setting :coordinateName)))              
             (is (= s2 (s/get-setting :burnin)))   
             (is (= s3 (s/get-setting :nslices)))   
             (is (= s4 (s/get-setting :mrsd)))))
  
  (testing "get response"
           (let [response (h/app (mock/request :get "/settings"))]
             
             (is (= (:status response) 200)))))

