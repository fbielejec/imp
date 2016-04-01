;;
;;---@fbielejec
;;

(ns imp-rest.web
  (:use compojure.core )
  (:use ring.middleware.json-params )
  (:use ring.middleware.params )
  (:require [clj-json.core :as json] )
  (:require [ring.util.response :as response] )
  (:require [compojure.route :as route] )
  (:require [imp-rest.settings :as s] )
  (:require [imp-rest.data :as d] )
  )


(defn json-response [data & [status]]
  {
   :status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)
   }
  );END:json-response


;(defn- str-to [num]
;  (apply str (interpose ", " (range 1 (inc num)))))


(defroutes handler
  
  ;   (GET "/count-up/:to" [to] (str-to (Integer. to)))
  
  (GET "/settings" []
       (json-response (s/get-settings)))
  
  (GET "/settings/:id" [id]
       (json-response (s/get-setting id)))
  
  (PUT "/settings" [id value]
       (json-response (s/put-setting id value)))
  
  (GET "/data" [] (d/get-data) )   
  
  (route/not-found "Page not found")
  
  );END:defroutes


(def app
  (-> handler
;    wrap-params
    wrap-json-params)
  )

