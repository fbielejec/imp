;;
;;---@fbielejec
;;

(ns imp.data.ntrees
;  (:use clojure.set)
  (:require [imp.data.trees :as t])
;  (:require [imp.analysis.distance-map-parser :as pm])
  )

(defn get-ntrees
  "Return the number of trees in db"
  []
  (let [trees-file (t/get-trees-db) tree-importer (t/create-tree-importer trees-file) ]
    (count (.importTrees tree-importer))))