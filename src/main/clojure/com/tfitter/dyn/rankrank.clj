;; for the pairs-day map of pagerank, replace the rank
;; by the position of the rank in its decreasing order

;;  this in fact slows things down
(defn ranked-days-nested-transients
  "replace rank for each day by its position in order, desc"
  [dpr]
    (->> dpr (reduce (fn [res [user days]] 
      (reduce (fn [res [day num]] 
        (assoc! res day (conj! (or (res day) (transient [])) [user num]))) 
      res days)) (transient {})) 
      (persistent!)  (map (fn [[k v]] 
      (let [
        with-nums-desc (sort-by second > (persistent! v))
        ranked (map (fn [[user _] rank] [user rank]) 
           with-nums-desc (iterate inc 0))
        ]
      [k ranked])))
    ;; (apply concat) (apply hash-map)
    (into {})
    ))
    
(defn ranked-days
  "replace rank for each day by its position in order, desc"
  [dpr & [quant]]
  (let [quant (or quant 1000000)
    [unsorted _] (->> dpr (reduce (fn [theres [user days]] 
      (reduce (fn [[res progress] [day num]]
        (when (and quant (= (mod progress quant) 0)) 
			       (.print System/err (str " " (quot progress quant))))
        (let [res (update-in res [day] #(conj (or % []) [user num]))]
          [res (inc progress)])) 
      theres days)) [{} 0]))]
      (.print System/err " days: ") 
      (->> unsorted (map (fn [[k v]] 
        (let [
          with-nums-desc (sort-by second > v)
          ranked (map (fn [[user _] rank] [user rank]) 
             with-nums-desc (iterate inc 0))
          ]
        (.print System/err (str " " k))
        [k ranked])))
    ;; (apply concat) (apply hash-map)
    (into {})
    )))

;; http://gist.github.com/283450    
(defn assoc-in-with
  "supply a default-map"
  [m default-map [k & ks] v]
  (if ks
    (assoc m k (assoc-in-with (get m k default-map) default-map ks v))
    (assoc m k v)))
    
    
(defn ranked-graph-simple
  "takes the result of ranked-days and produces a graph like dpagerank, except
  that the pagerank per day is replaced by the rank's rank that day"
  [by-day]        
  (->> by-day (reduce (fn [res [day pairs]]
      (reduce (fn [res [user rank]]
        (assoc-in-with res (sorted-map) [user day] rank))
        res pairs)) {})))
  
(defn ranked-graph
  "takes the result of ranked-days and produces a graph like dpagerank, except
  that the pagerank per day is replaced by the rank's rank that day"
  [by-day & [quant]]
  (let [quant (or quant 1000000)]        
  (->> by-day (reduce (fn [theres [day pairs]]
      (reduce (fn [[res progress] [user rank]]
        (let [rx (res user)
          yz (assoc (or rx (sorted-map)) day rank)
          ]
        (when (and quant (= (mod progress quant) 0)) 
			       (.print System/err (str " " (quot progress quant))))        
        (let [res (assoc! res user yz)]
          [res (inc progress)])))
        theres pairs)) [(transient {}) 0])
        first
        persistent!
        )))
