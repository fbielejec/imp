;;
;;---@fbielejec
;;

(ns imp.test-trees
  (:require [clojure.test :refer :all])
    (:require [ring.mock.request :as mock] )
  (:require [clj-json.core :as json] )
  (:require [imp.data.trees :as t])
  (:require [imp.routes.handler :as h])
  )


(deftest test-trees
  
  (testing "test trees file uploading"
           
           (let [file  (slurp "/home/filip/Dropbox/ClojureProjects/imp-rest/resources/WNV_small.trees") ]
             
             (h/app 
               (-> (mock/request
                     :put
                     "/upload"
                     (json/generate-string { :input file}))
                 (mock/content-type "application/json")))
             
             
             
             
             
             )
           
           ))
