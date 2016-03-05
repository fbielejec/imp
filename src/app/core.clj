(ns app.core
  ( :import java.io.FileReader)
  ( :import jebl.evolution.io.NexusImporter)
  (:require [app.utils :as utils])
  )

;baseline (Orbit): 23398 msec

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;---GLOABL VARIABLES POLLUTING THE NAMESPACE :)---;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def filename "/home/filip/Dropbox/ClojureProjects/newick_parser/resources/WNV.tre")

(def xCoordinateName "location2")

(def yCoordinateName "location1")


;;;;;;;;;;;;;;;;;;;;;
;;---HERE WE GO!---;;
;;;;;;;;;;;;;;;;;;;;;

(def treeImporter
  (->>   filename
    (new FileReader )
    (new NexusImporter )
    );END: thread last
  );END: treeImporter


; :node001 {:startX 0.1 :startY 0.3 :endX 0.5 :endY 0.4 :time 0.71 }

(defn analyzeTree [tree]
  (let [nodes (into #{}  (. tree getNodes ))  ]
    
    (reduce 
      (fn [map node]
        
        (if  ( not (. tree isRoot node))
          
          (let [parentNode (. tree getParent node)]
            
            (assoc map parentNode {
                                   :startX ( . parentNode getAttribute xCoordinateName ) ; parent long
                                   :startY ( . parentNode getAttribute yCoordinateName ) ;parent lat
                                   :endX  ( . node getAttribute xCoordinateName ); long
                                   :endY  ( . node getAttribute yCoordinateName ) ; lat
                                   :time  (- (. tree getHeight parentNode)  (. tree getHeight node) ) ;
                                   } )
            
            );END:let
          
          );END:if
        
        ); f-tion
      {} ; initial
      nodes; collection
      );END:reduce 
    
    );END:let
  );END: analyzeTree



(defn treesLoop []
  ;  
  ;  (doall
  ;    (map deref
  ;         (doall
  
  (while (. treeImporter hasTree)
    
    (let [currentTree (. treeImporter importNextTree ) ]
      
;      (utils/printHashMap 
(println (utils/toJSON
                 
           (analyzeTree currentTree)
                 
                 ) )

      );END:let
    
    );END: while
  
  ;           );END :doall
  ;         );END: map deref
  ;    );END: doall
  
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
