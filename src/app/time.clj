(ns app.time
  (:require [simple-time.core :as t]  )
  )
  
;https://github.com/mbossenbroek/simple-time

(defn getDateFields
  "Convert decimal date to :year :month :day"
  [decimalDate]
  (let [year (int decimalDate)]
    (let [decimalMonth  (- decimalDate year ) ]
      (let[month  (int (* 12   decimalMonth) ) ]
        (let [day  (-> decimalMonth (* 12 )  (- month )  (* 30 ) (int) ) ]
          {
           :year  year
           :month month
           :day day  ; (if (not= day 0) day 1  )
           }
          )
        )
      )
    )
  );END: convertToYearMonthDay


(defn parseSimpleDate
  "Return simple date from decimal date"
  [decimalDate]
  (let [dateFields (getDateFields decimalDate) ]
    (let [year (:year dateFields) month  (:month dateFields) day (:day dateFields) ]
      
      (t/datetime year (if (not= month 0) month 1  ) (if (not= day 0) day 1  ))
      
      );END:let
    );END:let
  );END:parseDate


(defn getSliceDate
  "Put a date on a slice height"
  [sliceHeight simpleDate]
[]
  (let [dateFields  (getDateFields sliceHeight) ]
      
;(t/add-years (t/datetime 2005 1 1) -1)
    

    
    );END:let
  );END: getDate


