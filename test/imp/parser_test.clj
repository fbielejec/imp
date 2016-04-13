;;
;;---@fbielejec
;;
;
;(ns imp-rest.parser-test
;  (:require [clojure.test :refer :all])
;  (:require [imp-rest.settings :as s])
;  (:require [imp-rest.parser :as p])
;  )
;
;(deftest test-parser
;  (testing "parser"
;           (s/put-setting :trees "/home/filip/Dropbox/ClojureProjects/imp-rest/resources/WNV_small.trees")
;           (s/put-setting :coordinateName "location")
;           (s/put-setting :burnin 1)
;           (s/put-setting :nslices 10)
;           (s/put-setting :mrsd 2005.3)
;           (p/parse-data )
;           
;           ;; TODO: silly
;           (is (= 1 1) )       
;           
;           ))
;
