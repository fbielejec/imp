(ns newick-parser.core
  ( :import java.io.FileReader)
  ( :import jebl.evolution.io.NexusImporter)
  )

; http://www.braveclojure.com/concurrency/

; http://www.thattommyhall.com/2014/02/24/concurrency-and-parallelism-in-clojure/

; https://dzone.com/articles/promises-and-futures-clojure

;http://stackoverflow.com/questions/18958972/why-are-only-32-threads-running-when-calling-futures-in-clojure

;baseline (Orbit): 23398 msec
(def filename "/home/filip/Dropbox/ClojureProjects/newick_parser/resources/WNV.trees")

(def treeImporter
  (->>   filename
    (new FileReader )
    (new NexusImporter )
    );END: thread last
  );END: treeImporter


(defn analyzeTree [tree]

  (let [nodes (into #{}  (. tree getNodes ))]

    (map  (fn [node]
            (if  ( not (. tree isRoot node))
              (let [parentNode (. tree getParent node)]
                (do

                 (println

                   (- (. tree getHeight parentNode)  (. tree getHeight node) )

                   );END: println

                  );END: do
                );END: let
              );END: if
            );END: fn
          nodes
          );END: map

    );END:let

  );END: analyzeTree


;(let [
;    top-sites `("www.google.com" "www.youtube.com" "www.yahoo.com" "www.msn.com")
;    futures-list (doall (
;            map #(
;                future (slurp (str "http://" %))
;            )
;            top-sites
;    ))
;    contents (map deref futures-list)
;    ]
;(doseq [s contents] (println s))
;)


;(defn collectFutures []
;  
;  (loop [currentTree (. treeImporter importNextTree ) futures-list '() ]
;    (if  (. treeImporter hasTree)
;      (recur
;        
;        (let [currentTree (. treeImporter importNextTree ) ]
;        
;        (conj futures-list (future (analyzeTree currentTree)) )
;        
;        )
;        );END:recur
;      
;      futures-list
;      
;      );END:if
;    );END:loop
;  );END: collectFutures
  



(defn treesLoop []
  
  (doall
    (map deref
         (doall
           
           (while (. treeImporter hasTree)
             
             (let [currentTree (. treeImporter importNextTree ) ]
               
               (future (analyzeTree currentTree))
               
               );END:let
             
             );END: while
           
           );END :doall
         );END: map deref
    );END: doall
  
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
