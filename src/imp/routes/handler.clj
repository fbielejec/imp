;;
;;---@fbielejec
;;

(ns imp.routes.handler
  (:use compojure.core )
  (:use ring.middleware.json-params )
  (:use ring.middleware.params )
  (:require [clj-json.core :as json] )
  (:require [ring.util.response :as response] )
  (:require [compojure.route :as route] )
  (:require [imp.data.settings :as s] )
  (:require [imp.data.trees :as t] )
  (:require [imp.data.attributes :as a])
  (:require [imp.data.ntrees :as n])
  (:require [imp.analysis.mean-distance-parser :as pm])
  (:require [imp.analysis.all-distances-parser :as pa])
  (:require [imp.analysis.distance-map-parser :as d])
  )


(def error-codes
  {:invalid 400
   :not-found 404})


(defn json-response [data & [status]]
  {
   :status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)
   })


(defroutes app-routes
  (GET "/" [] (response/resource-response "index.html" {:root "public"}))
  (route/resources "/")
  (route/not-found "Page not found"))


(defn clear-atoms
  "clear all singleton atoms from analysis package"
  []
  (do
    (d/clear-trees-dist-map)
    (pm/clear-data-mean)
    (pa/clear-data-all)))


(defroutes trees-routes
  ;; route to upload .trees files
  ;; TODO: validate content? (does first line contain #NEXUS)
  (PUT "/trees" [input]
       (try
         (do
           (clear-atoms)
           (json-response (t/handle-upload input))
           )
         (catch Exception e
           (println "Caught an error: Cannot upload trees")
           (json-response {"Error" "Cannot upload trees file"} 404)
           ))))


(defroutes attributes-routes
  ;; route on which server serves parsed location attribute values
  (GET "/attributes" []
       (try
         (json-response 
           (a/parse-attributes))
         (catch Exception e
           (println "Caught an error: Cannot read attributes")
           (json-response {"Error" "Cannot read location attributes from the uploaded trees file"} 404)
           
           ))))


(defroutes ntrees-routes
  ;; route on which server serves the number of trees
  (GET "/ntrees" []
       (try
         (json-response 
           (n/get-ntrees))
         (catch Exception e
           
           (println "Caught an error: Cannot read number of trees")           
           (json-response {"Error" "Cannot read from the uploaded trees file"} 404)))))


(defroutes settings-routes
  ;; routes to set the settings
  (GET "/settings" []
       (json-response (s/get-settings)))
  
  (GET "/settings/:id" [id]
       (json-response (s/get-setting id)))
  
  (PUT "/settings" [id value]
       (json-response (s/put-setting id value))))


(defroutes results-routes
  ;; routes with results
  (GET "/data/all" []
       (try
         (json-response (pa/get-data-all))
         (catch Exception e
           (println "Caught an error: Cannot parse distances distribution data")
           (json-response {"Error" "Cannot parse distances distribution data from these settings"} 404)
           )))
  
  
  (GET "/data/mean" []
       (try
         (json-response (pm/get-data-mean))
         (catch Exception e
           (println "Caught an error: Cannot parse mean distance data")
           (json-response {"Error" "Cannot parse mean distance data from these settings"} 404)))))


(def app
  (-> (routes trees-routes attributes-routes ntrees-routes settings-routes results-routes app-routes)
    wrap-json-params 
    ))

