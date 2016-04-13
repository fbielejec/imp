;;
;;---@fbielejec
;;

(ns imp.data.settings
  )

(def settings 
  (atom 
    {
;     :trees nil
     :coordinateName nil
     :burnin nil
     :nslices nil
     :mrsd nil
     }))

(defn get-settings []
  @settings)

(defn get-setting [id]
  (@settings (keyword id)))

;; TODO if id is among map keys
(defn put-setting [id value]
  (swap! settings assoc (keyword id) value)
  value)

