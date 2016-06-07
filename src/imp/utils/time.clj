;;
;;---@fbielejec
;;

(ns imp.utils.time
  (:require [simple-time.core :as t] )
  )


(defn get-date-fields
  "Convert decimal date to :year :month :day"
  [decimal-date]
  (let [year (int decimal-date)]
    (let [decimal-month (- decimal-date year)]
      (let[month (int (* 12   decimal-month))]
        (let [day (-> decimal-month (* 12 ) (- month) (* 30) (int))]
          {
           :years  year
           :months month
           :days day  ; (if (not= day 0) day 1  )
           }
          )))))


(defn parse-simple-date
  "Return simple date from decimal date"
  [decimal-date]
  (let [date-fields (get-date-fields decimal-date)]
    (let [year (:years date-fields) month (:months date-fields) day (:days date-fields)]
      (t/datetime year (if (not= month 0) month 1) (if (not= day 0) day 1)))))


(defn get-slice-date
  "Put a date on a slice height"
  [slice-height simple-date]
  (let [date-fields  (get-date-fields slice-height)]
    (t/format
      (-> simple-date  
        (t/add-years ( unchecked-negate (:years date-fields))) 
        (t/add-months( unchecked-negate (:months date-fields))) 
        (t/add-days ( unchecked-negate (:days date-fields))))
      "YYYY/MM/dd")))


(defn dateize-keys
  "transforms map keys to date strings given a starting date (mrsd)"
  [mrsd m]
  (let [end-date (parse-simple-date mrsd)]
    (letfn [(get-date [k] (get-slice-date k end-date))]
      (reduce
        (fn[km k]
          (assoc km (get-date k) (get m k))) ;fn
        {} ;initial
        (keys m)))))
