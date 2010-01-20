(defn scan-days [[days counter] {:keys [created_at]} & quant]
  (let [quant (or quant 100000)
  	daynum ]
  (if (= (mod counter quant) 0) (.print System/err (format " %d" (quot counter quant)))))
  [(assoc! users (:screen_name twit) (inc (users (:screen_name twit) 0))) (inc counter)])

(time (def users (persistent! ((reduce scan-users [(transient {}) 0] (fetch :hose :only [:screen_name])) 0))))
(.println System/err)


(defn daynum-of-dt
  "day of twit from created_at's datetime"
  [dt]
  (.getDayOfYear (DateTime. dt)))

;; devlinsf  
(defn nondecreasing? [s]
	(every? #(apply < %) (partition 2 1 s)))

(defn nondecreasing-str? [s] (every? (fn [[x y]] (<= (.compareTo x y) 0)) (partition 2 1 s)))

;; hiredman
;; (let [x [1 2 3 4 0]] (take-while true? (map < x (rest x))))
;; TODO piles on true's instead of just one
(defn nondecreasing-str? [s]
	(take-while true? (map (fn [x y] (<= (.compareTo x y) 0)) s (rest s))))
	
;; chouser
;; (every? true? (apply map > ((juxt rest seq) [1 3 4 6 7])))

(time (nondecreasing-str? (map #(% :created_at) 
	(fetch :hose :where {:$orderby {:created_at 1}} :only [:created_at]))))