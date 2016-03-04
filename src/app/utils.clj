(ns app.utils
(:require [clojure.pprint :refer (pprint)]  )
  )


(defn printHashMap [hashMap]
  "Pretty-prints a hashmap"
  
  (clojure.pprint/write hashMap) ; :stream nil)
  
  );END: printHashMap