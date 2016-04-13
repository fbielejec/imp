(ns imp.data.trees
;  (:require [clojure.java.jdbc :as sql])
;  (:import java.sql.DriverManager)  
  )


(def trees (atom []))

(defn list-trees []
  @trees)


(defn add-trees
  [input]
  (swap! trees conj input)
  input
  )

