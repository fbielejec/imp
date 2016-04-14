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
    
    ;; --- REGULAR TESTS---;;
    
    (testing "test location attributes parsing"
             (let [file  (slurp "/home/filip/Dropbox/ClojureProjects/imp-rest/resources/WNV_small.trees") ]
               ;; mock a PUT request
               (h/app 
                 (-> (mock/request
                       :put
                       "/trees"
                       (json/generate-string { :input file}))
                   (mock/content-type "application/json"))))  
             
             ;; parse attributes 
             (let [result (a/parse-attributes)]
               (->
                 (set result)
                 (= expected-result)
                 (is))))
    
    ;; --- REST TESTS---;;
    
    (testing "test location attributes GET request"
             (let [response (h/app (mock/request :get "/attributes"))]
               (->
                 (set (read-string (:body response)))
                 (= expected-result)
                 (is))))))

