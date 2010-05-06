(require '[jiraph.tc :as tc])

;; TODO this must be in my utils
(defn err [ & args]
  (doto System/err (.print (apply str args)) .flush))

(defn tokyo-read-reps [db] 
  (let [tc (:db db) 
    r (when (.iterinit tc) 
      (loop [k (.iternext2 tc) res [] i 0] 
        (if (empty? k) res 
          (do (when (zero? (mod i 10000)) (print "."))  
            (recur (.iternext2 tc) (conj 
              res [k (:days (jiraph.tc/db-get db k))]) (inc i))))))] 
  (into {} r)))

;; (time (doseq [[user reps] dreps] (jiraph.tc/db-add db user (protobuf Repliers :user user :days reps))))
(defn tokyo-write-reps [graph db-pathname & [progress]]
  (let [db (tc/db-init {:path db-pathname :create true})
    progress (or progress 10000)]
    (doseq [[[user reps] i] (map vector graph (iterate inc 0))]
      (when (zero? (mod i progress)) (err "."))
      (tc/db-add db user (protobuf Repliers :user user :days reps)))
  ))                                                  
  
  
(defn string-reps [keyworded-reps & [do-day]]
  (->> keyworded-reps 
    (map (fn [[user days]] 
      [user (->> days (map (fn [[day reps]] 
        [(if do-day (->> day name Integer/parseInt) day)
         (->> reps (map (fn [[rep num]] 
        [(name rep) num])) 
        (into {}))])) 
      (into (sorted-map)))])) 
    (into {})))
  
