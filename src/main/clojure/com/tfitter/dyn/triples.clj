(ns mongol.repliers
  (:use somnium.congomongo))
  
(mongo! :db "twitter")

(defn get-pair-days
	"read the results of dynpr"
	[coll key-name val-name & [quant day-name]]
	(let [
		quant    (or quant 1000000)
		day-name (or day-name :day)
		redfun (fn [[res progress] {key key-name val val-name day day-name}]
			; (str (" " (quot progress quant))) => "."
			(when (= (mod progress quant) 0) (.print System/err (str (" " (quot progress quant)))))
			(let [res (assoc-in res [key day] val)]
				[res (inc progress)]))
		_ (.print System/err (str "loading pair-days from " coll))
		[res _] (reduce redfun [{} 0] (fetch coll))]
		res))
		
		
	