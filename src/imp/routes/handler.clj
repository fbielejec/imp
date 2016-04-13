;;
;;---@fbielejec
;;

(ns imp.routes.handler
  (:use compojure.core )
  (:use ring.middleware.json-params )
  (:use ring.middleware.params )
;  (:import org.codehaus.jackson.JsonParseException)
;(:import clojure.contrib.condition.Condition)
  (:require [clj-json.core :as json] )
  (:require [ring.util.response :as response] )
  (:require [compojure.route :as route] )
  (:require [imp.data.settings :as s] )
;  (:require [imp.data.attributes :as a] )
  (:require [imp.analysis.parser :as p])
  (:require [imp.utils.utils :as u])
  )


;(defn init []
;  (println "imp is starting"))
;
;
;(defn destroy []
;  (println "imp is shutting down"))


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

; TODO: error handling (in handler :) )
(defroutes app-routes
  
  (GET  "/" [] (response/resource-response "index.html" {:root "public"}))
  
  (route/resources "/")
  
  (GET "/settings" []
       (json-response (s/get-settings)))
  
  (GET "/settings/:id" [id]
       (json-response (s/get-setting id)))
  
  (PUT "/settings" [id value]
       (json-response (s/put-setting id value)))
  
  
;    (GET "/attributes" []
;       (json-response (a/parse-attributes)))
  
  (GET "/data" [] 
       (json-response  
         (p/parse-data)))   
  
  (route/not-found "Page not found")
  
  )


;(defn wrap-exception-handling [handler]
;  (fn [req]
;    (try
;      (or (handler req)
;          (json-response {"error" "resource not found"} 404))
;      (catch JsonParseException e
;        (json-response {"error" "malformed json"} 400))
;      (catch Condition e
;        (let [{:keys [type message]} (meta e)]
;          (json-response {"error" message} (error-codes type)))))))


(def app
  (-> app-routes
    ;    wrap-params
;    wrap-exception-handler
    wrap-json-params))

