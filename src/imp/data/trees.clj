(ns imp.data.trees
  ;  (:import org.apache.commons.io.FileUtils )
  ;  (:import java.net.URL)
  ;  (:import java.io.File)
  (:import java.io.FileReader)
  (:import jebl.evolution.io.NexusImporter)
  )

;https://github.com/alandipert/enduro
;http://www.brandonbloom.name/blog/2013/06/26/slurp-and-spit/

(def trees-db (atom
                ; (clojure.java.io/file "trees.tmp")  
                (java.io.File/createTempFile "trees" ".tmp")
                ))


(defn overwrite-trees-db
  "Overwrite the content of trees-db atomically"
  [input]
  (clojure.java.io/copy
    input 
    @trees-db))


(defn delete-trees-db
  "Explicitely delete db file"
  []
  (.delete @trees-db))


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
