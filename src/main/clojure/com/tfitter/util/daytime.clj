(ns mongol.repliers
(:import 
  [java.util Date Calendar]
  [org.joda.time DateTime]))
  
(def *cal* (Calendar/getInstance))

;; very slow: http://paste.pocoo.org/show/169141/
(defn day-number-ju-judate 
  "get a day number of juDate, for now as the day of year, TODO: Julian day"
  [judate]
  (.get (doto *cal* (.setTime judate)) Calendar/DAY_OF_YEAR))
 
(defn day-number-of-judate 
  "get a day number of juDate, for now as the day of year, TODO: Julian day"
  [judate]
  (.getDayOfYear (DateTime. judate)))
  
;; (def *zero-day* (day-number-of-judate (Date. ??))
(def *zero-day* (.getDayOfYear (DateTime. "2009-10-16")))

(defn daynum-of-judate 
  "get the day number away from the *zero-day*"
  [judate]
  (- (day-number-of-judate judate) *zero-day*))