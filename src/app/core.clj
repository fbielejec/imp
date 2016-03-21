(ns app.core
  ( :import java.io.FileReader)
  ( :import jebl.evolution.io.NexusImporter)
  (:require [app.utils :as u])
  (:require [app.time :as t])
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;---GLOABL VARIABLES POLLUTING THE NAMESPACE :)---;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def filename "/home/filip/Dropbox/ClojureProjects/imp/resources/WNV_small.trees")

; (def filename "/home/filip/Dropbox/ClojureProjects/imp/resources/WNV_relaxed_geo_gamma.trees")

(def coordinateName "location")

(def outputFilename "/home/filip/Dropbox/JavaScriptProjects/imp-renderer/public/data.json" )

(def nBurninTrees 1 )

(def nSlices 10)

(def mrsd 2005.6)

(def importer
  (->>   filename
    (new FileReader )
    (new NexusImporter )
    );END: thread last
  );END: treeImporter

;;;;;;;;;;;;;;;;;;;;;
;;---HERE WE GO!---;;
;;;;;;;;;;;;;;;;;;;;;


(defn getRootCoords [tree]
  "Returns root coordinate attribute values"
  (let  [rootNode (. tree getRootNode ) ]
    
    ( . rootNode getAttribute coordinateName )
    
    );END:let
  );END: getRootCoords


(defn getDistanceToRoot
  "Returns great-cricle-distance distance between two coordinates"
  [nodeCoord rootCoord]
  (let [lat1 ( get  nodeCoord 1 ) long1 ( get  nodeCoord 0 ) lat2 ( get  rootCoord 1 ) long2 ( get  rootCoord 0 )]
    
    (u/great-circle-distance  lat1 long1 lat2 long2 )
    
    );END:let
  );END:getDistanceToRoot


