;; TODO make it a defn or a macro already
;; make grane optional

(use [clojure.contrib.seq-utils partition-all])

(defn mongo-store-pairs 
  "store a 2-tuple collection in mongo as map-pairs with given key and value name, in a given collection"
  [coll coll-name key-name val-name & quant]
    (let [quant (or quant 10000)] 
    (doseq [x (partition-all quant (map (fn [[k v]] {key-name k val-name v}) coll))] 
    (.print System/err ".") (mass-insert! coll-name x)))
    (.println System/err))

;; from rank-reps, replaced by mongo-store-pairs
(time 
  (doseq [x (partition-all 10000 
    (map (fn [[user score]] { :user user :score score }) pg-desc)
      )] 
    (.print System/err ".")
    (mass-insert! :pagerank x)))
(.println System/err)

;; from fans, replaced by mongo-store-pairs
(def put-sper []
  "save sper -- inverse of reps, graph of those who mention me"
  (doseq [x (partition-all 10000 
    (map (fn [[k v]] { :user k :sper v }) 
      sper))] 
    (.print System/err ".")
    (mass-insert! :sper x))
(.println System/err))