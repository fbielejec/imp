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
            ; TODO: mock a resource
           (let [file  (slurp "/home/filip/Dropbox/ClojureProjects/imp-rest/resources/WNV_small.trees") ]
             ;; mock a PUT request
             (h/app 
               (-> (mock/request
                     :put
                     "/trees"
                     (json/generate-string { :input file}))
                 (mock/content-type "application/json"))))
             
             ;; check if file got uploaded to server
             (with-open [rdr (clojure.java.io/reader (t/get-trees-db) )] 
               (is
                 (-> (line-seq rdr) 
                   (first )
                   (= "#NEXUS"))))))
