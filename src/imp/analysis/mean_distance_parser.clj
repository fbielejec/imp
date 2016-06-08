;;
;;---@fbielejec
;;

(ns imp.analysis.mean-distance-parser
  (:require [imp.data.settings :as s])
  (:require [imp.utils.utils :as u])
  (:require [imp.utils.time :as t])
  (:require [imp.analysis.distance-map-parser :as d])
  )

;; atom with mean data vector
(def data-mean (atom []))


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
  (vec mean-values-map))


(defn clear-data-mean
  "Clears the atom"
  []
  (reset! data-mean []))


(defn parse-data-mean
  "Parse, analyze and return mean distances with 95% CI to a formatted JSON, ready for plotting in frontend"
  []
  (let [trees-dist-map  (d/get-trees-dist-map) ]
    (let [distances-map
          (->>
            (apply u/merge-maps-by-keys trees-dist-map)
            (t/dateize-keys (s/get-setting :mrsd) )
            (into (sorted-map)))]
      (format-data (get-mean-values-map distances-map)))))


(defn get-data-mean
  "To avoid costly recomputing, fill and on subsequent calls get the data-mean atom (something like a singleton pattern)"
  []
  (if (empty? @data-mean)
    (reset! data-mean (parse-data-mean)) ;; true
    @data-mean ;; false
    ))

