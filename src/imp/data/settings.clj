;;
;;---@fbielejec
;;

(ns imp.data.settings
  )

(def settings 
  (atom 
    {
     :coordinateName nil
     :burnin nil
     :nslices nil
     :mrsd nil
     }))

(defn get-settings []
  @settings)

(defn get-setting [id]
  (@settings (keyword id)))

(defn put-setting 
  "if key exists in atomic map update its value"
  [id value]
  (if (contains? @settings id)
    (swap! settings assoc (keyword id) value)
    value
    ))

