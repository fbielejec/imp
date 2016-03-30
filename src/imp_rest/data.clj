;;
;;---@fbielejec
;;

(ns imp-rest.data
  (:require [imp-rest.parser :as p])
  )


(def data (atom {} ) )

(defn put-data
  ""
  []
  (reset! data (p/parse-data))
  );END:put-data

(defn get-data
  ""
  []
;  (put-data)
;  @data

(p/parse-data)

  );END:get-data


