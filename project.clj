(defproject imp-rest "0.0.1"
  :description "REST interface for imp"
  
  :repositories {"local" ~(str (.toURI (java.io.File. "maven_repository")))}  
  
  :dependencies
  [
   [org.clojure/clojure "1.7.0"]
   [compojure "1.4.0"]
   [ring/ring-defaults "0.2.0"]
   [ring/ring-core "1.4.0"]
   [ring/ring-json "0.4.0"]
   [ring/ring-jetty-adapter "1.4.0"]
   [ring-json-params "0.1.3"]
;   [ring/ring-mock "0.3.0"]
   [clj-json "0.5.3"]
   [org.clojure/data.json "0.2.6"]
   [simple-time "0.2.0"]
   [local/jebl "0.4"]
   [environ "1.0.0"]
   ]
  
;  :dev-dependencies
;  [
;   [lein-run "1.0.0-SNAPSHOT"]
;   [ring/ring-mock "0.3.0"]
;   ]
  
  :plugins [
            [no-man-is-an-island/lein-eclipse "2.0.0"]
            [lein-kibit "0.1.2"]
            ]
  
    :profiles {
               :uberjar {:aot :all}
               :test {:dependencies [
                                     [ring/ring-mock "0.3.0"]
                                     ]
                      }
               
               }
  
  :main imp.core 
;  :aot [imp.core]
  )



