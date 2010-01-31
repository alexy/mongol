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
        (let [ments (get-in dments [user day])
          avrank (->> ments keys  ; TODO weigh by vals?
            (map name) 
            (map #(get-in drank [% day])) mean)]
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
