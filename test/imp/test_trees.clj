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
  
    (testing "test error handling when uploading .trees file"
           (let [file  (slurp "test/resources/lorem_ipsum.tree") ]

             ;; upload file to db
             (t/handle-upload file)
             
                 ;; mock GET unique set of attributes
                          (let [response (h/app (mock/request :get "/attributes"))]
               
                   (is (= (:status response) 404))
               
               )
             
             
             )
             
             )
  
  (testing "test trees file uploading"
           (let [file  (slurp "test/resources/WNV_small.trees") ]
             ;; mock a PUT request
             (h/app 
               (-> (mock/request
                     :put
                     "/trees"
                     (json/generate-string { :input file}))
                 (mock/content-type "application/json"))))
             
             ;; check if file got uploaded to db
             (with-open [rdr (clojure.java.io/reader (t/get-trees-db) )] 
               (is
                 (-> (line-seq rdr) 
                   (first )
                   (= "#NEXUS")))))
  
  )
