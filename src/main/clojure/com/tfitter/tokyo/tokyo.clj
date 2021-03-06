(require '[jiraph.tc :as tc])
(use 'protobuf)
(use '[clojure.contrib.seq-utils :only [partition-all]])
(require '(clj-json [core :as json]))

(defprotobuf Repliers Dreps Repliers)

;; TODO this must be in my utils
(defn err [ & args]
  (doto System/err (.print (apply str args)) .flush))
(defn errln [ & args]
  (doto System/err (.println (apply str args)) .flush))

(defn tokyo-read-reps [proto db-pathname & [str-load?]]
  (let [
    init-params (let [par {:path db-pathname :read-only true}]
      (if str-load? par 
        (merge par {:dump protobuf-dump :load (partial protobuf-load proto)})))
    db (tc/db-init init-params)
    _ (tc/db-open db)
    tc (:db db) 
    r (when (.iterinit tc) 
      (loop [k (.iternext2 tc) res [] i 0] 
        (if (empty? k) res 
          (do (when (zero? (mod i 10000)) (err "."))  
            (recur (.iternext2 tc) (conj 
              res [k (:days (jiraph.tc/db-get db k))]) (inc i))))))]
  (tc/db-close db)
  (into {} r)))

(defstruct id-chunk :id :chunk)

(defn do-chunk [{:keys [id _]} proto chunk & [progress]]
	(errln "agent " id " got chunk of length " (count chunk))
	(let [res (map (fn [[user days] i] 
		(when (and progress (zero? (mod i progress))) (err id))
		; TODO a general way to pass protobuf fields, as well as proto, as parameters?
		(let [protodays (protobuf proto :user user :days days)]
			[user protodays])) chunk (iterate inc 0))
		reslen (count res)]
		(errln "agent " id " produced result of length " reslen)
		(struct id-chunk id res)))

;; (time (doseq [[user reps] dreps] (jiraph.tc/db-add db user (protobuf Repliers :user user :days reps))))

(defn tokyo-agents-write-reps [graph proto db-pathname & [num-agents progress]]
  (let [db (tc/db-init {:path db-pathname :create true :dump protobuf-dump :load (partial protobuf-load proto)})
    progress (or progress 10000)
    ;; uprots (pmap (fn [[user reps]] [user (protobuf Repliers :user user :days reps)]) graph)
	;; _ (time (doall uprots))
	chunk-size (int (/ (+ (count graph) num-agents) num-agents))
    chunks (partition-all chunk-size graph)
	
    agents (map #(agent (struct id-chunk % [])) (range num-agents))
    agents (map (fn [agt chunk] (send agt do-chunk proto chunk progress)) agents chunks)]
    	(errln "agents started... ")
    	(time (apply await agents))
    	(errln "agents done!")
		(tc/db-open db)
        (let [uprots (reduce #(into %1 (:chunk @%2)) [] agents)]
        	(err "uprots has length " (count uprots))
			;(tc/db-transaction ... ) makes no difference:
			(doseq [[[user prot] i] (map vector uprots (iterate inc 0))]
			  (when (and progress (zero? (mod i progress))) (err "."))
			  (tc/db-add db user prot)))
		(tc/db-close db)))  
		
		
(defn tokyo-pmap-write-reps [graph proto db-pathname & [progress]]
  (let [db (tc/db-init {:path db-pathname :create true :dump protobuf-dump :load (partial protobuf-load proto)})
    progress (or progress 10000)
    uprots (pmap (fn [[user reps]] [user (protobuf Repliers :user user :days reps)]) graph)
	;_ (time (doall uprots))
	]
		(tc/db-open db)
		(doseq [[[user prot] i] (map vector uprots (iterate inc 0))]
		  (when (zero? (mod i progress)) (err "."))
		  (tc/db-add db user prot))
	(tc/db-close db)))
		
  
  
(defn string-reps [keyworded-reps & [do-day]]
  (->> keyworded-reps 
    (pmap (fn [[user days]] 
      [user (->> days (map (fn [[day reps]] 
        [(if do-day (->> day name Integer/parseInt) day)
         (->> reps (map (fn [[rep num]] 
        [(name rep) num])) 
        (into {}))])) 
      (into (sorted-map)))])) 
    (into {})))
  
  
(defn tokyo-write-jackson [graph db-pathname & [progress]]
  (let [db (tc/db-init {:path db-pathname :create true :dump #(.getBytes %)})
    progress (or progress 10000)
	  ]
		(tc/db-open db)
		(doseq [[[user days] i] (map vector graph (iterate inc 0))]
		  (when (zero? (mod i progress)) (err "."))
      ;; TODO can sort days for sure while at it
		  (tc/db-add db user (json/generate-string days)))
	(tc/db-close db)))
	
	
(defn tokyo-read-jackson [db-pathname & [progress]]
  (let [
    progress (or progress 10000)
    init-params {:path db-pathname :read-only true}
    db (tc/db-init init-params)
    _ (tc/db-open db)
    tc (:db db) 
    r (when (.iterinit tc) 
      (loop [k (.iternext2 tc) res [] i 0] 
        (if (empty? k) res 
          (do (when (and progress (zero? (mod i progress))) (err "."))  
            (recur (.iternext2 tc) (conj 
              res [k (json/parse-string (jiraph.tc/db-get db k))]) (inc i))))))]
  (tc/db-close db)
  (into {} r)))
