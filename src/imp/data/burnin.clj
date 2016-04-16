;;
;;---@fbielejec
;;

(ns imp.data.burnin
;  (:use clojure.set)
  (:require [imp.data.trees :as t])
  (:require [imp.analysis.parser :as p])
  )

(defn parse-max-burnin
  "Return max possible burnin which is equal to the number of trees in db minus one"
  []
  (let [trees-file (t/get-trees-db) tree-importer (p/create-tree-importer trees-file) ]
    
    (count (.importTrees tree-importer))
    
    ))