;;
;;---@fbielejec
;;

(ns imp-rest.settings
  
  )

(def settings 
  (atom 
    {
     :filename nil
     :coordinateName nil
     :burnin nil
     :nslices nil
     :mrsd nil
     }
    )
  )

(defn get-settings []
  @settings
  );END:list

(defn get-setting [id]
  (@settings (keyword id))
  );END:get-setting

(defn put-setting [id value]
  (swap! settings assoc (keyword id) value)
  value
  );END: put-setting

