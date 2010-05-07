(require '[cupboard.bdb.je :as je])
(require '[cupboard.bdb.je-marshal :as jem])
(import '[com.sleepycat.je DatabaseEntry]) 

(defn err [ & args]
  (doto System/err (.print   (apply str args)) .flush))

(defn errln [ & args]
  (doto System/err (.println (apply str args)) .flush))
  
(defstruct id-chunk :id :chunk)

;; (def *num-agents* 8)
;; (def *bdb-agents* (map #(agent (struct id-chunk % [])) (range *num-agents*)))
  
(defn reset-agents [agents]
  (let [agents (map #(send % (fn [_] nil)) agents)]
  (apply await agents)
  (err "agents reset")))
  
(defn end-positions [n m]
  "break the sequence 0..n into m integral pieces
  and list their starting points" 
  (let [chunk (int (/ n m))] 
    (loop [r [] prev 0 curr chunk] 
      (if (> curr n) 
        (conj r (dec n))
        (recur (conj r prev) curr (+ curr chunk))))))
        
(defn do-range [old-agt-val db [begin beyond] & [progress]]
  (let [
        {:keys [id chunk]} old-agt-val
        dbe-key  (DatabaseEntry.)
        dbe-data (DatabaseEntry.)
       ]
    (errln "agent " id " doing range [" begin ", " beyond ")")  ; ] 
    (je/with-db-cursor [curs db]
      ;; NB does this return a pair already or just places the cursor?
        (loop [res (transient [])
               the-pair (je/db-cursor-search curs begin) 
               i 0]
               (let [the-key (first the-pair)]
                  (when (and progress (= 0 (mod i progress))) (err id)) 
                  (if (or (nil? the-key) (>= (compare the-key beyond) 0))
                    (do 
                      (err "agent " id " produced a chunk of length " (count res))
                      (struct id-chunk id (persistent! res)))
                    (recur (conj! res the-pair) 
                           (je/db-cursor-next curs :key dbe-key :data dbe-data)
                           (inc i))))))))

;; TODO instead of getting all numbered-keys, first fetch their count only,
;; compute endpoints from index range, then get the corresponding keys only
(defn agents-get-db [db-env db-name num-agents & [get-vector progress]]
  (let [
      db-keys-name (str db-name "-keys")
      end-keys     (je/with-db [db-keys db-env db-keys-name :read-only true] (let [
                      num-keys  (je/db-count db-keys)
                      pos-ends  (end-positions num-keys num-agents)]
                    (doall (map (comp second (partial je/db-get db-keys)) pos-ends))))
      key-ranges   (partition 2 1 end-keys)
      into-what    (if get-vector [] {})
    ]
    (assert (= (count key-ranges) num-agents))
    (je/with-db [db db-env db-name :read-only true]
      (errln "starting parallel agent-get of db " db-name " with " num-agents " agents")
      (let [agents (map #(agent (struct id-chunk % [])) (range num-agents))
            agents (map #(send %1 do-range db %2 progress) agents key-ranges)]
        (apply await agents)
        (errln " finished agents!")
        (reduce #(into %1 (:chunk @%2)) into-what agents)
        ))))
    
    
(defn bdb-put-seq [s db-env db-name]
  (err "writing db " db-name "... ")
  (je/with-db [db db-env db-name :allow-create true]
    (doseq [[k v] s] (je/db-put db k v)))
  (errln "done."))

(defn bdb-put-seq-cursor [s db-env db-name]
  (err "writing db " db-name "... ")
  (je/with-db [db db-env db-name :allow-create true]
    (je/with-db-cursor [curs db]
      (doseq [[k v] s] (je/db-cursor-put curs k v))))
  (errln "done."))
  
(defn put-db [s db-env db-main-name]
  (bdb-put-seq s db-env db-main-name)
  (let [db-keys-name (str db-main-name "-keys")
    the-keys (if (map? s) (keys s) (map first s))
    sorted-keys (sort the-keys)
    numbered-keys (map vector (iterate inc 0) sorted-keys)]
    (bdb-put-seq numbered-keys db-env db-keys-name)
    ))
 
;; (def je (je/db-env-open "je" :read-only true)) 
;; (.getDatabaseNames @(:env-handle je))
;; (time (def *numbered-keys* (get-numbered-keys je "dments-keys")))
;; (def bdb-g1 (je/db-open je "g1" :read-only true))
;; (.count @(:db-handle bdb-g1))
;; (->> (do-range (struct id-chunk 99 []) bdb-g1 ["0" "00023"] 2) :chunk)
;; (time (def dments (do-ranges bdb-g1 *numbered-keys* *bdb-agents* 10000)))


(defn bdb-put-triples [graph db-env db-name]
  (err "writing trilpes db " db-name "...")
  (je/with-db [db db-env db-name :allow-create true]
    (doseq [[k1 v1] graph]
      (doseq [[k2 v2] v1] 
        (je/db-put db [k1 k2] v2))))
  (errln "done."))


(defn bdb-put-triple-keys [graph db-env db-name]
  (err "writing trilpe keys db " db-name "...")
  (je/with-db [db db-env db-name :allow-create true]
    (reduce (fn [i [k1 v1]] (reduce (fn [i [k2 _]]
        (je/db-put db i [k1 k2]) (inc i)) i v1))
        0 graph)) 
  (errln "done."))

    
(defn put-triples-db [graph db-env db-main-name]
  (bdb-put-triples graph db-env db-main-name)
  (bdb-put-triple-keys graph db-env (str db-main-name "-keys")))

(defn get-triples-db [num-agents db-env db-main-name & [progress]]
  (let [triples (agents-get-db num-agents db-env db-main-name progress :get-vector)]
    (->> triples
      (reduce (fn [[res [[k1 k2] v]]] (let [m (res k1)] 
        (assoc! res k1 
          (if m (update-in m [k2] #(conj (or % []) v)) 
                (hash-map k1 (hash-map k2 v)))))) 
          (transient {}))
      persistent!)))
    
