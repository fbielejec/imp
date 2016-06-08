(ns imp.data.trees
  (:import java.io.FileReader)
  (:import jebl.evolution.io.NexusImporter)
;  (:require [imp.analysis.clear-atoms :as ca])
  )


(def trees-db (atom
                ; (clojure.java.io/file "trees_db.tmp")  
                (java.io.File/createTempFile "trees_db" ".tmp")))


(defn overwrite-trees-db
  "Overwrite the content of trees-db atomically"
  [input]
  (clojure.java.io/copy
    input 
    @trees-db))


(defn delete-trees-db
  "Explicitely delete db-trees file and clear all atomic singletons in the analysis package"
  []
  (.delete @trees-db)
;  (ca/clear-all)
  )


(defn get-trees-db
  "Return the current content of db-trees file"
  []
  @trees-db)


(defn handle-upload
  "handle incoming "
  [input]
  (overwrite-trees-db input))


;;---CREATE TREE IMPORTER---;;

(defn create-tree-importer
  "Create JEBL tree importer"
  [file]
  (->> file
    (new FileReader )
    (new NexusImporter)))

