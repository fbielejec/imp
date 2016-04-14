(ns imp.data.trees
  (:import java.io.FileReader)
  (:import jebl.evolution.io.NexusImporter)
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
  "Explicitely delete db-trees file"
  []
  (.delete @trees-db))


(defn get-trees-db
  "Return the current content of db-trees file"
  []
  @trees-db)


(defn handle-upload
  "handle incoming "
  ; TODO: validate content? (does first line contain #NEXUS)
  [input]
  (overwrite-trees-db input))

;     (let [tree-importer
;         (->> @trees-db
;           (new FileReader )
;           (new NexusImporter))
;     tree (.importNextTree tree-importer)    ]
;     )
