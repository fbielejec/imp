(ns app.core
  ( :import java.io.FileReader)
  ( :import jebl.evolution.io.NexusImporter)
  (:require [app.utils :as utils])
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;---GLOABL VARIABLES POLLUTING THE NAMESPACE :)---;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def filename "/home/filip/Dropbox/ClojureProjects/imp/resources/WNV_small.trees")

; (def filename "/home/filip/Dropbox/ClojureProjects/imp/resources/WNV_relaxed_geo_gamma.trees")

(def coordinateName "location")

(def nSlices 10)

(def mrsd 2007.3)

;;;;;;;;;;;;;;;;;;;;;
;;---HERE WE GO!---;;
;;;;;;;;;;;;;;;;;;;;;

(def treeImporter
  (->>   filename
    (new FileReader )
    (new NexusImporter )
    );END: thread last
  );END: treeImporter


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
    
    (utils/great-circle-distance  lat1 long1 lat2 long2 )
    
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


(defn getDistances
  "For every slice calculate spatial stats"
  [branchesMap sliceHeights]
  (reduce
    (fn [slicesMap sliceHeight ]
      
      ;    get the branches  intersected by this slice
      (let [ branchesSubset ( filterBySlice branchesMap sliceHeight) ]
        
        ;  get the furthest one from root
        (let [ furthestBranch (getFurthestFromRoot branchesSubset) ]
          
          (let [dist (map :distanceToRoot ( vals furthestBranch) ) length (map :parentHeight ( vals furthestBranch) )  ]
            
            ;              (assoc slicesMap sliceHeight {
            ;                                            :wavefrontDistance (/ (nth dist 0) (nth length 0) )
            ;                                            } );END: assoc
            
            (assoc slicesMap sliceHeight   (/ (nth dist 0) (nth length 0) )  );END: assoc
            
            );END:let
          
          );END:let
        );END:let
      
      );END:fn
    { } ;initial
    sliceHeights ;coll
    );END:reduce
  );END: getDistances


; TODO: not sure how memory-efficient importing all is 
(defn treesLoop
  "Iterate over trees distribution calculating spatial stats"
  []
  (let [sliceHeights (atom nil)]
    (reduce
      (fn [mapsVector currentTree]
        
        (let [branchesMap (analyzeTree currentTree)  ]
          
          (if (not @sliceHeights)
            
            ;rebind sliceHeights  
            (do
              
              (reset! sliceHeights (createSliceHeights branchesMap)  )
              
              (conj mapsVector ( getDistances branchesMap @sliceHeights )   )           
              
              );END: do
            
            ; go about business
            (conj mapsVector ( getDistances branchesMap @sliceHeights )  )            
            
            );END:if
          
          );END:let
        
        );END:fn
      [];initial
      (lazy-seq (. treeImporter importTrees ) ) ;coll
      );END:reduce
    );END:let
  );END:treesLoop


(defn getSortedJSON 
  [mapsVector]
  
  (->> mapsVector
    ( apply utils/merge-maps)
    (utils/toJSON)
    );END: feelin thready
  
  );END:getSortedJSON


(defn -main
  "Entry point"
  [& args]
  (do
    
    (time
      
      (println
        (getSortedJSON ( treesLoop ) )
        )
      )
    
    ( println "Done!" )
    
    ( System/exit 0 )
    
    );END;do
  );END: main
