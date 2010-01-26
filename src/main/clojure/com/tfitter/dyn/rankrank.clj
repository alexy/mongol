;; for the pairs-day map of pagerank, replace the rank
;; by the position of the rank in its decreasing order

;;  this in fact slows things down
(defn ranked-days-transients
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
  [dpr]
    (->> dpr (reduce (fn [res [user days]] 
      (reduce (fn [res [day num]] 
        (update-in res [day] #(conj (or % []) [user num]))) 
      res days)) {}) 
      (map (fn [[k v]] 
        (let [
          with-nums-desc (sort-by second > v)
          ranked (map (fn [[user _] rank] [user rank]) 
             with-nums-desc (iterate inc 0))
          ]
        [k ranked])))
    ;; (apply concat) (apply hash-map)
    (into {})
    ))

;; http://gist.github.com/283450    
(defn assoc-in-with
  "supply a default-map"
  [m default-map [k & ks] v]
  (if ks
    (assoc m k (assoc-in-with (get m k default-map) default-map ks v))
    (assoc m k v)))
    
    
(defn ranked-graph
  "takes the result of ranked-graph"
  [by-day]        
  (->> by-day (reduce (fn [res [day pairs]]
      (reduce (fn [res [user rank]]
        (assoc-in-with res (sorted-map) [user day] rank))
        res pairs)) {})))
  
