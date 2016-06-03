;;
;;---@fbielejec
;;

(ns imp.data.attributes
  (:use clojure.set)
  (:require [imp.data.trees :as t])
;  (:require [imp.analysis.distance-map-parser :as pm])
  )

(defn parse-attributes
  "Return a unique set of attribute names"
  []
  (let [trees-file (t/get-trees-db) tree-importer (t/create-tree-importer trees-file) tree (.importNextTree tree-importer)]
    (reduce
      (fn [unique-attributes node]
        (if (not (.isRoot tree node))
          ( union unique-attributes
                  (set (.getAttributeNames node)))))
      (hash-set) ;; initial
      (set  (.getNodes tree)))))