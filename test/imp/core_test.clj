(ns imp.core-test
  (:require [clojure.test :refer :all]
            [app.core :refer :all]
            [app.utils :refer :all]
             [app.time :refer :all]
            ))

(def map1
{0.0 1, 0.1 3 }

(def map2
{0.0 2, 0.1 4 }
  )

(def data (vector map1 map2 ) )

(def merged-map {0.0 [1 2] 0.1 [2 3]  })

(deftest test-merge
  (testing "merge-maps"
    (is (= merged-map (apply merge-maps data)))))
