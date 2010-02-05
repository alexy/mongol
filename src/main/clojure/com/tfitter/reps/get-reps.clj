(ns mongol.repliers
  (:use [somnium.congomongo]
      ;; [incanter core stats charts]
      [clojure.contrib.seq-utils :only [partition-all]])
  (:import [org.joda.time DateTime]))

(mongo! :db "twitter")


;; originally, I've created reps as a simple mentions graph from semi-raw JSON twits -- where only the user block 
;; was replaced by user_id and screen_name fields.  However, it turned out that the screen_name is often in different case,
;;even in the from position as generated by Streaming API 
;; (about 6415 difgferences for the October-November 2009 100Mtwits gardenhose dataset.)
;; Thus I set about to lowercase all from's, merging their repliers maps, and lowercase all to's inside each replier map,
;;  merging their time series in a sorted order.
;; A much simpler approach is to re-create reps from JSON in a single pass, lowercasing both from and to.
;; However merging adjacency list is a useful transformation for the future use.
;; One point to keep in mind is that congomongo creates outer keys as strings, while preserving inner ones as keywords.
;; This can be either used to differentiate the from from the to, or unified back to strings-only node names. 

;; TODO unify "from" and :to, or keep formats intentionally different
;; for directed edges?
;; get-reps simply fetches the collection as is

(defn get-reps  
  "fetch replier graph, progress dot every quant heads, does not lowercase nor merge"
    [coll user-key reps-key & [quant]]
  (let [quant (or quant 10000)
  result (persistent! 
  ((reduce (fn [[m n] {from user-key reps reps-key}]
      (if (= (mod n quant) 0) (.print System/err (format " %d" (quot n quant))))     
      [(assoc! m from reps) (inc n)])
    [(transient {}) 0] (fetch coll)) 0) )]
  (.println System/err)
  result))

