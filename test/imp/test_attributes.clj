;;
;;---@fbielejec
;;

(ns imp.test-attributes
  (:require [clojure.test :refer :all])
  (:require [ring.mock.request :as mock] )
  (:require [clj-json.core :as json] )
  (:require [imp.routes.handler :as h])
  (:require [imp.data.attributes :as a])
  )


(deftest test-attributes
  (let [expected-result (set ["rate" "location"])]
    
    (testing "test location attributes parsing"
                   ;; mock a PUT trees request
             (let [file  (slurp "test/resources/WNV_small.trees") ]
               (h/app 
                 (-> (mock/request
                       :put
                       "/trees"
                       (json/generate-string { :input file}))
                   (mock/content-type "application/json"))))  
             
             ;; test GET on attributes (tests the attributes parser)
             (let [response (h/app (mock/request :get "/attributes"))]
               (->
                 (set (read-string (:body response)))
                 (= expected-result)
                 (is))))))

