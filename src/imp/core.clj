;;
;;---@fbielejec
;;

(ns imp.core
  (:use ring.adapter.jetty)
  (:require [imp.routes.handler :as handler])
  (:require [environ.core :refer [env]] )
   (:gen-class)
  )


;(defn -main [& [port]]
;  (let [port (Integer. (or port (env :port) 5000))]
;
;    (jetty/run-jetty (site #'app) {:port port :join? false})
;    
;    )
;  )

(defn -main
  "Entry point"
  [& [port]]
  
   (let [port (Integer. (or port (env :port) 8080))]
  
  (run-jetty #'handler/app {:port port})
  
  )
  
  )
