;;
;;---@fbielejec
;;

(ns imp.core
  (:use ring.adapter.jetty)
  (:require [imp.routes.handler :as handler])
  (:require [environ.core :refer [env]] )
  (:gen-class)
  )


(defn -main
  "Entry point"
  [& [port]]
  (let [port (Integer. (or port (env :port) 8080))]
    (run-jetty #'handler/app {:port port})))
