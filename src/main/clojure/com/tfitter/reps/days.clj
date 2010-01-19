(ns tfitter 
  ;; (:use [...])
  (:require [clojure.contrib.str-utils2 :as s2])
  (:import [org.joda.time DateTime]
           [java.util Date]
           [java.text SimpleDateFormat]))

;; (import '[org.joda.time DateTime])

;; Joda DateTime
(defn joda-at [s]
  (DateTime. (str (s2/replace s " " "T") "Z")))
  
(def dateFmt (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss Z"))

;; java.util.Date
(defn date-at [s]
  (.parse dateFmt (str s " +0000")))


(def +day-zero+ (.getDayOfYear (DateTime. "2009-10-16")))
;; TODO doesn't work across year boundaries:
(defn day-number 
  "day number relative to the +day-zero+"
  [dt]  ; dt is juDate
  (- (.getDayOfYear (DateTime. dt)) +day-zero+))
