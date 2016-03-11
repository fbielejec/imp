(ns app.core
  ( :import java.io.FileReader)
  ( :import jebl.evolution.io.NexusImporter)
  (:require [app.utils :as utils])
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;---GLOABL VARIABLES POLLUTING THE NAMESPACE :)---;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def filename "/home/filip/Dropbox/ClojureProjects/imp/resources/WNV_small.trees")

;(def xCoordinateName "location2")
;(def yCoordinateName "location1")

(def coordinateName "location")

(def nSlices 1000)

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

;(defn analyzeTree [tree]
;  (let [nodes (into #{}  (. tree getNodes ))  ]
;    
;    (reduce 
;      (fn [map node]
;        
;        (if  ( not (. tree isRoot node))
;          
;          (let [parentNode (. tree getParent node)]
;            
;            (assoc map parentNode {
;                                   ;                                   :startX ( . parentNode getAttribute xCoordinateName ) ; parent long
;                                   ;                                   :startY ( . parentNode getAttribute yCoordinateName ) ;parent lat
;                                   ;                                   :endX  ( . node getAttribute xCoordinateName ); long
;                                   ;                                   :endY  ( . node getAttribute yCoordinateName ) ; lat
;                                   ;                                   :startHeight (. tree getHeight node) 
;                                   ;                                   :endHeight (. tree getHeight parentNode)
;                                   :length  (- (. tree getHeight parentNode)  (. tree getHeight node) ) ;
;                                   } )
;            
;            );END:let
;          
;          );END:if
;        
;        ); f-tion
;      {} ; initial
;      nodes; collection
;      );END:reduce 
;    
;    );END:let
;  );END: analyzeTree


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
                                        :startHeight (. tree getHeight node) 
                                        :endHeight (. tree getHeight parentNode)
                                        :length  (- (. tree getHeight parentNode)  (. tree getHeight node) )
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
  (apply min (map :startHeight (vals branchesMap)  ) )
  );END: getMinStartTime


(defn getMaxStartTime [branchesMap]
;  TODO: maybe :endHeight ?
  (apply max (map :startHeight (vals branchesMap)  ) )
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
            
            (<=  (:startHeight ( val branch) )  sliceHeight (:endHeight ( val branch) ) ) 
            
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
  "TODO"
  [branchesMap tree]
  
  (let [sliceHeights (createSliceHeights branchesMap)  ]
    
    

      
    ( let [slice (nth sliceHeights 25 ) ]
      (let [ branchesSubset ( filterBySlice branchesMap slice) ]
        
        (do 
          
        (println branchesSubset)
        
        (println "---------------")
        
        (println (getFurthestFromRoot branchesSubset) )
        
        );END:do
        
        );END:let
      );END:let
    

    
    
    
;   (reduce
;     (fn [slicesMap sliceHeight ]
;       
;        TODO : which branches are intersected by this slice
;        TODO get the furthest one from root
;
;(println
;  
;   (map :startHeight (vals branchesMap) ) 
;  
; )
;				if (nodeHeight < sliceHeight
;								&& sliceHeight <= parentHeight)
;
;       (assoc slicesMap sliceHeight {
;                                     :distance 0.0
;                                     }
; 
;         );END: assoc
;       
;       );END:fn
;      { } ;initial
;      sliceHeights ;coll
;     );END:reduce

    
    
    );END:let
  
  );END: getDistances



(defn treesLoop []
  ;  (while (. treeImporter hasTree)
  (let [currentTree (. treeImporter importNextTree ) ]
    
    
    ;    (let [nodes (into #{}  (. currentTree getNodes ))  ] 
    ;      
    ;       ( -> nodes   count println  )
    ;      
    ;      )
    
    
    (let [branchesMap (analyzeTree currentTree) ]
      (do
        
        (println
          
          ( getDistances branchesMap currentTree)
          ;        ( -> branchesMap   count println  )
          
          )
        
        );END: do
      );END:let
    
    
    );END:let
  ;     );END: while
  );END: treesLoop



(defn -main
  "Entry point"
  [& args]
  (do
    
    (time
      ( treesLoop )
      )
    
    ( println "Done!" )
    
    ( System/exit 0 )
    
    );END;do
  );END: main
