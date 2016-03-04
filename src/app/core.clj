(ns app.core
  ( :import java.io.FileReader)
  ( :import jebl.evolution.io.NexusImporter)
   (:require [app.utils :as utils])
  )


;;;;;;;;;;;;;;;;;;;;;

;(let [tia ( agent treeImporter )]
;  
;  (defn hasNextTree [ ]
;    (letfn [(hasNext [out ] (.hasTree out ) out)]
;      (send tia hasNext )
;      );END: letfn
;    );END: hasNextTree
;  
;  (defn getNextTree []
;    (letfn [(getNext [out ] (.importNextTree out ) out)]
;      (send tia getNext )
;      );END: letfn
;    );END: getNextTree
;  
;  
;  (defn silly []
;    
;    (doall
;      (map deref
;           (doall
;             
;             (while (. @tia hasTree)
;               
;               (let [currentTree (. @tia importNextTree ) ]
;                 (future
;                   (analyzeTree currentTree)
;                   );END: future
;                 );END:let
;               
;               );END: while
;             
;             );END :doall
;           );END: map deref
;      );END: doall
;    
;    );END:silly
;  
;  );END :let
;
;(defn treesLoop []
;            ( silly ) 
;  )

;;;;;;;;;;;;;;;;;;;;;

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

;(defn analyzeTree [tree]
;  
;  (let [nodes (into #{}  (. tree getNodes )) nodesMap {} ]
;    
;    (map  (fn [node]
;            (if  ( not (. tree isRoot node))
;              (let [parentNode (. tree getParent node)]
;                (do
;                        
;                    (- (. tree getHeight parentNode)  (. tree getHeight node) )
;                    
;                  );END: do
;                );END: let
;              );END: if
;            );END: fn
;          nodes
;          );END: map
;    
;    );END:let
;  
;  );END: analyzeTree





(defn analyzeTree [tree]
  
  (let [nodes (into #{}  (. tree getNodes ))  ]
    
    (reduce 
      (fn [map node]
        
        (if  ( not (. tree isRoot node))
          
          (let [parentNode (. tree getParent node)]
            
;             ( . node getAttribute "location1" )
           
            
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
               
               (utils/printHashMap 
                 (analyzeTree currentTree)
                 )
               
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
    
;      ( utils/printHashMap {:startX 0.1 :startY 0.3 :endX 0.5 :endY 0.4 :time 0.71 })

    (time
      ( treesLoop )
      )
    
    ( println "Done!" )
    
    ( System/exit 0 )
    
    );END;do
  );END: main
