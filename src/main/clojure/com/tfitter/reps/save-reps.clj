;; TODO make it a defn or a macro already
;; make grane optional

;; (use [clojure.contrib.seq-utils partition-all])

(defn partition-all
  "Returns a lazy sequence of lists like clojure.core/partition, but may
  include lists with fewer than n items at the end."
  ([n coll]
     (partition-all n n coll))
  ([n step coll]
     (lazy-seq
      (when-let [s (seq coll)]
        (cons (take n s) (partition-all n step (drop step s)))))))
        
(defn mongo-store-pairs 
  "store a 2-tuple collection in mongo as map-pairs with given key and value name, in a given collection"
  [coll coll-name key-name val-name & [unsort quant]]
    (let [quant (or quant 10000)] 
    (doseq [x (partition-all quant (map (fn [[k v]]
      (let [v (if unsort (into {} v) v)] 
      {key-name k val-name v})) coll))] 
    (.print System/err ".") (mass-insert! coll-name x)))
    (.println System/err))
