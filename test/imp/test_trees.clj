;;
;;---@fbielejec
;;

(ns imp.test-trees
  (:require [clojure.test :refer :all])
  (:require [imp.data.trees :as t])
  )


(deftest test-trees
  
    (testing "test trees store"
           
             (t/add-trees "FOO")

             (println
               (t/list-trees)
               )
             
   )
  
;  (testing "test trees DB store"
;)
  
  )
