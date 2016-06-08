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
  (GET  "/" [] (response/resource-response "index.html" {:root "public"}))
  
  (route/resources "/")
  
  (route/not-found "Page not found"))


(defroutes trees-routes
  ;; route to upload .trees files
  (PUT "/trees" [input]
       (json-response 
         (t/handle-upload input))))


(defroutes attributes-routes
  ;; route on which server serves parsed location attribute values
  (GET "/attributes" []
       (json-response 
         (a/parse-attributes)
         )))

(defroutes ntrees-routes
  ;; route on which server serves the number of trees
  (GET "/ntrees" []
       (json-response 
         (n/get-ntrees)
         )))

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
       (json-response (pa/get-data-all)))
  
  (GET "/data/mean" []
       (json-response (pm/get-data-mean))))


(def app
  (-> (routes trees-routes attributes-routes ntrees-routes settings-routes results-routes app-routes)
    wrap-json-params))

