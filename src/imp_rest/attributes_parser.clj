;;
;;---@fbielejec
;;

(ns imp-rest.attributes-parser
  (:require [imp-rest.settings :as s])
  (:require [imp-rest.parser :as p])
  )

;; TODO: get unique attribute names

(defn parse-attributes
  "Return a unique set of attribute names"
  []
  (let [treefilename (s/get-setting :filename) tree-importer (p/create-tree-importer treefilename) tree (.importNextTree tree-importer)]
    
    (reduce
      (fn [unique-attributes node]
        
        (if (not (.isRoot tree node))
          
          (conj unique-attributes  (.getAttributeNames node) )
          
          )
        
        )
      (hash-set) ;; initial
      (set  (.getNodes tree)) ;; coll
      
      )
    
    )
  )