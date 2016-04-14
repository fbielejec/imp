;;
;;---@fbielejec
;;

(ns imp.test-attributes
  (:require [clojure.test :refer :all])
  (:require [ring.mock.request :as mock] )
  (:require [clj-json.core :as json] )
  (:require [imp.routes.handler :as h])
  (:require [imp.data.attributes :as a])
  (:use clojure.data)
  )



(deftest test-attributes
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
           (is
             (->
               (a/parse-attributes)
               (= #{"rate" "location"})))))

