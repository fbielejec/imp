;;
;;---@fbielejec
;;

(ns imp-rest.utils
  (:require [clojure.pprint :refer (pprint)]  )
  (:require [clojure.data.json :as json] )
  (:require [imp-rest.time :as t])
  )


(defn p-print [coll]
  "Pretty-prints a data structure"
  (clojure.pprint/write coll))


(defn toJSON [coll]
  "Call to clojure.data.json library to generate JSON from Clojure data structure"
  (json/write-str coll))


(defn from-json [coll]
  (json/read-str coll))


(defn great-circle-distance
  "Call with 5-th arg 3958.761 for miles and 3440.069 for nautical miles"
  ([lat1 long1 lat2 long2] (great-circle-distance lat1 long1 lat2 long2 6371.009))
  ([lat1 long1 lat2 long2 radius]
    (let [ [lat1-r long1-r lat2-r long2-r] (map #(Math/toRadians %) [lat1 long1 lat2 long2])]
      (* radius
         (Math/acos (+ (* (Math/sin lat1-r) (Math/sin lat2-r))
                       (* (Math/cos lat1-r) (Math/cos lat2-r) (Math/cos (- long1-r long2-r)))))))))


(defn merge-maps [& maps]
  "Merge map values by keys: {:key [val1 val2 ...]}"
  (reduce (fn [m1 m2]
            (reduce (fn [m [k v]]
                      (update-in m [k] (fnil conj []) v))
                    m1, m2))
          {}
          maps))


(defn write-file [data filename]
  (with-open [w (clojure.java.io/writer  filename )]
    (.write w (str data))))


(defn update-values 
  "Applies f To each value of the map "
  [m f & args]
  (reduce 
    (fn [r [k v]] 
      (assoc r k (apply f v args)))
    {} 
    m
    ))






