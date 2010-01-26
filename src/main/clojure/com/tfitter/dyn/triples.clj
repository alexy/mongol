(ns mongol.repliers
  (:use somnium.congomongo)
  (:import [org.joda.time DateTime]))
  
(mongo! :db "twitter")

;; http://gist.github.com/283450    
(defn assoc-in-with
  "supply a default-map"
  [m default-map [k & ks] v]
  (if ks
    (assoc m k (assoc-in-with (get m k default-map) default-map ks v))
    (assoc m k v)))

(defn graph-pair-days
	"read the results of dynpr"
	[s & [quant]]
	(let [
		quant    (or quant 1000000)
		redfun (fn [[res progress] [key day val]]
			; (str (" " (quot progress quant))) => "."
			(when (= (mod progress quant) 0) (.print System/err (str " " (quot progress quant))))
			(let [res (assoc-in-with res (sorted-map) [key day] val)]
				[res (inc progress)]))
		[res _] (reduce redfun [{} 0] s)]
		res))
		
(defn get-pair-days
	"read the results of dynpr"
	[coll key-name val-name & [quant day-name]]
	(let [
		_ (.print System/err (str "loading pair-days from " coll))
		day-name (or day-name :day)
		coll-arr (fetch-fast coll :dismongo :keys [key-name day-name val-name] :progress quant)
		]
		(graph-pair-days coll-arr quant)))
		
; from daytime.clj
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
  
(defn get-day-reps
	"load the replier graph rolled by day"
	[coll-name user-name reps-name & [quant]]
	(let [quant (or quant 1000000)
	  ]
		(->> (fetch coll-name :dismongo? true) 
    ;; (take 10) -- for debugging
		(reduce (fn [theres {from user-name reps reps-name}]
			(reduce (fn [theres [to moments]]
				(reduce (fn [[res progress] moment]
					(let [daynum (daynum-of-judate moment)
					  rx (or (res from) (sorted-map))
					  z  (or (rx daynum) 0)
					  yz (assoc rx daynum (inc z))
					  ]
						;; TODO we really need update-in-with! here for sorted daynum
      			(when (= (mod progress quant) 0) 
      				(.print System/err (str " " (quot progress quant))))
						(let [res (assoc! res from yz)]
						[res (inc progress)])))
					theres moments))
				theres reps))
			[(transient {}) 0])
    first
    persistent!)))
	