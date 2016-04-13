;;
;;---@fbielejec
;;
;
;(ns imp-rest.attributes-parser-test
;  (:require [clojure.test :refer :all])
;  (:require [imp-rest.settings :as s])
;  (:require [imp-rest.attributes :as a])
;  )
;
;(deftest test-parser
;  (testing "attributes parser"
;           (s/put-setting :trees "/home/filip/Dropbox/ClojureProjects/imp-rest/resources/WNV_small.trees")
;           (let [unique-attributes (a/parse-attributes )]
;             
;             ;             (println
;             ;               unique-attributes
;             ;               )
;             
;             (is (pos? (count unique-attributes))))))
;
