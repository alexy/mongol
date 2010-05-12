(defn balance [dreps dments & [progress]]
  (let [progress (or progress 10000)]
  "for every user, get his froms and tos, and compute daily balances for them -- who said more than he heard back"
  
  (->> dreps (reduce (fn [[res i] [user1 days]] 
    (when (and progress (zero? (mod i progress))) (err "."))
    [(reduce (fn [res [day reps]]
    (let [neg-ments (->> dments user1 day  (map (fn [[k v]] [k (- v)])) (into {}))
      balday (merge-with + reps neg-ments)]
      (reduce (fn [res [user2 more]] 
        (let [prev (get-in res [user1 (dec day) user2])]
        (assoc-in res [user1 day user2] (+ (or prev 0) more))
        )) res balday)))
        res days) (inc i)]) [{} 0]))))   
