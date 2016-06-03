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


(defn parse-mean-data
  "Parse, analyze and return mean distances with 95% CI to a formatted JSON, ready for plotting in frontend"
  []
  (let [trees-dist-map  (d/get-trees-dist-map) ]
    (let [distances-map
          (->>
            (apply u/merge-maps-by-keys trees-dist-map)
            (t/dateize-keys (s/get-setting :mrsd) )
            (into (sorted-map)))]
      (format-data (get-mean-values-map distances-map)))))

