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

(defn getRootCoords [tree settings]
  "Returns root coordinate attribute values"
  (let  [rootNode (. tree getRootNode ) ]
    
    ( . rootNode getAttribute (:coordinateName settings) )
    
    );END:let
  );END: getRootCoords


(defn getDistanceToRoot
  "Returns great-cricle-distance distance between two coordinates"
  [nodeCoord rootCoord]
  (let [lat1 ( get  nodeCoord 1 ) long1 ( get  nodeCoord 0 ) lat2 ( get  rootCoord 1 ) long2 ( get rootCoord 0 )]
    
    (u/great-circle-distance  lat1 long1 lat2 long2 )
    
    );END:let
  );END:getDistanceToRoot


(defn analyzeTree
  "Represents tree as a map of branches (node-parent pairs):
   :node001 {:startX 0.1 :startY 0.3 :endX 0.5 :endY 0.4 :length 0.71 }
   key is a node which represents the branch, value is a map of attributes we need later"
  [tree settings]
  
  (into {}
        
        (let [nodes (into #{}  (. tree getNodes )) rootCoord (getRootCoords tree settings) ]
          
          (map  (fn [node]
                  (if  ( not (. tree isRoot node))
                    (let [parentNode (. tree getParent node)]
                      (let [nodeCoord ( . node getAttribute (:coordinateName settings) ) parentCoord ( . parentNode getAttribute (:coordinateName settings) ) ]
                        
                        ; :node001 {:startX 0.1 :startY 0.3 :endX 0.5 :endY 0.4 :length 0.71 }                        
                        (hash-map node {
                                        :startX ( get parentCoord 0 ) ; parent long
                                        :startY ( get parentCoord 1 ) ;parent lat
                                        :endX ( get nodeCoord 0 ); long
                                        :endY ( get nodeCoord 1 ) ; lat
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
  (apply min (map :nodeHeight (vals branchesMap) ) )
  );END: getMinStartTime


(defn getMaxStartTime [branchesMap]
  (apply max (map :nodeHeight (vals branchesMap) ) )
  );END: getMaxStartTime


(defn createSliceHeights [branchesMap settings]
  "Returns a sequence of length nSlices"
  (let [ minim (getMinStartTime branchesMap) maxim (getMaxStartTime branchesMap) by (/ ( - maxim minim)  (:nslices settings) ) ]
    
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


(defn getDistances
  "For every slice calculate spatial stats"
  [branchesMap sliceHeights]
  (reduce
    (fn [slicesMap sliceHeight ]
      ; get the branches  intersected by this slice
      (let [ branchesSubset ( filterBySlice branchesMap sliceHeight) ]
        (if ( > (count branchesSubset) 0)
          
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


(defn createTreeImporter
  ""
  [settings]
  (let [ filename (:filename settings)]
    
    (->> filename
      (new FileReader )
      (new NexusImporter )
      );END: thread last
    
    );END:let
  );END:createTreeImporter


(defn extractTrees
  "Make a collection of tree maps"
  [ settings]
  (let [treeImporter (createTreeImporter settings) ]
    (reduce
      (fn [treeMaps currentTree]
        
        (conj treeMaps
              ( analyzeTree currentTree settings)
              ); END:conj
        
        );END:fn
      [] ;initial
      (drop (:burnin settings)  (lazy-seq (. treeImporter importTrees ) ) );coll
      );END:reduce
    );END:let
  );END: extractTrees


(defn getMaxParentHeight
  "Go over the collection of tree maps and return max height"
  [treeMaps]
  (apply max    
         (map (fn[head & tail]
                (apply max (map :parentHeight (vals head)  ) )
                )
              treeMaps
              );END: apply
         );END:apply  
  );END: getMaxParentHeight


(defn getMinNodeHeight
  "Go over the collection of tree maps and return min height"
  [treeMaps]
  (apply min    
         (map (fn[head & tail]
                (apply min (map :nodeHeight (vals head)  ) )
                )
              treeMaps
              );END: apply
         );END:apply  
  );END: getMinNodeHeight


(defn createSliceHeights
  "Create a uniform sequence of length nslices with slice heights"
  [ treeMaps settings]
  (let [ minim (getMinNodeHeight treeMaps) maxim (getMaxParentHeight treeMaps) interval (/ maxim (:nslices settings) ) ]
    
    (loop [i 0 timeSlices []]
      (if (< i (:nslices settings))
        (recur (inc i) (conj timeSlices (- maxim (* interval i) ) ) )
        timeSlices
        );END: if
      );END: loop
    
    );END:let
  );END:createSliceHeights


(defn treesLoop
  "Go over the collection of tree maps calculating spatial stats
  @return: vector of maps with the same keys"
  [ settings]
  (let [treeMaps (extractTrees settings) sliceHeights (createSliceHeights treeMaps settings)]
    
    (into []
          (pmap (fn[treeMap] 
                  ( getDistances treeMap sliceHeights 
                                 ))
                treeMaps))
    );END: let
  );END: treesLoop


(defn dateize-keys
  "transforms map keys to date strings"
  [m ]
  (let [endDate (t/parseSimpleDate (s/get-setting :mrsd) )]
    (letfn [(getDate [k] (t/getSliceDate k endDate) ) ]
      (reduce
        (fn[km k]
          
          (assoc km (getDate k) (get m k) )    
          
          );fn
        { };initial
        (keys m) ;coll
        );END:reduce
      );END: letfn
    );END:let
  );END:dateize-keys


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


; TODO recur to get i
(defn name-value
  ""
  [coll]
  (map
    (fn [elem]
      
      {
       :name (str "tree_" )
       :value elem
       }
      
      )
    coll
    )
  )


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
  [mapsVector]
  
  (->> mapsVector
    (apply u/merge-maps)
    (dateize-keys )
    (into (sorted-map))
    (frontend-friendly-format)
    );END: feelin thready
  
  );END: format-data


(defn parse-data
  "Parse, analyze and return formatted JSON, ready for plotting in frontend"
  [ ]
  (let [settings (s/get-settings)]
    
    ( format-data 
      (treesLoop settings) 
      )
    
    
    );END:let
  );END:parse







