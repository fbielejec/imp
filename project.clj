(defproject newick_parser "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "GNU LGPL"
            :url "http://www.gnu.org/licenses/lgpl-3.0.en.html"}
  
  :repositories {"local" ~(str (.toURI (java.io.File. "maven_repository")))}          
            
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [jebl "0.4"]
                 [org.clojure/data.json "0.2.6"]
  ]
  
  :main app.core
  
  )
