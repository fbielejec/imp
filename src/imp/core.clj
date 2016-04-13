;;
;;---@fbielejec
;;

(ns imp.core
  (:use ring.adapter.jetty)
  (:require [imp.routes.handler :as handler])
  )

(defn -main
  "Entry point"
  [& args]
  (run-jetty #'handler/app {:port 8080}))
