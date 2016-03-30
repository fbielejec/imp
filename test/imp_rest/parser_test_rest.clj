;;
;;---@fbielejec
;;

(ns imp-rest.parser-test-rest
  (:require [clojure.test :refer :all])
  (:require  [ring.mock.request :as mock] )
    (:require [imp-rest.web :as w])

    ;  (:require [imp-rest.settings :as s])
;  (:require [imp-rest.parser :as p])
  
)

(deftest test-parser-rest
  
  (testing "put settings"

      (let [response (w/app (mock/request :get "/settings"))]
      
        (println 
          response
          )
        
      );END:let
           
           
               (is (= 1 1) )   
           
    );END: put settings test
  
  
  );END:rest test

;(deftest test-app
;  (testing "main route"
;    (let [response (app (mock/request :get "/"))]
;      (is (= (:status response) 200))
;      (is (= (:body response) "Hello World")))
;)
;
;  (testing "not-found route"
;    (let [response (app (mock/request :get "/invalid"))]
;      (is (= (:status response) 404)))))