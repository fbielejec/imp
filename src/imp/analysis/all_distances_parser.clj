;;
;;---@fbielejec
;;

(ns imp.analysis.all-distances-parser
  ;  (:require [imp.data.trees :as trees])
  (:require [imp.data.settings :as s])
  (:require [imp.utils.utils :as u])
  (:require [imp.utils.time :as t])
  (:require [imp.analysis.distance-map-parser :as d])
  )


(defn pair-with-key
  "Carry the key over to every value of map"
  [maps]
  (map (fn [ m]
         ( let [k (key m) values (val m) ]
           (map
             (fn [v]
               (hash-map :time k :distance v))
             values)))
       maps))


(defn interleave-n
  "Interleave n trees (first with first, second with second, etc)"
  [coll]
  (let [ ntrees (inc (count (nth coll 0 ))) ]
    (partition ntrees 
               (apply interleave coll ))))


(defn name-value
  "add name and value keys"
  [coll]
  (let [i (atom 0)]
    (map
      (fn [elem]
        (swap! i inc)
        {
         :name (str "tree_" @i )
         :values elem
         }
        )
      coll)))


;; TODO: 3 traversals, limit to 2
(defn frontend-friendly-format 
  "format the data exacly as the D3 frontend expects it"
  [maps]
  (-> maps
    (pair-with-key)
    (interleave-n)
    (name-value)
    ))


(defn format-data
  "format the data to conform to JSON format ready for D3 plotting"
  [maps-vector]
  (->> maps-vector
    (apply u/merge-maps-by-keys)
    (t/dateize-keys (s/get-setting :mrsd))
    (into (sorted-map))
    (frontend-friendly-format)))


(defn parse-all-data
  "Parse, analyze and return all tree distances into formatted JSON, ready for plotting in frontend"
  []
  (let [trees-dist-map  (d/get-trees-dist-map)]
    (format-data trees-dist-map)))