(defn sort-diff-map
  "convert a map to a sorted map and subtract each prev val from next"
  [m & [unkeyword diff]]
  (let [
    unstring (= unkeyword :unstring)
    s
    (if unkeyword
        (let [k (->> m keys (map #(Integer/parseInt (if unstring % (name %)))))
              v (vals m)]
        (interleave k v))
        (apply concat m))
    sm (apply sorted-map s)]
    (if diff
      (let [k  (keys sm)  ; TODO can we split both at the same time, e,g, unzip?
      v  (vals sm)
      dv (into [(first v)] (map - (rest v) v))]
      (->> (interleave k dv) 
        (partition 2)
        (filter (fn [[_ y]] (> y 0)))
        (apply concat)
        (apply sorted-map)))
        sm  ; not diff
        )))

(defn sort-diff-reps
  "for graphs of the form {:user u :reps {reps-map}}, replace the hash-map reps with a sorted-map,
  and if diff is true, also replace vals by differences"
  [reps & [unkeyword diff]]
  ;; TODO pmap below runs twice slower; replace into {} with something?
  ;; ->> ... concat (apply hash-map)
  (->> reps (map (fn [[user reps]] [user (sort-diff-map reps unkeyword diff)])) 
    (into {})
    ;; (apply concat) (apply hash-map)
    ))
    
  
(defn get-reps-sorted  
  "fetch replier graph, progress dot every quant heads
  reps will ne converted to a sorted-map, 
  when diff is true, subtract preceding value from each next reps
  TODO: sort-diff-map or sort-diff-reps for different graphs!"
  [coll user-key reps-key & [unkeyword diff nested quant]]
  (let [quant (or quant 10000)
  result (->> (fetch coll)
  (reduce (fn [[m n] {from user-key reps reps-key}]
      (if (= (mod n quant) 0) (.print System/err (format " %d" (quot n quant)))) 
      (let [ 
        ;; params [reps unkeyword diff] 
        ;; ... (apply sort-diff-reps params) -- slower?
            reps   (if nested (sort-diff-reps reps unkeyword diff) 
                              (sort-diff-map  reps unkeyword diff))]
           [(assoc! m from reps) (inc n)]))
    [(transient {}) 0]) first persistent!)]
  (.println System/err)
  result))

;; from sorted-seq-merge.clj
(defn sorted-date-seq-merge
  "Lazily merge two sorted seqs into a single sorted seq"
  [a b]
  (lazy-seq
    (cond
      (empty? a) b
      (empty? b) a
      (< (.compareTo (first a) (first b)) 0) (cons (first a) (sorted-date-seq-merge (rest a) b))
      :else                   (cons (first b) (sorted-date-seq-merge a (rest b))))))
              
(defn lc-dt-reps
  "lowercase single from-to adjacency list; can also be used for time conversions, if needed"
  [reps]
  (reduce (fn [mres [to times]] 
    (let [
      to (.toLowerCase (name to)) 
      oldtimes (mres to)
      ;; TODO merge-sort the times when inserting into, or sort after into?
    newtimes (if oldtimes (sorted-date-seq-merge oldtimes times) times)] 
    ;; (println (str "to=>" to " newtimes=>" newtimes))
    (assoc mres to newtimes)))
    {} reps))
  
(defn merge-reps
  "merge two complete reps for the same from; reps are already lc-dt-res'ed"
  [a b]
  (reduce (fn [r [k v]] (let [has (r k)] 
  	(if has (update-in r [k] #(vec (sorted-date-seq-merge % v))) (assoc r k v)))) a b)
  )
  
;; TODO could have replaced mongo fetching by in-RAM processing of get-reps'ed reps,
;; since I check the total leaf count in both anyways
(defn get-lc-reps  
  "fetch replier graph, progress dot every quant heads, lowercase"
    [coll & args]
  (let [{user-key :user-key, reps-key :reps-key, quant :quant, limit :take 
         :or {user-key :user reps-key :reps quant 10000}} (apply hash-map args)
  fetch-reps (fetch :reps)       
  fetch-reps (if limit (take limit fetch-reps) fetch-reps)
  result (persistent! 
  ((reduce (fn [[m n] {from user-key reps reps-key}]
  	(let [from-lc (.toLowerCase from)
  		lc-reps (lc-dt-reps reps)
  		has (m from-lc)]
      (if (= (mod n quant) 0) (.print System/err (format " %d" (quot n quant))))     
      [(assoc! m from-lc (if has (merge-reps has lc-reps) lc-reps)) (inc n)]))
    [(transient {}) 0] fetch-reps) 0))]
  (.println System/err)
  result))

;; lowercase reps in memory
;; TODO can ge unified with get-lc-reps with a different arity
(defn mem-lc-reps  
  "lowercase a replier graph in-memory, progress dot every quant heads"
    [reps & args]
  (let [{quant :quant, limit :take :or {quant 10000}} (apply hash-map args)
  result (persistent! 
  ((reduce (fn [[m n] [from reps]]
  	(let [from-lc (.toLowerCase from)
  		lc-reps (lc-dt-reps reps)
  		has (m from-lc)]
      (if (= (mod n quant) 0) (.print System/err (format " %d" (quot n quant))))     
      [(assoc! m from-lc (if has (merge-reps has lc-reps) lc-reps)) (inc n)]))
    [(transient {}) 0] reps) 0))]
  (.println System/err)
  result))

;; fetch a collection from mongo without _ns and _id
(defn get-mongo-less [coll-name] (map #(dissoc % :_ns :_id) (fetch coll-name)))
;; (defn sper-db [] (get-coll :sper))

(defn get-coll-pairs
  "get a collection of maps like {:user u :data d} as seq of tuples [user pair]"x
  [coll-name key-name val-name]
  (map (fn [{key key-name val val-name}] [key val]) 
    (get-mongo-less coll-name)))


(defn get-joda-reps 
  "get a reps graph from mongo, replacing juDate with joda DateTime"
  [coll-name user-key reps-key] 
  (map (fn [{user user-key reps reps-key}]
    (let [joreps (map (fn [[from dates]] (let [jodates (map #(DateTime. %) dates)]
            [from jodates])) reps)]
        [user joreps])) (get-coll coll-name)))

;; (def sper (get-joda-reps :sper))
