(defn sper-sorted [sper user]
  "fetch sper and sort by the number of replies"
  (when-let [ours (sper (keyword user))]
    (->> ours (map (fn [[user reps]] [user (count reps)])) (sort-by second >))
    ))
    
(defn sper-back [reps sper user] 
  (let [user-reps (reps user)
    tops (sper-sorted sper user)
    total (count tops)
    half (/ total 2) 
    front (->> tops 
      (map (fn [i [user num]]
      (let [back (user-reps (keyword user))] [i user (count back)])) (iterate inc 0))
      (remove (fn [[i user num]] (zero? num)))
      (map first)
      (filter #(< % half))
      count)]
    (/ front total)))
      