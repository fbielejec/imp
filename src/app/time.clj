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
           :years  year
           :months month
           :days day  ; (if (not= day 0) day 1  )
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
    (let [year (:years dateFields) month  (:months dateFields) day (:days dateFields) ]
      
      (t/datetime year (if (not= month 0) month 1  ) (if (not= day 0) day 1  ))
      
      );END:let
    );END:let
  );END:parseDate


(defn getSliceDate
  "Put a date on a slice height"
  [sliceHeight simpleDate]
  (let [dateFields  (getDateFields sliceHeight) ]
    
    (t/format
      (-> simpleDate  
        (t/add-years ( unchecked-negate (:years dateFields))) 
        (t/add-months( unchecked-negate (:months dateFields))) 
        (t/add-days ( unchecked-negate (:days dateFields)))   
        )
      "YYYY/MM/dd"
      );END:format
    
    );END:let
  );END: getDate