; :node001 {:startX 0.1 :startY 0.3 :endX 0.5 :endY 0.4 :length 0.71 }
(defn analyzeTree
  "Represents tree as a map of branches (node-parent pairs):
   :node001 {:startX 0.1 :startY 0.3 :endX 0.5 :endY 0.4 :length 0.71 }
   key is a node which represents the branch, value is a map of attributes we need later"
  [tree]
  
  (into {}
        
        (let [nodes (into #{}  (. tree getNodes )) rootCoord (getRootCoords tree) ]
          
          (map  (fn [node]
                  (if  ( not (. tree isRoot node))
                    (let [parentNode (. tree getParent node)]
                      (let [nodeCoord ( . node getAttribute coordinateName ) parentCoord ( . parentNode getAttribute coordinateName ) ]
                        
                        (hash-map node {
                                        :startX ( get  parentCoord 0 ) ; parent long
                                        :startY ( get  parentCoord 1 ) ;parent lat
                                        :endX  ( get  nodeCoord 0 ); long
                                        :endY  ( get  nodeCoord 1 ) ; lat
                                        :nodeHeight (. tree getHeight node)
                                        :parentHeight (. tree getHeight parentNode)
                                        :distanceToRoot (getDistanceToRoot nodeCoord rootCoord)
                                        })
                        
                        );END: let
                      );END: let
                    );END: if
                  );END: fn
                nodes
                );END: map
          
          );END:let
        );END: into
  );END: analyzeTree


(defn getMinStartTime [branchesMap]
  (apply min (map :nodeHeight (vals branchesMap)  ) )
  );END: getMinStartTime


(defn getMaxStartTime [branchesMap]
  (apply max (map :nodeHeight (vals branchesMap)  ) )
  );END: getMaxStartTime


(defn createSliceHeights [branchesMap]
  "Returns a sequence of length nSlices"
  (let [ minim (getMinStartTime branchesMap) maxim (getMaxStartTime branchesMap) by (/ ( - maxim minim)  nSlices ) ]
    
    (range minim maxim by)
    
    );END:let
  );createSliceHeights


(defn filterBySlice
  "Return collection of branches which are intersected by sliceHeight"
  [branchesMap sliceHeight]
  (filter (fn [branch]
            
            (<=  (:nodeHeight ( val branch) )  sliceHeight (:parentHeight ( val branch) ) )
            
            );END:fn
          branchesMap
          );END: filter
  );END:filterBySlice


;furthest. is that a word?
(defn getFurthestFromRoot
  "From a collection of branches return the one which is furthest from root"
  [branchesSubset]
  (let [ maxDist (apply max (map :distanceToRoot (vals branchesSubset)  ) )]
    (filter (fn [branch]
              
              (==  (:distanceToRoot ( val branch) ) maxDist)
              
              );END:fn
            branchesSubset
            );END: filter
    );END:let
  );END: getFurthestFromRoot


; TODO
(defn getDistances
  "For every slice calculate spatial stats"
  [branchesMap sliceHeights]
  (reduce
    (fn [slicesMap sliceHeight ]
      ; get the branches  intersected by this slice
      (let [ branchesSubset ( filterBySlice branchesMap sliceHeight) ]
        (if (>(count branchesSubset) 0)
          
          ;  get the furthest one from root
          (let [ furthestBranch (getFurthestFromRoot branchesSubset) ]
            (let [dist (map :distanceToRoot ( vals furthestBranch) ) length (map :parentHeight ( vals furthestBranch) )  ]
              
              (assoc slicesMap sliceHeight (nth dist 0)
                     );END: assoc
              
              );END:let
            );END:let
          
          (assoc slicesMap sliceHeight  0.0  
                 );END: assoc
          
          );END:if
        );let
      );END:fn
    { } ;initial
    sliceHeights ;coll
    );END:reduce
  );END: getDistances



(defn extractTrees
  "Make a collection of tree maps"
  [treeImporter]
  (reduce
    (fn [treeMaps currentTree]
      
      (conj treeMaps
            ( analyzeTree currentTree )
            ); END:conj
      
      );END:fn
    [] ;initial
    (drop nBurninTrees  (lazy-seq (. treeImporter importTrees ) ) );coll
    );END:reduce
  );END: extractTrees


(defn getMaxParentHeight
  "Go over the collection of tree maps and return max height"
  [treeMaps]
  ;  (let [mapsVector (extractTrees treeImporter) ]
  (apply max    
         (map (fn[head & tail]
                (apply max (map :parentHeight (vals head)  ) )
                )
              treeMaps
              );END: apply
         );END:apply  
  ;    );END:let
  );END: getMaxParentHeight


(defn getMinNodeHeight
  "Go over the collection of tree maps and return min height"
  [treeMaps]
  ;  (let [mapsVector (extractTrees treeImporter) ]
  (apply min    
         (map (fn[head & tail]
                (apply min (map :nodeHeight (vals head)  ) )
                )
              treeMaps
              );END: apply
         );END:apply  
  ;    );END:let
  );END: getMinNodeHeight


(defn createSliceHeights
  "Create a uniform sequence of length nSlices with slice heights"
  [nSlices treeMaps]
  (let [ minim (getMinNodeHeight treeMaps) maxim (getMaxParentHeight treeMaps) interval (/ maxim nSlices ) ]
    
    ;(range (+ minim by) maxim by)
    
    (loop [i 0 timeSlices []]
      (if (< i nSlices)
        (recur (inc i) (conj timeSlices (- maxim (* interval i) ) ) )
        timeSlices
        );END: if
      );END: loop
    
    );END:let
  );END:createSliceHeights

; TODO pmap
(defn treesLoop
  "Go over the collection of tree maps calculating spatial stats
  @return: vector of maps with the same keys"
  [treeImporter]
  (let [treeMaps (extractTrees treeImporter)]
    (let [sliceHeights (createSliceHeights nSlices treeMaps)]
      
      (reduce
        (fn [mapsVector branchesMap]
          
          (conj mapsVector 
                ( getDistances branchesMap sliceHeights )  
                )    
          
          );END:fn
        []; initial
        treeMaps; coll
        );END:reduce
      
      );END: let
    );END: let
  );END: treesLoop


;  https://clojuredocs.org/clojure.set/rename-keys
;(rename-keys {:a 1, :b 2} {:a :new-a, :b :new-b})
;{:new-a 1, :new-b 2}
;TODO
(defn dateize-keys
  "transforms map keys to date strings"
  [m]
  (let [endDate (t/parseSimpleDate mrsd)]
    (letfn [(getDate [k] (t/getSliceDate k endDate) ) ]
      (reduce
        (fn[km k]
          
          (assoc km (getDate k) (get m k) )    
          
          );fn
        { };initial
        (keys m) ;coll
        );END: letfb
      );END:let
    );END:dateize-keys
  
  
  (defn getNiceJSON 
    [mapsVector]
    
    (->> mapsVector
      ( apply u/merge-maps)
      (dateize-keys )
      (into (sorted-map) )
      (u/toJSON)
      (str )
      );END: feelin thready
    
    );END:getSortedJSON
  
  
  (defn -main
    "Entry point"
    [& args]
    (do
      
      (time
        
        ;      (println
        
        (u/writeFile 
          (getNiceJSON (treesLoop importer) )
          outputFilename
          )
        
        ;      )
        
        );END:time
      
      ( println "Done!" )
      
      ( System/exit 0 )
      
      );END;do
    );END: main
  