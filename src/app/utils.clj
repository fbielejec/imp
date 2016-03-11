(ns app.utils
  (:require [clojure.pprint :refer (pprint)]  )
  (:require [clojure.data.json :as json] )
  )


(defn printHashMap [hashMap]
  "Pretty-prints a hashmap"
  
  (clojure.pprint/write hashMap) ; :stream nil)
  
  );END: printHashMap


(defn toJSON [hashMap]
  "Call to clojure.data.json library to generate JSON from Clojure data structure"
  (json/write-str hashMap)
  );END: saveJSON


(defn great-circle-distance
  "Call with 5-th arg 3958.761 for miles and 3440.069 for nautical miles"
  ([lat1 long1 lat2 long2] 
    
    (great-circle-distance lat1 long1 lat2 long2 6371.009)
    
    );END:arity
  
  ([lat1 long1 lat2 long2 radius]
    
    (let [ [lat1-r long1-r lat2-r long2-r] (map #(Math/toRadians %) [lat1 long1 lat2 long2]) ]
      (* radius
         (Math/acos (+ (* (Math/sin lat1-r) (Math/sin lat2-r))
                       (* (Math/cos lat1-r) (Math/cos lat2-r) (Math/cos (- long1-r long2-r)) )
                       
                       );END:+
                    );END:acos
         );END:*
      );END:let
    );END:arity 
  );END:great-circle-distance


