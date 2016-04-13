;;
;;---@fbielejec
;;

(ns imp-rest.attributes
  (:use clojure.set)
  (:require [imp-rest.settings :as s])
  (:require [imp-rest.parser :as p])
  )

(defn parse-attributes
  "Return a unique set of attribute names"
  []
  (let [treefilename (s/get-setting :trees) tree-importer (p/create-tree-importer treefilename) tree (.importNextTree tree-importer)]
    (reduce
      (fn [unique-attributes node]
        (if (not (.isRoot tree node))
          ( union unique-attributes
                  (set (.getAttributeNames node)))))
      (hash-set) ;; initial
      (set  (.getNodes tree)))))