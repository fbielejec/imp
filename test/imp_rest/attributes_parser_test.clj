;;
;;---@fbielejec
;;

(ns imp-rest.attributes-parser-test
  (:require [clojure.test :refer :all])
  (:require [imp-rest.settings :as s])
  (:require [imp-rest.attributes-parser :as p])
  )

(deftest test-parser
  (testing "attributes parser"
           
           (s/put-setting :filename "/home/filip/Dropbox/ClojureProjects/imp-rest/resources/WNV_small.trees")
           
           (let [unique-attributes (p/parse-attributes )]
             
             (println
               unique-attributes
               )
             
             (is (pos? (count unique-attributes) ) )       
             
             )
           
           ))

