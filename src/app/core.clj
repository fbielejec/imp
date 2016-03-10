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
;                                   ;                                   :startTime (. tree getHeight node) 
;                                   ;                                   :endTime (. tree getHeight parentNode)
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


(defn analyzeTree [tree]
  
  (into {}
        
        (let [nodes (into #{}  (. tree getNodes ))  ]
          
          (map  (fn [node]
                  (if  ( not (. tree isRoot node))
                    (let [parentNode (. tree getParent node)]
                      (let [nodeCoord ( . node getAttribute coordinateName ) parentCoord ( . parentNode getAttribute coordinateName ) ]
                        
                        (hash-map node {
                                        :startX ( get  parentCoord 0 ) ; parent long
                                        :startY ( get  parentCoord 1 ) ;parent lat
                                        :endX  ( get  nodeCoord 0 ); long
                                        :endY  ( get  nodeCoord 1 ) ; lat
                                        :startTime (. tree getHeight node) 
                                        :endTime (. tree getHeight parentNode)
                                        :length  (- (. tree getHeight parentNode)  (. tree getHeight node) )
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
  (apply min (map :startTime (vals branchesMap)  ) )
  );END: getMinStartTime


(defn getMaxStartTime [branchesMap]
  (apply max (map :startTime (vals branchesMap)  ) )
  );END: getMaxStartTime


(defn createSliceHeights [branchesMap]
  (let [ minim (getMinStartTime branchesMap) maxim (getMaxStartTime branchesMap) by (/ ( - maxim minim)  nSlices ) ]
    
    (range minim maxim by) 
    
    );END:let
  );createSliceHeights


(defn getRootCoords [tree]
  
  (let  [rootNode (. tree getRootNode ) ] 
    (let  [rootCoords ( . rootNode getAttribute coordinateName )]
      
      {
       :startX ( get  rootCoords 0 ) ;  long
       :startY ( get  rootCoords 1 ) ; lat
       }
      
      );END:let
    );END:let
  );END: getRootCoords

;   (<= (:startTime (vals branchesMap)) sliceHeight (:endTime (vals branchesMap) ) )
; :node001 {:startX 0.1 :startY 0.3 :endX 0.5 :endY 0.4 :length 0.71 }

(defn filterBySlice [branchesMap sliceHeight]

  (filter (fn [branch]

;  (println 
    
; (map :endTime branch)   "\n"
    
   (<=  (:startTime ( val branch) )  sliceHeight (:endTime ( val branch) ) ) ;;"\n"

;    )

  
;  false
  
            );END:fn
          branchesMap
          );END: filter
  
  );END:filterBySlice 



; :node001 {:startTime 0.1 :endTime 0.3   }

; :node002 {:startTime 0.4 :endTime 0.5 }

(defn getDistances [branchesMap tree]
  
  (let [sliceHeights (createSliceHeights branchesMap) rootCoord (getRootCoords tree) ]
    
    

      
      ( let [slice (nth sliceHeights 25 ) ]

     ( filterBySlice branchesMap slice)
        
        )


    
    
    
;   (reduce
;     (fn [slicesMap sliceHeight ]
;       
;        TODO : which branches are intersected by this slice
;        TODO get the furthest one from root
;
;(println
;  
;   (map :startTime (vals branchesMap) ) 
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
