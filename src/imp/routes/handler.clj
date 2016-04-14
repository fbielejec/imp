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
  ;  (:require [imp.utils.utils :as u])
  )


(def error-codes
  {:invalid 400
   :not-found 404})


(defn json-response [data & [status]]
  {
   :status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)
   }
  )


(defroutes app-routes
  (GET  "/" [] (response/resource-response "index.html" {:root "public"}))
  
  (route/resources "/")
  
  (route/not-found "Page not found"))


(defroutes settings-routes
  (GET "/settings" []
       (json-response (s/get-settings)))
  
  (GET "/settings/:id" [id]
       (json-response (s/get-setting id)))
  
  (PUT "/settings" [id value]
       (json-response (s/put-setting id value))))


(defroutes trees-routes
  
  ;; route to upload .trees file to
  (PUT "/upload" [input]
       (json-response 
         (t/handle-upload input)))
  
  ;  (GET "/trees" []
  ;       (json-response (t/list-trees)))
  
  
  )


(def app
  (-> (routes settings-routes trees-routes app-routes)
    ;    wrap-params
    ;    wrap-exception-handler
    wrap-json-params))

