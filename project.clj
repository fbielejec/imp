(defproject imp-rest "0.0.1"
  :description "REST interface for imp"
  
;  :repositories {"local" ~(str (.toURI (java.io.File. "maven_repository")))}  
;:repositories {"local" "file:maven_repository"}  

:repositories [["local" "file:maven_repository"]
               ["jebl2" {:url "http://www.stat.ubc.ca/~bouchard/maven/jebl/jebl/2.0/"
                         :snapshots false
                         :sign-releases false
                         :checksum :fail
                         :update :never
                         }]
               ]

  :dependencies
  [
   [org.clojure/clojure "1.7.0"]
   [compojure "1.4.0"]
   [ring/ring-defaults "0.2.0"]
   [ring/ring-core "1.4.0"]
   [ring/ring-json "0.4.0"]
   [ring/ring-jetty-adapter "1.4.0"]
   [ring-json-params "0.1.3"]
   [clj-json "0.5.3"]
   [org.clojure/data.json "0.2.6"]
   [simple-time "0.2.0"]
;   [local/jebl "0.4"]
   [jebl "2.0"]
   [environ "1.0.0"]
   ]
  :min-lein-version "2.6.0"
  :plugins [
            [no-man-is-an-island/lein-eclipse "2.0.0"]
            [lein-kibit "0.1.2"]
            ]
;  :hooks [environ.leiningen.hooks]
  :profiles {
             :uberjar {:aot :all}
             :test {:dependencies [
                                   [ring/ring-mock "0.3.0"]
                                   ]}
             :production {:env {:production true}}
             }
  :main imp.core 
  )



