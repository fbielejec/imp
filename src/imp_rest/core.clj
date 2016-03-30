;;
;;---@fbielejec
;;

(ns imp-rest.core
  (:use ring.adapter.jetty)
  (:require [imp-rest.web :as web])
  )

(defn -main
  "Entry point"
  [& args]
  (do
    
    (run-jetty #'web/app {:port 8080})
    
    );END;do
  );END: main
