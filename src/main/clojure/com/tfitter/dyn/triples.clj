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
	[coll-name user-name reps-name & quant]
	(let [
		quant (or quant 100000)
		get-reps (->> coll-name (fetch) (map #(dissoc % :_id :_ns))) ;; (take 10)
		[res _] 
		(reduce (fn [[res progress] {from user-name reps reps-name}]
			(when (= (mod progress quant) 0) 
				(.print System/err (str " " (quot progress quant))))
			(let [res (reduce (fn [res [to moments]]
				(reduce (fn [res moment]
					(let [daynum (daynum-of-judate moment)]
						;; TODO we really need update-in-with here for sorted daynum
						(update-in res [from daynum to] #(inc (or % 0)))))
					res moments))
				res reps)]
				[res (inc progress)]))
			[{} 0] get-reps)
		]
		res))
	