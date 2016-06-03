;;
;;---@fbielejec
;;

(ns imp.analysis.mean-distance-parser
  ;  (:import java.io.FileReader)
  ;  (:import jebl.evolution.io.NexusImporter)
  ;  (:require [imp.data.trees :as trees])
  (:require [imp.data.settings :as s])
  (:require [imp.utils.utils :as u])
  (:require [imp.utils.time :as t])
  
  (:require [imp.analysis.distance-map-parser :as d])
  )


(defn get-mean-values-map
  "Take the map of merged distances return map with mean and 95% CI"
  [merged-map]
  (map
    (fn [elem]
      (let [mean-distance (u/mean (val elem) )  std-distance (u/stdev (val elem) mean-distance) interval (* 1.96 std-distance)]
        {
         :time (key elem )
         :mean_distance mean-distance
         :lower_distance (- mean-distance interval)
         :upper_distance (+ mean-distance interval)
         }))
    merged-map))


(defn format-data
  "return JSON-friendly format"
  [mean-values-map]
  {:values (vec mean-values-map)})


;; TODO: repeated, move to utils
(defn dateize-keys
  "transforms map keys to date strings"
  [m]
  (let [end-date (t/parse-simple-date (s/get-setting :mrsd))]
    (letfn [(get-date [k] (t/get-slice-date k end-date))]
      (reduce
        (fn[km k]
          (assoc km (get-date k) (get m k))) ;fn
        {} ;initial
        (keys m)))))


(defn parse-mean-data
  "Parse, analyze and return mean distances with 95% CI to a formatted JSON, ready for plotting in frontend"
  []
  (let [trees-dist-map  (d/get-trees-dist-map) ]
    (let [distances-map
          (->>
            (apply u/merge-maps-by-keys trees-dist-map)
            (dateize-keys )
            (into (sorted-map)))]
      (format-data (get-mean-values-map distances-map)))))

