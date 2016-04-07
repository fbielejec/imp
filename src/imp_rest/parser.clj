;;
;;---@fbielejec
;;

(ns imp-rest.parser
  (:import java.io.FileReader)
  (:import jebl.evolution.io.NexusImporter)
  (:require [imp-rest.settings :as s])
  (:require [imp-rest.utils :as u])
  (:require [imp-rest.time :as t])
  )


;;;;;;;;;;;;;;;;;;;;;;;
;;---PARSE TO JSON---;;
;;;;;;;;;;;;;;;;;;;;;;;


(defn get-root-coords [tree settings]
  "Returns root coordinate attribute values"
  (let [root-node (.getRootNode tree)]
    (.getAttribute root-node (:coordinateName settings))))


(defn get-distance-to-root
  "Returns great-cricle-distance distance between two coordinates"
  [node-coord root-coord]
  (let [lat1 ( get  node-coord 1 ) long1 ( get  node-coord 0 ) lat2 ( get  root-coord 1 ) long2 ( get root-coord 0 )]
    (u/great-circle-distance  lat1 long1 lat2 long2 )))


(defn analyze-tree
  "Represents tree as a map of branches (node-parent pairs):
   :node001 {:startX 0.1 :startY 0.3 :endX 0.5 :endY 0.4 :length 0.71 }
   key is a node which represents the branch, value is a map of attributes we need later"
  [tree settings]
  (into {}
        (let [nodes (set  (.getNodes tree)) root-coord (get-root-coords tree settings) ]
          (map  (fn [node]
                  (if (not (.isRoot tree node))
                    (let [parent-node (.getParent tree node)]
                      (let [node-coord (.getAttribute node (:coordinateName settings)) parent-coord (.getAttribute parent-node (:coordinateName settings))]
                        ;; :node001 {:startX 0.1 :startY 0.3 :endX 0.5 :endY 0.4 :length 0.71 }                        
                        (hash-map node {
                                        :startX ( get parent-coord 0 ) ; parent long
                                        :startY ( get parent-coord 1 ) ;parent lat
                                        :endX ( get node-coord 0 ); long
                                        :endY ( get node-coord 1 ) ; lat
                                        :nodeHeight (. tree getHeight node)
                                        :parentHeight (. tree getHeight parent-node)
                                        :distanceToRoot (get-distance-to-root node-coord root-coord)
                                        })))))
                nodes))))


(defn get-min-start-time [branches-map]
  (apply min (map :nodeHeight (vals branches-map))))


(defn get-max-start-time [branches-map]
  (apply max (map :nodeHeight (vals branches-map))))


(defn create-slice-heights [branches-map settings]
  "Returns a sequence of length nslices"
  (let [ minim (get-min-start-time branches-map) maxim (get-max-start-time branches-map) by (/ ( - maxim minim)  (:nslices settings) ) ]
    (range minim maxim by)))


(defn filter-by-slice
  "Return collection of branches which are intersected by sliceHeight"
  [branches-map slice-height]
  (filter (fn [branch]
            (<=  (:nodeHeight (val branch)) slice-height (:parentHeight (val branch))))
          branches-map))


;furthest. is that a word?
(defn get-furthest-from-root
  "From a collection of branches return the one which is furthest from root"
  [branches-subset]
  (let [max-dist (apply max (map :distanceToRoot (vals branches-subset)))]
    (filter (fn [branch]
              (==  (:distanceToRoot (val branch)) max-dist))
            branches-subset)))


(defn get-distances
  "For every slice calculate spatial stats"
  [branches-map slice-heights]
  (reduce
    (fn [slices-map slice-height ]
      ;; get the branches intersected by this slice
      (let [ branches-subset (filter-by-slice branches-map slice-height)]
        (if (pos? (count branches-subset))
          ;  get the furthest one from root
          (let [ furthest-branch (get-furthest-from-root branches-subset)]
            (let [dist (map :distanceToRoot (vals furthest-branch)) length (map :parentHeight ( vals furthest-branch))]
              (assoc slices-map slice-height (nth dist 0))))
          (assoc slices-map slice-height 0.0)))) ;; END:fn
    {} ;; initial
    slice-heights))


(defn create-tree-importer
  "Create JEBL tree importer"
  [filename]
;  (let [filename (:filename settings)]
    (->> filename
      (new FileReader )
      (new NexusImporter))
;    )
  )


(defn extract-trees
  "Make a collection of tree maps"
  [settings]
  (let [tree-importer (create-tree-importer (:filename settings))]
    (reduce
      (fn [tree-maps current-tree]
        (conj tree-maps
              (analyze-tree current-tree settings)))
      [] ;initial
      (drop (:burnin settings) (lazy-seq (.importTrees tree-importer))))))


(defn get-max-parent-height
  "Go over the collection of tree maps and return max height"
  [tree-maps]
  (apply max    
         (map (fn[head & tail]
                (apply max (map :parentHeight (vals head))))
              tree-maps)))


(defn get-min-node-height
  "Go over the collection of tree maps and return min height"
  [tree-maps]
  (apply min    
         (map (fn [head & tail]
                (apply min (map :nodeHeight (vals head))))
              tree-maps)))


(defn create-slice-heights
  "Create a uniform sequence of length nslices with slice heights"
  [ tree-maps settings]
  (let [minim (get-min-node-height tree-maps) maxim (get-max-parent-height tree-maps) interval (/ maxim (:nslices settings))]
    (loop [i 0 time-slices []]
      (if (< i (:nslices settings))
        (recur (inc i) (conj time-slices (- maxim (* interval i))))
        time-slices
        ))))


(defn trees-loop
  "Go over the collection of tree maps calculating spatial stats
  @return: vector of maps with the same keys"
  [settings]
  (let [tree-maps (extract-trees settings) slice-heights (create-slice-heights tree-maps settings)]
    (vec
    (pmap
      (fn [tree-map] (get-distances tree-map slice-heights))
      tree-maps))))


(defn dateize-keys
  "transforms map keys to date strings"
  [m]
  (let [end-date (t/parse-simple-date (s/get-setting :mrsd))]
    (letfn [(get-date [k] (t/get-slice-date k end-date))]
      (reduce
        (fn[km k]
          (assoc km (get-date k) (get m k)));fn
        {};initial
        (keys m)))))


(defn pair-with-key
  "Carry the key over to every value of map"
  [maps]
  ;  (let [ ntrees (->  maps (first) (val) (count))]    
  (map (fn [ m]
         ( let [k (key m) values (val m) ]
           (map
             (fn [v]
               (hash-map :time k :distance v))
             values)))
       maps)
  ;  ) 
  )


(defn interleave-n
  "Interleave n trees (first with first, second with second, etc)"
  [coll]
  (let [ ntrees (count (nth coll 0 ))]
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
      coll
      )))

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
    (apply u/merge-maps)
    (dateize-keys )
    (into (sorted-map))
    (frontend-friendly-format)))


(defn parse-data
  "Parse, analyze and return formatted JSON, ready for plotting in frontend"
  []
  (let [settings (s/get-settings)]
    (format-data 
      (trees-loop settings))))



