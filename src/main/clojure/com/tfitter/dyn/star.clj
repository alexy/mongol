(defn mean
  "just a regular joe's mean"
  [s]
  (let [sum (reduce + s)
    len (count s)] 
  (if (> len 0) (double (/ sum len)) 0.0)))
  
(defn star-rank
  "compute my daily rank to my daily mentioners' average daily rank" 
  [drank dments]
  ;; TODO pmap?
  (->> drank (map (fn [[user dayranks]]
    (let [
      davrseq (map (fn [[day rank]]
        (let [
          ments    (get-in dments [user day])
          dranks   (->> ments keys (map name) (map #(get-in drank [% day])))
          weights  (vals ments)
          sumranks (->> (map (fn [k v] (* k v)) dranks weights) (reduce + ))
          summents (reduce + weights)   
          avrank (if (> summents 0) (double (/ sumranks summents)) 0.0)]
        [day [rank avrank]])) dayranks)
        ;; (->> davrseq (apply concat) (apply sorted-map))
      davranks (into (sorted-map) davrseq)
      ]
    [user davranks])))
    ;; (apply concat) (apply hash-map)
    ;; ctdean:
    ;; (reduce #(apply assoc %1 %2) {})
    ;; alexyk:
    ;; (reduce (fn [r [k v]] (assoc! r k v)) (transient {}))
    ;; (reduce #(apply assoc! %1 %2) (transient {}))
    ;; persistent!
    (into {})
    ))
