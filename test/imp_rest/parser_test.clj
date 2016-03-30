;;
;;---@fbielejec
;;

(ns imp-rest.parser-test
  (:require [clojure.test :refer :all])
  (:require [imp-rest.settings :as s])
  (:require [imp-rest.parser :as p])
  ;            [ring.mock.request :as mock]
  ;            [compojure-test.handler :refer :all]
  
  )

;(deftest test-app
;  (testing "main route"
;    (let [response (app (mock/request :get "/"))]
;      (is (= (:status response) 200))
;      (is (= (:body response) "Hello World"))))
;
;  (testing "not-found route"
;    (let [response (app (mock/request :get "/invalid"))]
;      (is (= (:status response) 404)))))

(deftest test-parser
  (testing "parser"
           
             (s/put-setting :filename "/home/filip/Dropbox/ClojureProjects/imp-rest/resources/WNV_small.trees")
             (s/put-setting :coordinateName "location")
             (s/put-setting :burnin 1)
             (s/put-setting :nslices 10)
             (s/put-setting :mrsd 2005.3)
             
;             (let [settings (s/get-settings)]
             
               (println
                  (p/parse-to-json )
                 )
             
             (is (= 1 1) )       
             
;             );END:let
             
           );END: testing
  );END: test-parser




