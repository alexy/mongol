(use '[cupboard.bdb.je :as je])
(use '[cupboard.bdb.je-marshal :as jem])
(import '[com.sleepycat.je DatabaseEntry]) 

(def *num-agents* 8)

(defstruct id-chunk :id :chunk)

(def *bdb-agents* (map #(agent (struct id-chunk % [])) (take *num-agents* (iterate inc 0))))
  
(defn reset-agents [agents]
  (map #(send % (fn [_] nil)) agents)
  (apply await agents)
  (err "agents reset"))
  
(defn end-positions [n m]
  "break the sequence 0..n into m integral pieces
  and list their starting points" 
  (let [chunk (int (/ n m))] 
    (loop [r [] prev 0 curr chunk] 
      (if (> curr n) 
        (conj r n)
        (recur (conj r prev) curr (+ curr chunk))))))
        
(defn do-range [old-agt-val db [begin beyond] & [progress]]
  (let [
        {:keys [id chunk]} old-agt-val
        dbe-key  (DatabaseEntry.)
        dbe-data (DatabaseEntry.)
       ]
    (je/with-db-cursor [curs db]
      ;; NB does this return a pair already or just places the cursor?
        (loop [res (transient [])
               the-pair (je/db-cursor-search curs begin) 
               i 0]
               (let [the-key (first the-pair)]
                  (when (and progress (= 0 (mod i progress))) (print id)) 
                  (if (or (nil? the-key) (>= the-key beyond))
                    (struct id-chunk id (persistent! res))
                    (recur (conj! res the-pair) 
                           (je/db-cursor-next curs :key dbe-key :data dbe-data) 
                           (inc i))))))))
  
(defn do-ranges [db-name db-env numbered-keys agents & [progress]]
  (let [
      total-keys   (count numbered-keys)
      total-agents (count agents)
      pos-ends     (end-positions total-keys total-agents)
      key-ends     (map #(numbered-keys %) pos-ends)
      key-ranges   (partition 2 1 key-ends)
      _            (assert (= (count key-ranges) total-agents))
      ;; agents       
    ]
    (je/with-db [db db-env db-name]
      (err (str "starting parallel agent-get of db " db-name))
      (map #(send %1 do-range db %2 progress) agents key-ranges)
      (println (str "started " total-agents " agents"))
      (apply await agents)
      (println "finished agents!")
      (reduce #(into %1 (:chunk @%2)) {} agents))))
    
(defn get-numbered-keys [db-env db-name]
  (je/with-db [db db-env db-name]
    (err (str "getting numbered keys from db " db-name))
    (let [resvec
      (je/with-db-cursor [c db]
        (loop [res (transient []) the-pair (je/db-cursor-next c)]
          (if (empty? the-pair)
            (persistent! res)
            (recur (conj! res the-pair) (je/db-cursor-next c))
            )))]
      (into {} resvec))))
      
(defn agents-get-db [agents db-env db-main-name & [progress]]
  (let [db-keys-name (str (db-main-name "-keys"))
        numbered-keys (get-numbered-keys db-env db-keys-name)]
    (reset-agents agents)
    (do-ranges db-main-name db-env numbered-keys agents progress)))

    