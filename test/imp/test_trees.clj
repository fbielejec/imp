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
  
;  (testing "test trees store"
;           (let [t1 "FOO"]
;             (t/add-trees t1)
;             (is (= t1 ( nth (t/list-trees) 0)))))
  
  (testing "test trees DB store"
           
           (let [t1 "FOO"]
             
             (h/app 
               (-> (mock/request
                     :put
                     "/upload"
                     (json/generate-string { :input t1})
                     )
                 (mock/content-type "application/json")
                 ))
             
             
             
             
             (println
               (type
             (t/list-trees)
             )
             )
             
             
             
             )
           
           ))
