;;
;;---@fbielejec
;;

(ns imp-rest.core
  (:use ring.adapter.jetty)
  (:require [imp-rest.handler :as handler])
  )

(defn -main
  "Entry point"
  [& args]
  (run-jetty #'handler/app {:port 8080}))
